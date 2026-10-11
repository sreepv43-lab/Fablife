package io.github.sreepv43.fablife.data

import io.github.sreepv43.fablife.content.Goal
import io.github.sreepv43.fablife.content.Journey
import io.github.sreepv43.fablife.content.Ritual
import io.github.sreepv43.fablife.content.Step
import io.github.sreepv43.fablife.engine.JourneyRules
import io.github.sreepv43.fablife.engine.logicalDay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class FabRepository(
    private val db: FabDatabase,
    val prefsRepo: PrefsRepository,
    private val content: ContentRepository,
    private val onRemindersChanged: suspend () -> Unit,
) {
    private val mutex = Mutex()

    val habits = db.habits().observeAll()
    val checkIns = db.checkIns().observeAll()
    val steps = db.steps().observeAll()
    val ritualSettings = db.ritualSettings().observeAll()
    val routineLogs = db.routineLogs().observeAll()
    val journal = db.journal().observeAll()
    val topTasks = db.topTasks().observeAll()
    val sessionLogs = db.sessionLogs().observeAll()
    val prefs = prefsRepo.prefs

    /** The journey the user is working on; falls back to the first one if the stored id is unknown. */
    private suspend fun activeJourney(): Journey = content.journey(prefsRepo.current().activeJourneyId) ?: content.journeys.first()

    suspend fun today(): LocalDate = logicalDay(LocalDateTime.now(), prefsRepo.current().dayStartOffsetHours)

    suspend fun completeOnboarding(transform: (UserPrefs) -> UserPrefs) = mutex.withLock {
        val chosen = transform(prefsRepo.current())
        val journey = content.journeyForGoal(chosen.goal)
        val p = chosen.copy(activeJourneyId = journey.id)
        // Journey and rituals must exist before the flag flips, so the main screen never sees a half-set-up app.
        db.ritualSettings().upsert(
            listOf(
                RitualSettingsEntity(Ritual.MORNING, p.wakeTime.plusMinutes(10), true),
                RitualSettingsEntity(Ritual.AFTERNOON, LocalTime.of(14, 0), true),
                RitualSettingsEntity(Ritual.EVENING, p.bedTime.minusHours(1), true),
            ),
        )
        ensureStepRowsLocked(journey)
        prefsRepo.update { p.copy(onboardingCompleted = true) }
        onRemindersChanged()
    }

    /** Creates the step rows of a journey the first time it is started: the first step unlocked, the rest locked. */
    private suspend fun ensureStepRowsLocked(journey: Journey) {
        val existing = db.steps().all().map { it.stepId }.toSet()
        val missing = journey.steps.filter { it.id !in existing }
        if (missing.isEmpty()) return
        val untouched = journey.steps.none { it.id in existing }
        db.steps().upsert(
            missing.map { step ->
                StepProgressEntity(step.id, journey.id, if (untouched && step == journey.steps.first()) StepState.UNLOCKED else StepState.LOCKED)
            },
        )
    }

    /** Starts a journey (or switches back to one already started). Progress in every journey is kept. */
    suspend fun startJourney(id: String) = mutex.withLock {
        val journey = content.journey(id) ?: return@withLock
        ensureStepRowsLocked(journey)
        prefsRepo.update { it.copy(activeJourneyId = journey.id) }
    }

    /** User accepted a step from its letter: the habit joins its ritual and the challenge starts today. */
    suspend fun acceptStep(stepId: String) = mutex.withLock {
        val step = content.journeyOfStep(stepId)?.steps?.first { it.id == stepId } ?: return@withLock
        (step.goal as? Goal.Habit)?.let { insertJourneyHabit(step, it) }
        val progress = db.steps().all().first { it.stepId == stepId }
        if (progress.state == StepState.UNLOCKED) {
            db.steps().upsert(listOf(progress.copy(state = StepState.ACTIVE, letterRead = true, activatedDay = today())))
        }
    }

    private suspend fun insertJourneyHabit(step: Step, goal: Goal.Habit) {
        val h = goal.habit
        db.habits().insert(
            HabitEntity(
                id = h.id, title = h.title, description = h.description, icon = h.icon, ritual = h.ritual,
                order = (db.habits().all().filter { it.ritual == h.ritual }.maxOfOrNull { it.order } ?: 0) + 1,
                durationSeconds = h.durationSeconds, smallerVersion = h.smallerVersion,
                sourceStepId = step.id, createdAtMillis = System.currentTimeMillis(),
                toolType = h.tool?.type?.name, toolRef = h.tool?.ref,
            ),
        )
    }

    /** Sets (or clears, with null) a check-in, then returns the step this completed, if any. */
    suspend fun setCheckIn(habitId: String, day: LocalDate, status: CheckInStatus?): Step? = mutex.withLock {
        if (status == null) db.checkIns().delete(habitId, day)
        else db.checkIns().upsert(CheckInEntity(habitId, day, status, System.currentTimeMillis()))
        evaluateLocked()
    }

    suspend fun completeAction(stepId: String): Step? = mutex.withLock { completeStepLocked(stepId) }

    /** "Skip ahead": keeps the habit (if any) in the ritual but unlocks the next step now. */
    suspend fun skipStep(stepId: String): Step? = mutex.withLock {
        val step = content.journeyOfStep(stepId)?.steps?.first { it.id == stepId } ?: return@withLock null
        (step.goal as? Goal.Habit)?.let { insertJourneyHabit(step, it) }
        completeStepLocked(stepId)
    }

    private suspend fun evaluateLocked(): Step? {
        val journey = activeJourney()
        val ids = journey.steps.map { it.id }.toSet()
        val active = db.steps().all().firstOrNull { it.stepId in ids && it.state == StepState.ACTIVE } ?: return null
        val activatedDay = active.activatedDay ?: return null
        val step = journey.steps.first { it.id == active.stepId }
        val (target, window) = when (val goal = step.goal) {
            is Goal.Habit -> goal.targetDays to goal.windowDays
            is Goal.RitualChallenge -> goal.targetDays to goal.windowDays
            is Goal.Action -> return null
        }
        val done = doneDaysFor(step, db.habits().all(), db.checkIns().all())
        return if (JourneyRules.isChallengeComplete(done, activatedDay, target, window)) completeStepLocked(step.id) else null
    }

    private suspend fun completeStepLocked(stepId: String): Step? {
        val journey = content.journeyOfStep(stepId) ?: return null
        val all = db.steps().all().associateBy { it.stepId }
        val current = all[stepId] ?: return null
        if (current.state == StepState.COMPLETED) return null
        val index = journey.steps.indexOfFirst { it.id == stepId }
        val updates = mutableListOf(current.copy(state = StepState.COMPLETED, letterRead = true, completedDay = today()))
        journey.steps.getOrNull(index + 1)?.let { next ->
            all[next.id]?.takeIf { it.state == StepState.LOCKED }?.let { updates += it.copy(state = StepState.UNLOCKED) }
        }
        db.steps().upsert(updates)
        return journey.steps[index]
    }

    /** On app open: after 7+ days away the active challenge restarts (completed steps are kept). */
    suspend fun onAppOpen() = mutex.withLock {
        val p = prefsRepo.current()
        if (!p.onboardingCompleted) return@withLock
        syncHabitToolsLocked()
        val activeIds = activeJourney().steps.map { it.id }.toSet()
        val lastCheckIn = db.checkIns().all().filter { it.status == CheckInStatus.DONE }.maxOfOrNull { it.day }
        val active = db.steps().all().firstOrNull { it.stepId in activeIds && it.state == StepState.ACTIVE }
        val today = today()
        if (active?.activatedDay != null && lastCheckIn != null && !active.activatedDay.isAfter(lastCheckIn) &&
            JourneyRules.isWelcomeBack(lastCheckIn, today)
        ) {
            db.steps().upsert(listOf(active.copy(activatedDay = today)))
            prefsRepo.update { it.copy(welcomeBackPending = true) }
        }
    }

    /** Journey habits created before tools existed (or by an older app version) pick up their tool from content. */
    private suspend fun syncHabitToolsLocked() {
        val templates = content.journeys.flatMap { j -> j.steps.mapNotNull { (it.goal as? Goal.Habit)?.habit } }.associateBy { it.id }
        val stale = db.habits().all().filter { it.sourceStepId != null && it.toolType == null && templates[it.id]?.tool != null }
        if (stale.isEmpty()) return
        db.habits().upsert(stale.map { h -> templates.getValue(h.id).tool!!.let { h.copy(toolType = it.type.name, toolRef = it.ref) } })
    }

    suspend fun addCustomHabit(title: String, ritual: Ritual) = mutex.withLock {
        val order = (db.habits().all().filter { it.ritual == ritual }.maxOfOrNull { it.order } ?: 0) + 1
        db.habits().insert(
            HabitEntity(
                id = "custom_${System.currentTimeMillis()}", title = title.trim(), description = null, icon = "custom",
                ritual = ritual, order = order, durationSeconds = null, smallerVersion = null, sourceStepId = null,
                createdAtMillis = System.currentTimeMillis(),
            ),
        )
    }

    suspend fun deleteHabit(id: String) = mutex.withLock {
        db.habits().delete(id)
        db.checkIns().deleteForHabit(id)
    }

    suspend fun moveHabit(id: String, up: Boolean) = mutex.withLock {
        val habit = db.habits().all().first { it.id == id }
        val siblings = db.habits().all().filter { it.ritual == habit.ritual }.sortedBy { it.order }.toMutableList()
        val i = siblings.indexOfFirst { it.id == id }
        val j = if (up) i - 1 else i + 1
        if (j !in siblings.indices) return@withLock
        siblings.add(j, siblings.removeAt(i))
        db.habits().upsert(siblings.mapIndexed { index, h -> h.copy(order = index + 1) })
    }

    suspend fun setHabitRitual(id: String, ritual: Ritual) = mutex.withLock {
        val habit = db.habits().all().first { it.id == id }
        val order = (db.habits().all().filter { it.ritual == ritual }.maxOfOrNull { it.order } ?: 0) + 1
        db.habits().upsert(listOf(habit.copy(ritual = ritual, order = order)))
    }

    suspend fun setUseSmaller(id: String, useSmaller: Boolean) = mutex.withLock {
        val habit = db.habits().all().first { it.id == id }
        db.habits().upsert(listOf(habit.copy(useSmaller = useSmaller)))
    }

    suspend fun updateRitualSettings(settings: RitualSettingsEntity) {
        db.ritualSettings().upsert(listOf(settings))
        onRemindersChanged()
    }

    suspend fun logRoutine(routineId: String, minutes: Int) {
        db.routineLogs().insert(RoutineLogEntity(routineId = routineId, day = today(), minutes = minutes))
    }

    suspend fun logSession(kind: SessionKind, ref: String, minutes: Int) {
        if (minutes <= 0) return
        db.sessionLogs().insert(SessionLogEntity(kind = kind, ref = ref, day = today(), minutes = minutes))
    }

    suspend fun addJournalEntry(kind: JournalKind, text: String, mood: Int? = null): Long {
        val clean = text.trim()
        require(clean.isNotEmpty() || mood != null) { "A journal entry needs text or a mood" }
        return db.journal().insert(JournalEntryEntity(day = today(), kind = kind, mood = mood, text = clean, createdAtMillis = System.currentTimeMillis()))
    }

    suspend fun deleteJournalEntry(id: Long) = db.journal().delete(id)

    /** Writes (or, with blank text, clears) one of today's three Top 3 slots. */
    suspend fun setTopTask(day: LocalDate, slot: Int, text: String) {
        require(slot in 0..2) { "Top 3 slots are 0..2" }
        val clean = text.trim()
        if (clean.isEmpty()) db.topTasks().delete(day, slot) else {
            val existing = db.topTasks().all().firstOrNull { it.day == day && it.slot == slot }
            db.topTasks().upsert(TopTaskEntity(day, slot, clean, existing?.done ?: false))
        }
    }

    suspend fun setTopTaskDone(day: LocalDate, slot: Int, done: Boolean) {
        val existing = db.topTasks().all().firstOrNull { it.day == day && it.slot == slot } ?: return
        db.topTasks().upsert(existing.copy(done = done))
    }

    suspend fun exportJson(): String {
        val p = prefsRepo.current()
        fun str(v: Any?) = JsonPrimitive(v?.toString())
        val root = JsonObject(
            mapOf(
                "app" to str("Fab Life"),
                "exportedAt" to str(LocalDateTime.now()),
                "profile" to JsonObject(
                    mapOf(
                        "name" to str(p.name), "goal" to str(p.goal), "diet" to str(p.dietType),
                        "gentleStomach" to JsonPrimitive(p.gentleStomach), "fitnessLevel" to str(p.fitnessLevel),
                        "movementNeeds" to str(p.movementNeeds), "wakeTime" to str(p.wakeTime), "bedTime" to str(p.bedTime),
                    ),
                ),
                "habits" to JsonArray(db.habits().all().map {
                    JsonObject(mapOf("id" to str(it.id), "title" to str(it.title), "ritual" to str(it.ritual), "order" to JsonPrimitive(it.order)))
                }),
                "checkIns" to JsonArray(db.checkIns().all().map {
                    JsonObject(mapOf("habitId" to str(it.habitId), "day" to str(it.day), "status" to str(it.status)))
                }),
                "journey" to JsonArray(db.steps().all().map {
                    JsonObject(mapOf("stepId" to str(it.stepId), "state" to str(it.state), "activatedDay" to str(it.activatedDay), "completedDay" to str(it.completedDay)))
                }),
                "routines" to JsonArray(db.routineLogs().all().map {
                    JsonObject(mapOf("routineId" to str(it.routineId), "day" to str(it.day), "minutes" to JsonPrimitive(it.minutes)))
                }),
                "sessions" to JsonArray(db.sessionLogs().all().map {
                    JsonObject(mapOf("kind" to str(it.kind), "ref" to str(it.ref), "day" to str(it.day), "minutes" to JsonPrimitive(it.minutes)))
                }),
                "journal" to JsonArray(db.journal().all().map {
                    JsonObject(mapOf("day" to str(it.day), "kind" to str(it.kind), "mood" to str(it.mood), "text" to str(it.text)))
                }),
                "top3" to JsonArray(db.topTasks().all().map {
                    JsonObject(mapOf("day" to str(it.day), "slot" to JsonPrimitive(it.slot), "text" to str(it.text), "done" to JsonPrimitive(it.done)))
                }),
            ),
        )
        return root.toString()
    }

    suspend fun resetAll() = mutex.withLock {
        db.habits().clear()
        db.checkIns().clear()
        db.steps().clear()
        db.routineLogs().clear()
        db.journal().clear()
        db.topTasks().clear()
        db.sessionLogs().clear()
        db.coachNotes().clear()
        prefsRepo.clear()
        onRemindersChanged()
    }

    companion object {
        /** Days that count towards a step's challenge: habit done days, or full-ritual days. */
        fun doneDaysFor(step: Step, habits: List<HabitEntity>, checkIns: List<CheckInEntity>): Set<LocalDate> {
            val done = checkIns.filter { it.status == CheckInStatus.DONE }
            return when (val goal = step.goal) {
                is Goal.Habit -> done.filter { it.habitId == goal.habit.id }.map { it.day }.toSet()
                is Goal.RitualChallenge -> {
                    // Only the habits this journey added count, not custom habits or other journeys' habits.
                    val prefix = step.id.substringBefore('.') + "."
                    val ids = habits.filter { it.ritual == goal.ritual && it.sourceStepId?.startsWith(prefix) == true }.map { it.id }.toSet()
                    JourneyRules.fullRitualDays(ids, done.groupBy({ it.day }, { it.habitId }).mapValues { it.value.toSet() })
                }
                is Goal.Action -> emptySet()
            }
        }
    }
}
