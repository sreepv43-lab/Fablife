package io.github.sreepv43.fablife.ui.explore

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import io.github.sreepv43.fablife.content.Move
import io.github.sreepv43.fablife.content.RoutineItem
import io.github.sreepv43.fablife.ui.AppState
import io.github.sreepv43.fablife.ui.AppViewModel
import io.github.sreepv43.fablife.ui.CountdownTimer
import io.github.sreepv43.fablife.ui.KeepScreenOn

/** Plays a routine one move at a time, with rests between moves. */
@Composable
fun RoutinePlayerScreen(vm: AppViewModel, state: AppState, routineId: String, nav: NavHostController) {
    KeepScreenOn()
    val routine = vm.content.exercise.routines.firstOrNull { it.id == routineId } ?: return
    val moves = vm.content.exercise.moves.associateBy { it.id }
    var index by rememberSaveable { mutableIntStateOf(0) }
    var resting by rememberSaveable { mutableStateOf(false) }
    var showEasier by rememberSaveable { mutableStateOf(false) }
    var painPrompt by rememberSaveable { mutableStateOf(false) }
    var finished by rememberSaveable { mutableStateOf(false) }

    fun advance() {
        showEasier = false
        if (index == routine.items.size - 1) {
            if (!finished) vm.logRoutine(routine.id, routine.minutes)
            finished = true
        } else if (!resting && routine.restSeconds > 0 && routine.items[index].label != "Rest" && routine.items[index + 1].label != "Rest") {
            resting = true
        } else {
            resting = false
            index++
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.Outlined.Close, contentDescription = "Close routine") }
            Text(routine.title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
        }
        LinearProgressIndicator(progress = { (index + if (finished) 1 else 0) / routine.items.size.toFloat() }, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))

        if (finished) {
            Finished(vm, state, nav)
            return
        }
        val item = routine.items[index]
        val move = item.move?.let { moves[it] }
        Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
        ) {
            if (resting) {
                Text("Rest", style = MaterialTheme.typography.headlineMedium)
                val next = routine.items[index + 1]
                Text("Next: ${nameOf(next, moves)}", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(8.dp))
                CountdownTimer(routine.restSeconds, key = "rest$index", autoStart = true) { advance() }
            } else {
                Text("${index + 1} of ${routine.items.size}", style = MaterialTheme.typography.labelLarge)
                Text(nameOf(item, moves), style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center, modifier = Modifier.padding(8.dp))
                move?.let {
                    Text(it.howTo, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, modifier = Modifier.widthIn(max = 480.dp))
                    if (showEasier) {
                        Text("Easier: ${it.easier}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(top = 12.dp))
                    } else {
                        TextButton(onClick = { showEasier = true }) { Text("Show easier version") }
                    }
                }
                Spacer(Modifier.height(16.dp))
                when {
                    item.seconds != null -> CountdownTimer(item.seconds, key = "item$index", autoStart = true) { advance() }
                    item.reps != null -> Text("× ${item.reps}", fontSize = 56.sp, style = MaterialTheme.typography.displayLarge)
                }
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (index > 0 && !resting) IconButton(onClick = { index--; showEasier = false }) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Previous move") }
            if (move != null && !resting) TextButton(onClick = { painPrompt = true }) { Text("Feel pain?") }
            Spacer(Modifier.weight(1f))
            TextButton(onClick = { advance() }) { Text(if (resting) "Skip rest" else "Skip") }
            if (!resting && item.seconds == null) Button(onClick = { advance() }) { Text("Done") }
        }
    }

    if (painPrompt) {
        AlertDialog(
            onDismissRequest = { painPrompt = false },
            title = { Text("Feel pain?") },
            text = { Text("Stop this move. Try the easier version or skip it. If pain continues, rest and talk to a doctor.") },
            confirmButton = { TextButton(onClick = { showEasier = true; painPrompt = false }) { Text("Show easier version") } },
            dismissButton = { TextButton(onClick = { painPrompt = false; advance() }) { Text("Skip this move") } },
        )
    }
}

private fun nameOf(item: RoutineItem, moves: Map<String, Move>): String =
    item.label ?: item.move?.let { moves[it]?.name } ?: "Move"

@Composable
private fun Finished(vm: AppViewModel, state: AppState, nav: NavHostController) {
    val habit = state.habits.firstOrNull { it.id == "morning_movement" }
    val offerCheckIn = habit != null && habit.id !in state.doneToday
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("💪", fontSize = 64.sp)
        Text("You moved your body today.", style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
        Text("That's something to be proud of.", modifier = Modifier.padding(8.dp))
        Spacer(Modifier.height(16.dp))
        if (offerCheckIn) {
            Button(onClick = {
                vm.setCheckIn("morning_movement", state.today, done = true)
                nav.popBackStack()
            }) { Text("Mark “Move for 5 minutes” as done") }
            TextButton(onClick = { nav.popBackStack() }) { Text("Close") }
        } else {
            Button(onClick = { nav.popBackStack() }) { Text("Close") }
        }
    }
}
