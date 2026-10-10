package io.github.sreepv43.fablife.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.sreepv43.fablife.AppContainer
import io.github.sreepv43.fablife.content.Goal
import io.github.sreepv43.fablife.content.Journey
import io.github.sreepv43.fablife.content.Letter
import io.github.sreepv43.fablife.content.Ritual
import io.github.sreepv43.fablife.content.Step
import io.github.sreepv43.fablife.content.ToolType
import io.github.sreepv43.fablife.data.CheckInEntity
import io.github.sreepv43.fablife.data.CheckInStatus
import io.github.sreepv43.fablife.data.FabRepository
import io.github.sreepv43.fablife.data.HabitEntity
import io.github.sreepv43.fablife.data.JournalEntryEntity
import io.github.sreepv43.fablife.data.SessionLogEntity
import io.github.sreepv43.fablife.data.TopTaskEntity
import io.github.sreepv43.fablife.data.RitualSettingsEntity
import io.github.sreepv43.fablife.data.RoutineLogEntity
import io.github.sreepv43.fablife.data.StepProgressEntity
import io.github.sreepv43.fablife.data.StepState
import io.github.sreepv43.fablife.data.UserPrefs
import io.github.sreepv43.fablife.engine.JourneyRules
import io.github.sreepv43.fablife.engine.logicalDay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/** A journey step with its stored progress and challenge progress ("2 of 3 days"). */
data class StepView(val step: Step, val progress: StepProgressEntity, val doneCount: Int, val target: Int)

/** A journey with how far the user got in it, for the journey picker. */
data class JourneyStatus(val journey: Journey, val completedSteps: Int, val started: Boolean) {
    val complete: Boolean get() = started && completedSteps == journey.steps.size
}

data class AppState(
    val prefs: UserPrefs,
    val today: LocalDate,
    val activeJourney: Journey,
    val journeys: List<JourneyStatus>,
    val habits: List<HabitEntity>,
    val checkIns: List<CheckInEntity>,
    /** The steps of the **active** journey that have been started. */
    val steps: List<StepView>,
    val ritualSettings: Map<Ritual, RitualSettingsEntity>,
    val routineLogs: List<RoutineLogEntity>,
    val journal: List<JournalEntryEntity> = emptyList(),
    val topTasks: List<TopTaskEntity> = emptyList(),
    val sessionLogs: List<SessionLogEntity> = emptyList(),
) {
    val doneByDay: Map<LocalDate, Set<String>> = checkIns.filter { it.status == CheckInStatus.DONE }
        .groupBy({ it.day }, { it.habitId }).mapValues { it.value.toSet() }
    val skippedToday: Set<String> = checkIns.filter { it.day == today && it.status == CheckInStatus.SKIPPED }.map { it.habitId }.toSet()
    val doneToday: Set<String> = doneByDay[today].orEmpty()

    fun habitsFor(ritual: Ritual) = habits.filter { it.ritual == ritual }.sortedBy { it.order }

    fun streak(ritual: Ritual): Int =
        JourneyRules.currentStreak(JourneyRules.fullRitualDays(habitsFor(ritual).map { it.id }.toSet(), doneByDay), today)

    fun longestStreak(ritual: Ritual): Int =
        JourneyRules.longestStreak(JourneyRules.fullRitualDays(habitsFor(ritual).map { it.id }.toSet(), doneByDay))

    /** The step the user is working on: unread letter, active challenge or pending action. */
    val currentStep: StepView? = steps.firstOrNull { it.progress.state == StepState.UNLOCKED || it.progress.state == StepState.ACTIVE }
    val journeyComplete: Boolean = steps.isNotEmpty() && steps.all { it.progress.state == StepState.COMPLETED }
    val completedSteps: Int = steps.count { it.progress.state == StepState.COMPLETED }

    val showNudge: Boolean
        get() {
            val active = currentStep?.takeIf { it.progress.state == StepState.ACTIVE && it.step.goal !is Goal.Action } ?: return false
            val dismissed = prefs.nudgeDismissedDay
            if (dismissed != null && !dismissed.isBefore(today.minusDays(2))) return false
            return JourneyRules.needsNudge(doneByDay.keys, active.progress.activatedDay ?: return false, today)
        }

    /** Minutes moved this week (Monday start): routines, plus 5 minutes per movement-habit day without a routine. */
    val weeklyMinutes: Int
        get() {
            val monday = today.with(DayOfWeek.MONDAY)
            val logs = routineLogs.filter { !it.day.isBefore(monday) }
            val routineDays = logs.map { it.day }.toSet()
            val movementHabits = habits.filter { it.toolType == ToolType.ROUTINE.name }.map { it.id }.toSet()
            val habitDays = doneByDay.filter { (day, ids) -> !day.isBefore(monday) && ids.any { it in movementHabits } && day !in routineDays }.size
            return logs.sumOf { it.minutes } + habitDays * 5
        }

    val placeholders: Map<String, String>
        get() {
            val format = DateTimeFormatter.ofPattern("h:mm a")
            return mapOf(
                "name" to prefs.displayName,
                "wakeTime" to prefs.wakeTime.format(format),
                "bedTime" to prefs.bedTime.format(format),
            )
        }
}

