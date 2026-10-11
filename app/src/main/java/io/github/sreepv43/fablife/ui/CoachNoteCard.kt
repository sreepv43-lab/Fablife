package io.github.sreepv43.fablife.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.sreepv43.fablife.coach.AdaptiveNotes
import io.github.sreepv43.fablife.coach.CoachContext
import io.github.sreepv43.fablife.coach.CoachStep
import io.github.sreepv43.fablife.coach.GoalKind
import io.github.sreepv43.fablife.coach.StepPhase
import io.github.sreepv43.fablife.coach.StepTheme
import io.github.sreepv43.fablife.content.Goal
import io.github.sreepv43.fablife.content.MovementNeeds
import io.github.sreepv43.fablife.content.Ritual
import io.github.sreepv43.fablife.content.ToolType
import io.github.sreepv43.fablife.data.StepState
import java.time.LocalTime
import java.time.temporal.ChronoUnit

/** What the on-device coach knows about the user right now, taken from the app state. */
fun coachContext(state: AppState, hour: Int = LocalTime.now().hour): CoachContext {
    val current = state.currentStep
    val step = current?.let { view ->
        val goal = view.step.goal
        CoachStep(
            title = view.step.title,
            goalKind = when (goal) {
                is Goal.Habit -> GoalKind.HABIT
                is Goal.Action -> GoalKind.ACTION
                is Goal.RitualChallenge -> GoalKind.RITUAL
            },
            phase = if (view.progress.state == StepState.UNLOCKED) StepPhase.UNREAD else StepPhase.ACTIVE,
            doneDays = view.doneCount,
            targetDays = view.target,
            daysActive = view.progress.activatedDay?.let { ChronoUnit.DAYS.between(it, state.today).toInt() + 1 } ?: 0,
            smallerVersion = (goal as? Goal.Habit)?.habit?.smallerVersion,
            theme = when {
                state.activeJourney.id == "eat" -> StepTheme.FOOD
                (goal as? Goal.Habit)?.habit?.tool?.type == ToolType.ROUTINE -> StepTheme.MOVEMENT
                else -> StepTheme.OTHER
            },
        )
    }
    val lastDone = state.doneByDay.keys.maxOrNull()
    return CoachContext(
        day = state.today,
        hour = hour,
        name = state.prefs.name.ifBlank { null },
        journeyTitle = state.activeJourney.title,
        step = step,
        journeyComplete = state.journeyComplete,
        streak = Ritual.entries.maxOf { state.streak(it) },
        daysSinceCheckIn = lastDone?.let { ChronoUnit.DAYS.between(it, state.today).toInt() },
        obstacles = state.prefs.obstacles,
        gentleEating = state.prefs.gentleStomach,
        gentleMovement = state.prefs.movementNeeds != MovementNeeds.STANDARD,
    )
}

/** A short note from the on-device coach. It is chosen on the phone from the user's progress; nothing is sent anywhere. */
@Composable
fun CoachNoteCard(state: AppState, modifier: Modifier = Modifier) {
    val note = remember(state.today, state.currentStep, state.prefs, state.doneByDay.size) { AdaptiveNotes.note(coachContext(state)) }
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = modifier.fillMaxWidth().widthIn(max = 640.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Coach note", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
            Text(note.text, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 4.dp))
            Text("Written on your phone from your progress", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 6.dp))
        }
    }
}
