package io.github.sreepv43.fablife.ui.ritual

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import io.github.sreepv43.fablife.content.Ritual
import io.github.sreepv43.fablife.engine.Recommendations
import io.github.sreepv43.fablife.ui.AppState
import io.github.sreepv43.fablife.ui.AppViewModel
import io.github.sreepv43.fablife.ui.CountdownTimer
import io.github.sreepv43.fablife.ui.KeepScreenOn
import io.github.sreepv43.fablife.ui.Routes
import io.github.sreepv43.fablife.ui.habitIcon
import io.github.sreepv43.fablife.ui.label

/** Walks through a ritual's habits one at a time. Completed check-ins are saved as you go. */
@Composable
fun RitualPlayerScreen(vm: AppViewModel, state: AppState, ritual: Ritual, nav: NavHostController) {
    KeepScreenOn()
    // Snapshot the habit list so reordering or completing habits doesn't shift pages mid-ritual.
    val habits = remember(ritual) { state.habitsFor(ritual) }
    var index by rememberSaveable { mutableIntStateOf(0) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.Outlined.Close, contentDescription = "Close ritual") }
            Text("${ritual.label} ritual", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
        }
        if (habits.isEmpty()) {
            Text("This ritual has no habits yet.", modifier = Modifier.padding(16.dp))
            return
        }
        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.Center) {
            habits.indices.forEach { i ->
                Box(
                    Modifier.padding(4.dp).size(10.dp).background(
                        if (i <= index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant, CircleShape,
                    ),
                )
            }
        }
        if (index >= habits.size) {
            Finished(ritual, onClose = { nav.popBackStack() })
            return
        }
        val habit = habits[index]
        val done = habit.id in state.doneToday
        fun next(markedDone: Boolean) {
            if (index < habits.size - 1) { index++; return }
            // A fully completed ritual closes the player so the celebration shows; otherwise show the summary.
            val complete = habits.all { it.id in state.doneToday || (markedDone && it.id == habit.id) }
            if (complete) nav.popBackStack() else index = habits.size
        }
        Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(habitIcon(habit.icon), contentDescription = null, modifier = Modifier.size(72.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(16.dp))
            Text(
                if (habit.useSmaller && habit.smallerVersion != null) habit.smallerVersion else habit.title,
                style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center,
            )
            habit.description?.let {
                Text(it, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, modifier = Modifier.widthIn(max = 480.dp).padding(top = 8.dp))
            }
            if (!habit.useSmaller && habit.smallerVersion != null) {
                Text(
                    "Hard day? ${habit.smallerVersion}. That counts too.",
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp),
                )
            }
            Spacer(Modifier.height(24.dp))
            if (habit.id == "morning_movement") {
                OutlinedButton(onClick = {
                    val c = vm.content
                    Recommendations.wakeUpRoutine(c.exercise.routines, state.prefs.fitnessLevel, state.prefs.movementNeeds, c.jumpingRoutineIds)
                        ?.let { nav.navigate(Routes.routine(it.id)) }
                }) { Text("Follow a guided routine") }
                Spacer(Modifier.height(16.dp))
            }
            habit.durationSeconds?.let { seconds ->
                CountdownTimer(seconds, key = habit.id, autoStart = false, onFinished = {})
            }
            if (done) Text("✓ Done today", color = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(top = 16.dp))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (index > 0) IconButton(onClick = { index-- }) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Previous habit") }
            Spacer(Modifier.weight(1f))
            TextButton(onClick = { next(markedDone = false) }) { Text("Skip") }
            Button(onClick = {
                if (!done) vm.setCheckIn(habit.id, state.today, done = true)
                next(markedDone = true)
            }) { Text(if (index == habits.size - 1) "Done 🎉" else "Done") }
        }
    }
}

@Composable
private fun Finished(ritual: Ritual, onClose: () -> Unit) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("🌟", fontSize = 64.sp)
        Text("${ritual.label} ritual finished", style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
        Text("Every step you took today counts.", modifier = Modifier.padding(16.dp), textAlign = TextAlign.Center)
        Button(onClick = onClose) { Text("Back to today") }
    }
}