sealed interface UiEvent {
    data class StepCompleted(val step: Step, val isLast: Boolean) : UiEvent
    data class RitualCompleted(val ritual: Ritual) : UiEvent
}

class AppViewModel(private val container: AppContainer) : ViewModel() {
    private val repo: FabRepository = container.repository
    val content = container.content
    private val dayTick = MutableStateFlow(0)

    private val _events = MutableSharedFlow<UiEvent>(extraBufferCapacity = 4)
    val events: SharedFlow<UiEvent> = _events

    val prefs: StateFlow<UserPrefs?> = repo.prefs.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val state: StateFlow<AppState?> = combine(
        listOf(repo.prefs, repo.habits, repo.checkIns, repo.steps, repo.ritualSettings, repo.routineLogs, repo.journal, repo.topTasks, repo.sessionLogs, dayTick),
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        val p = values[0] as UserPrefs
        val habits = values[1] as List<HabitEntity>
        val checkIns = values[2] as List<CheckInEntity>
        val allProgress = values[3] as List<StepProgressEntity>
        val progress = allProgress.associateBy { it.stepId }
        val today = logicalDay(LocalDateTime.now(), p.dayStartOffsetHours)
        val activeJourney = content.journey(p.activeJourneyId) ?: content.journeys.first()
        val journeys = content.journeys.map { j ->
            val rows = j.steps.mapNotNull { progress[it.id] }
            JourneyStatus(j, rows.count { it.state == StepState.COMPLETED }, started = rows.isNotEmpty())
        }
        val steps = activeJourney.steps.mapNotNull { step ->
            val pr = progress[step.id] ?: return@mapNotNull null
            val (target, window) = when (val g = step.goal) {
                is Goal.Habit -> g.targetDays to g.windowDays
                is Goal.RitualChallenge -> g.targetDays to g.windowDays
                is Goal.Action -> 1 to 1
            }
            val done = if (pr.activatedDay != null) {
                JourneyRules.challengeProgress(FabRepository.doneDaysFor(step, habits, checkIns), pr.activatedDay, target, window)
            } else 0
            StepView(step, pr, done, target)
        }
        @Suppress("UNCHECKED_CAST")
        AppState(
            prefs = p, today = today, activeJourney = activeJourney, journeys = journeys,
            habits = habits, checkIns = checkIns, steps = steps,
            ritualSettings = (values[4] as List<RitualSettingsEntity>).associateBy { it.ritual },
            routineLogs = values[5] as List<RoutineLogEntity>,
            journal = values[6] as List<JournalEntryEntity>,
            topTasks = values[7] as List<TopTaskEntity>,
            sessionLogs = values[8] as List<SessionLogEntity>,
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun onResume() {
        dayTick.value++
        viewModelScope.launch { repo.onAppOpen() }
    }

    /**
     * Letter ids: `intro`, `outro`, `missed`, `welcome` for the active journey; `intro:<journeyId>` and
     * `outro:<journeyId>` for a specific one; `gentle`; or a step id.
     */
    fun letter(id: String): Letter? {
        val active = content.journey(prefs.value?.activeJourneyId.orEmpty()) ?: content.journeys.first()
        return when {
            id == "intro" -> active.introLetter
            id == "outro" -> active.outroLetter
            id == "missed" -> active.missedDaysLetter
            id == "welcome" -> active.welcomeBackLetter
            id == "gentle" -> content.food.gentleGuidance
            id.startsWith("intro:") -> content.journey(id.removePrefix("intro:"))?.introLetter
            id.startsWith("outro:") -> content.journey(id.removePrefix("outro:"))?.outroLetter
            else -> content.journeyOfStep(id)?.steps?.firstOrNull { it.id == id }?.letter
        }
    }

    fun startJourney(id: String) = viewModelScope.launch { repo.startJourney(id) }

    fun completeOnboarding(transform: (UserPrefs) -> UserPrefs) = viewModelScope.launch { repo.completeOnboarding(transform) }

    fun updatePrefs(transform: (UserPrefs) -> UserPrefs) = viewModelScope.launch { container.prefs.update(transform) }

    fun setGentle(on: Boolean, today: LocalDate) = updatePrefs {
        it.copy(gentleStomach = on, gentleSince = if (on) today else null, gentleTipDismissed = false)
    }

    fun acceptStep(stepId: String) = viewModelScope.launch { repo.acceptStep(stepId) }

    fun completeAction(stepId: String) = viewModelScope.launch { repo.completeAction(stepId)?.let(::emitStepCompleted) }

    fun skipStep(stepId: String) = viewModelScope.launch { repo.skipStep(stepId)?.let(::emitStepCompleted) }

    private fun emitStepCompleted(step: Step) {
        val journey = content.journeyOfStep(step.id)
        _events.tryEmit(UiEvent.StepCompleted(step, journey?.steps?.last()?.id == step.id))
    }

    /** Toggles a habit done/undone for [day] and emits celebrations. */
    fun toggleHabit(habitId: String, day: LocalDate) = setCheckIn(habitId, day, done = state.value?.doneByDay?.get(day)?.contains(habitId) != true)

    fun setCheckIn(habitId: String, day: LocalDate, done: Boolean) = viewModelScope.launch {
        val before = state.value
        val completed = repo.setCheckIn(habitId, day, if (done) CheckInStatus.DONE else null)
        if (done && before != null && day == before.today) {
            val habit = before.habits.firstOrNull { it.id == habitId }
            if (habit != null) {
                val ids = before.habitsFor(habit.ritual).map { it.id }
                if ((before.doneToday + habitId).containsAll(ids)) _events.tryEmit(UiEvent.RitualCompleted(habit.ritual))
            }
        }
        completed?.let(::emitStepCompleted)
    }

    fun skipToday(habitId: String, day: LocalDate) = viewModelScope.launch { repo.setCheckIn(habitId, day, CheckInStatus.SKIPPED) }

    fun addHabit(title: String, ritual: Ritual) = viewModelScope.launch { repo.addCustomHabit(title, ritual) }
    fun deleteHabit(id: String) = viewModelScope.launch { repo.deleteHabit(id) }
    fun moveHabit(id: String, up: Boolean) = viewModelScope.launch { repo.moveHabit(id, up) }
    fun setHabitRitual(id: String, ritual: Ritual) = viewModelScope.launch { repo.setHabitRitual(id, ritual) }
    fun setUseSmaller(id: String, value: Boolean) = viewModelScope.launch { repo.setUseSmaller(id, value) }
    fun updateRitualSettings(settings: RitualSettingsEntity) = viewModelScope.launch { repo.updateRitualSettings(settings) }
    fun logRoutine(routineId: String, minutes: Int) = viewModelScope.launch { repo.logRoutine(routineId, minutes) }
    fun logSession(kind: io.github.sreepv43.fablife.data.SessionKind, ref: String, minutes: Int) = viewModelScope.launch { repo.logSession(kind, ref, minutes) }
    fun addJournalEntry(kind: io.github.sreepv43.fablife.data.JournalKind, text: String, mood: Int? = null) = viewModelScope.launch { repo.addJournalEntry(kind, text, mood) }
    fun deleteJournalEntry(id: Long) = viewModelScope.launch { repo.deleteJournalEntry(id) }
    fun setTopTask(day: LocalDate, slot: Int, text: String) = viewModelScope.launch { repo.setTopTask(day, slot, text) }
    fun setTopTaskDone(day: LocalDate, slot: Int, done: Boolean) = viewModelScope.launch { repo.setTopTaskDone(day, slot, done) }
    suspend fun exportJson(): String = repo.exportJson()
    fun resetAll() = viewModelScope.launch { repo.resetAll() }
}
