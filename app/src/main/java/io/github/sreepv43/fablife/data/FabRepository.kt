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
    private val journey: Journey get() = content.journey

    val habits = db.habits().observeAll()
    val checkIns = db.checkIns().observeAll()
    val steps = db.steps().observeAll()
    val ritualSettings = db.ritualSettings().observeAll()
    val routineLogs = db.routineLogs().observeAll()
    val prefs = prefsRepo.prefs

    suspend fun today(): LocalDate = logicalDay(LocalDateTime.now(), prefsRepo.current().dayStartOffsetHours)

    suspend fun completeOnboarding(transform: (UserPrefs) -> UserPrefs) = mutex.withLock {
        val p = transform(prefsRepo.current())
        // Journey and rituals must exist before the flag flips, so the main screen never sees a half-set-up app.
        db.ritualSettings().upsert(
            listOf(
                RitualSettingsEntity(Ritual.MORNING, p.wakeTime.plusMinutes(10), true),
                RitualSettingsEntity(Ritual.AFTERNOON, LocalTime.of(14, 0), true),
                RitualSettingsEntity(Ritual.EVENING, p.bedTime.minusHours(1), true),
            ),
        )
        db.steps().upsert(
            journey.steps.mapIndexed { i, step ->
                StepProgressEntity(step.id, journey.id, if (i == 0) StepState.UNLOCKED else StepState.LOCKED)
            },
        )
        prefsRepo.update { p.copy(onboardingCompleted = true) }
        onRemindersChanged()
    }

    /** User accepted a step from its letter: the habit joins its ritual and the challenge starts today. */
    suspend fun acceptStep(stepId: String) = mutex.withLock {
        val step = journey.steps.first { it.id == stepId }
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
        val step = journey.steps.first { it.id == stepId }
        (step.goal as? Goal.Habit)?.let { insertJourneyHabit(step, it) }
        completeStepLocked(stepId)
    }

    private suspend fun evaluateLocked(): Step? {
        val active = db.steps().all().firstOrNull { it.state == StepState.ACTIVE } ?: return null
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
        val lastCheckIn = db.checkIns().all().filter { it.status == CheckInStatus.DONE }.maxOfOrNull { it.day }
        val active = db.steps().all().firstOrNull { it.state == StepState.ACTIVE }
        val today = today()
        if (active?.activatedDay != null && lastCheckIn != null && !active.activatedDay.isAfter(lastCheckIn) &&
            JourneyRules.isWelcomeBack(lastCheckIn, today)
        ) {
            db.steps().upsert(listOf(active.copy(activatedDay = today)))
            prefsRepo.update { it.copy(welcomeBackPending = true) }
        }
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
            ),
        )
        return root.toString()
    }

    suspend fun resetAll() = mutex.withLock {
        db.habits().clear()
        db.checkIns().clear()
        db.steps().clear()
        db.routineLogs().clear()
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
                    val ids = habits.filter { it.ritual == goal.ritual }.map { it.id }.toSet()
                    JourneyRules.fullRitualDays(ids, done.groupBy({ it.day }, { it.habitId }).mapValues { it.value.toSet() })
                }
                is Goal.Action -> emptySet()
            }
        }
    }
}
