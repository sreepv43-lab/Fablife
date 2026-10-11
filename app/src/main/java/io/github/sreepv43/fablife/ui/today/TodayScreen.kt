package io.github.sreepv43.fablife.ui.today

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.github.sreepv43.fablife.content.Goal
import io.github.sreepv43.fablife.content.Ritual
import io.github.sreepv43.fablife.data.HabitEntity
import io.github.sreepv43.fablife.data.StepState
import io.github.sreepv43.fablife.ui.AppState
import io.github.sreepv43.fablife.ui.AppViewModel
import io.github.sreepv43.fablife.ui.CoachNoteCard
import io.github.sreepv43.fablife.ui.Routes
import io.github.sreepv43.fablife.ui.emoji
import io.github.sreepv43.fablife.ui.buttonLabel
import io.github.sreepv43.fablife.ui.habitIcon
import io.github.sreepv43.fablife.ui.openShortcut
import io.github.sreepv43.fablife.ui.tool
import io.github.sreepv43.fablife.ui.toolRoute
import io.github.sreepv43.fablife.ui.label
import io.github.sreepv43.fablife.ui.timeFormat
import java.time.LocalTime

@Composable
fun TodayScreen(vm: AppViewModel, state: AppState, nav: NavHostController) {
    val greeting = when (LocalTime.now().hour) {
        in 4..11 -> "Good morning"
        in 12..16 -> "Good afternoon"
        else -> "Good evening"
    }
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Text(
                "$greeting, ${state.prefs.displayName}",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp).padding(vertical = 8.dp),
            )
        }
        if (state.prefs.welcomeBackPending) item {
            InfoCard("Welcome back! 👋", "Everything you built is still here. Let's start again, gently.", "Read your letter") {
                vm.updatePrefs { it.copy(welcomeBackPending = false) }
                nav.navigate(Routes.letter("welcome"))
            }
        }
        item { CoachNoteCard(state) }
        item { JourneyCard(vm, state, nav) }
        if (state.showNudge) item { NudgeCard(vm, state, nav) }
        val since = state.prefs.gentleSince
        if (state.prefs.gentleStomach && since != null && !since.isAfter(state.today.minusDays(14)) && !state.prefs.gentleTipDismissed) item {
            Card(Modifier.fillMaxWidth().widthIn(max = 640.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Feeling better? 🌿", style = MaterialTheme.typography.titleMedium)
                    Text("Try adding one higher-fibre food this week, like oats or a peeled cooked apple.", modifier = Modifier.padding(vertical = 8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalButton(onClick = { vm.updatePrefs { it.copy(gentleTipDismissed = true) } }) { Text("Try it") }
                        TextButton(onClick = { vm.updatePrefs { it.copy(gentleTipDismissed = true, gentleSince = state.today) } }) { Text("Not yet") }
                    }
                }
            }
        }
        Ritual.entries.forEach { ritual -> item(ritual.name) { RitualCard(vm, state, ritual, nav) } }
    }
}

@Composable
private fun InfoCard(title: String, body: String, action: String, onAction: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(body, modifier = Modifier.padding(vertical = 8.dp))
            Button(onClick = onAction) { Text(action) }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun JourneyCard(vm: AppViewModel, state: AppState, nav: NavHostController) {
    val modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp)
    if (state.journeyComplete) {
        InfoCard("${state.activeJourney.title}: complete 🏆", "You finished this journey. Your habits stay in your rituals, so keep them going.", "Choose my next journey") { nav.navigate(Routes.JOURNEYS) }
        return
    }
    val current = state.currentStep ?: return
    val step = current.step
    val index = state.steps.indexOf(current) + 1
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer), modifier = modifier) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Step $index of ${state.activeJourney.steps.size} · ${state.activeJourney.title}", style = MaterialTheme.typography.labelLarge)
            Text(step.title, style = MaterialTheme.typography.titleLarge)
            if (current.progress.state == StepState.UNLOCKED) {
                Text("A new letter from your coach is waiting.")
                Button(onClick = { nav.navigate(Routes.letter(step.id)) }) { Text("Read letter ✉️") }
                return@Column
            }
            when (val goal = step.goal) {
                is Goal.Habit, is Goal.RitualChallenge -> {
                    val label = if (goal is Goal.Habit) goal.habit.title else "Complete your ${state.activeJourney.ritualFocus.label.lowercase()} ritual"
                    Text("$label: ${current.doneCount} of ${current.target} days")
                    LinearProgressIndicator(progress = { current.doneCount / current.target.toFloat() }, modifier = Modifier.fillMaxWidth())
                    if (step.tips.isNotEmpty()) {
                        Text("💡 " + step.tips[(state.today.toEpochDay() % step.tips.size).toInt()], style = MaterialTheme.typography.bodyMedium)
                    }
                    if (state.prefs.gentleStomach && step.gentleNote != null) {
                        Text("🌿 " + step.gentleNote, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                is Goal.Action -> {
                    Text(goal.title, style = MaterialTheme.typography.titleMedium)
                    Text(goal.description, style = MaterialTheme.typography.bodyMedium)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { vm.completeAction(step.id) }) { Text("Mark as done") }
                        goal.shortcuts.forEach { sc -> OutlinedButton(onClick = { nav.openShortcut(sc.target) }) { Text(sc.label) } }
                    }
                }
            }
            TextButton(onClick = { nav.navigate(Routes.letter(step.id)) }, contentPadding = PaddingValues(0.dp)) { Text("Re-read letter") }
        }
    }
}

