package io.github.sreepv43.fablife.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.sreepv43.fablife.AppContainer
import io.github.sreepv43.fablife.content.Goal
import io.github.sreepv43.fablife.content.Letter
import io.github.sreepv43.fablife.content.Ritual
import io.github.sreepv43.fablife.content.Step
import io.github.sreepv43.fablife.data.CheckInEntity
import io.github.sreepv43.fablife.data.CheckInStatus
import io.github.sreepv43.fablife.data.FabRepository
import io.github.sreepv43.fablife.data.HabitEntity
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

data class AppState(
    val prefs: UserPrefs,
    val today: LocalDate,
    val habits: List<HabitEntity>,
    val checkIns: List<CheckInEntity>,
    val steps: List<StepView>,
    val ritualSettings: Map<Ritual, RitualSettingsEntity>,
    val routineLogs: List<RoutineLogEntity>,
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
            val habitDays = doneByDay.filter { (day, ids) -> !day.isBefore(monday) && "morning_movement" in ids && day !in routineDays }.size
            return logs.sumOf { it.minutes } + habitDays * 5
        }

    val placeholders: Map<String, String>
        get() = mapOf("name" to prefs.displayName, "wakeTime" to prefs.wakeTime.format(DateTimeFormatter.ofPattern("h:mm a")))
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
        listOf(repo.prefs, repo.habits, repo.checkIns, repo.steps, repo.ritualSettings, repo.routineLogs, dayTick),
    ) { values ->
        @Suppress("UNCHECKED_CAST")
        val p = values[0] as UserPrefs
        val habits = values[1] as List<HabitEntity>
        val checkIns = values[2] as List<CheckInEntity>
        val progress = (values[3] as List<StepProgressEntity>).associateBy { it.stepId }
        val today = logicalDay(LocalDateTime.now(), p.dayStartOffsetHours)
        val steps = content.journey.steps.mapNotNull { step ->
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
            prefs = p, today = today, habits = habits, checkIns = checkIns, steps = steps,
            ritualSettings = (values[4] as List<RitualSettingsEntity>).associateBy { it.ritual },
            routineLogs = values[5] as List<RoutineLogEntity>,
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    fun onResume() {
        dayTick.value++
        viewModelScope.launch { repo.onAppOpen() }
    }

    fun letter(id: String): Letter? = when (id) {
        "intro" -> content.journey.introLetter
        "outro" -> content.journey.outroLetter
        "missed" -> content.journey.missedDaysLetter
        "welcome" -> content.journey.welcomeBackLetter
        "gentle" -> content.food.gentleGuidance
        else -> content.journey.steps.firstOrNull { it.id == id }?.letter
    }

    fun completeOnboarding(transform: (UserPrefs) -> UserPrefs) = viewModelScope.launch { repo.completeOnboarding(transform) }

    fun updatePrefs(transform: (UserPrefs) -> UserPrefs) = viewModelScope.launch { container.prefs.update(transform) }

    fun setGentle(on: Boolean, today: LocalDate) = updatePrefs {
        it.copy(gentleStomach = on, gentleSince = if (on) today else null, gentleTipDismissed = false)
    }

    fun acceptStep(stepId: String) = viewModelScope.launch { repo.acceptStep(stepId) }

    fun completeAction(stepId: String) = viewModelScope.launch { repo.completeAction(stepId)?.let(::emitStepCompleted) }

    fun skipStep(stepId: String) = viewModelScope.launch { repo.skipStep(stepId)?.let(::emitStepCompleted) }

    private fun emitStepCompleted(step: Step) {
        _events.tryEmit(UiEvent.StepCompleted(step, content.journey.steps.last().id == step.id))
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
    suspend fun exportJson(): String = repo.exportJson()
    fun resetAll() = viewModelScope.launch { repo.resetAll() }
}