@Composable
private fun NudgeCard(vm: AppViewModel, state: AppState, nav: NavHostController) {
    val active = state.currentStep ?: return
    val habitId = (active.step.goal as? Goal.Habit)?.habit?.id
    Card(Modifier.fillMaxWidth().widthIn(max = 640.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Missed a few days? 💛", style = MaterialTheme.typography.titleMedium)
            Text("That's completely normal. Coming back is what counts. What would help most?")
            TextButton(onClick = { nav.navigate(Routes.letter("missed")) }, contentPadding = PaddingValues(0.dp)) { Text("Read a letter from your coach") }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilledTonalButton(onClick = { vm.updatePrefs { it.copy(nudgeDismissedDay = state.today) } }) { Text("Keep going") }
                if (habitId != null && state.habits.any { it.id == habitId && !it.useSmaller }) {
                    OutlinedButton(onClick = {
                        vm.setUseSmaller(habitId, true)
                        vm.updatePrefs { it.copy(nudgeDismissedDay = state.today) }
                    }) { Text("Make it smaller") }
                }
            }
            TextButton(onClick = { nav.navigate("settings") }, contentPadding = PaddingValues(0.dp)) { Text("Change reminder time") }
        }
    }
}

@Composable
private fun RitualCard(vm: AppViewModel, state: AppState, ritual: Ritual, nav: NavHostController) {
    val habits = state.habitsFor(ritual)
    val time = state.ritualSettings[ritual]?.reminderTime
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp),
    ) {
        Column(Modifier.padding(vertical = 12.dp)) {
            Row(Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("${ritual.emoji} ${ritual.label}", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                val streak = state.streak(ritual)
                if (streak > 0) Text("🔥 $streak", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(end = 8.dp))
                if (time != null) Text(time.format(timeFormat), style = MaterialTheme.typography.bodySmall)
            }
            if (habits.isEmpty()) {
                Text(
                    "No habits yet. Your journey will add some soon.",
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                )
            } else {
                habits.forEach { HabitRow(vm, state, it, nav) }
                Button(onClick = { nav.navigate(Routes.ritual(ritual)) }, modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                    Text("Start ritual")
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HabitRow(vm: AppViewModel, state: AppState, habit: HabitEntity, nav: NavHostController) {
    val done = habit.id in state.doneToday
    val skipped = habit.id in state.skippedToday
    var menu by remember { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
            .combinedClickable(role = Role.Checkbox, onClickLabel = if (done) "Mark not done" else "Mark done", onLongClick = { menu = true }) {
                vm.toggleHabit(habit.id, state.today)
            }
            .padding(horizontal = 8.dp),
    ) {
        Checkbox(checked = done, onCheckedChange = null, modifier = Modifier.padding(12.dp))
        Icon(habitIcon(habit.icon), contentDescription = null, modifier = Modifier.size(22.dp), tint = MaterialTheme.colorScheme.primary)
        Column(Modifier.weight(1f).padding(start = 12.dp, top = 6.dp, bottom = 6.dp)) {
            Text(
                if (habit.useSmaller && habit.smallerVersion != null) habit.smallerVersion else habit.title,
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = if (skipped) TextDecoration.LineThrough else null,
            )
            val sub = if (skipped) "Skipped today" else habit.description
            if (sub != null) Text(sub, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        val tool = habit.tool()
        if (tool != null && !done) {
            TextButton(onClick = { toolRoute(habit, vm, state)?.let(nav::navigate) }) { Text(tool.type.buttonLabel) }
        }
        Box {
            IconButton(onClick = { menu = true }) { Icon(Icons.Outlined.MoreVert, contentDescription = "More options for ${habit.title}") }
            DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                if (!done) DropdownMenuItem(text = { Text("Skip today") }, onClick = { vm.skipToday(habit.id, state.today); menu = false })
                if (habit.smallerVersion != null) {
                    DropdownMenuItem(
                        text = { Text(if (habit.useSmaller) "Use full version" else "Make it smaller") },
                        onClick = { vm.setUseSmaller(habit.id, !habit.useSmaller); menu = false },
                    )
                }
                Ritual.entries.filter { it != habit.ritual }.forEach { r ->
                    DropdownMenuItem(text = { Text("Move to ${r.label.lowercase()}") }, onClick = { vm.setHabitRitual(habit.id, r); menu = false })
                }
                DropdownMenuItem(text = { Text("Edit my habits") }, onClick = { nav.navigate(Routes.HABITS); menu = false })
            }
        }
    }
}
