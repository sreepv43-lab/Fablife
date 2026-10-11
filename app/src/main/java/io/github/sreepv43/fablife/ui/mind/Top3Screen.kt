package io.github.sreepv43.fablife.ui.mind

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.github.sreepv43.fablife.engine.TopThree
import io.github.sreepv43.fablife.ui.AppState
import io.github.sreepv43.fablife.ui.AppViewModel

/** The three things that matter most today, with tick-offs. Unfinished ones from yesterday can be carried over. */
@Composable
fun Top3Screen(vm: AppViewModel, state: AppState, habitId: String?, nav: NavHostController) {
    val today = state.today
    val slots = TopThree.slots(state.topTasks, today)
    val carry = TopThree.carryOver(state.topTasks, today)
    val habit = habitId?.let { id -> state.habits.firstOrNull { it.id == id } }
    val planned = slots.any { it != null }

    Column(Modifier.fillMaxSize()) {
        ToolTopBar("Top 3 for today", nav)
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Column(Modifier.fillMaxWidth().widthIn(max = 640.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    "If you only got three things done today, which three would make it a good day? Write them, then tick them off.",
                    style = MaterialTheme.typography.bodyLarge,
                )
                slots.forEachIndexed { slot, task ->
                    TaskRow(
                        slot = slot, stored = task?.text.orEmpty(), done = task?.done == true,
                        onCommit = { vm.setTopTask(today, slot, it) },
                        onDone = { vm.setTopTaskDone(today, slot, it) },
                    )
                }
                if (TopThree.allDone(state.topTasks, today)) {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer), modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text("🎉 All three done", style = MaterialTheme.typography.titleMedium)
                            Text("That's a real day's work. Rest well.", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
                if (carry.isNotEmpty()) {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer), modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Unfinished from yesterday", style = MaterialTheme.typography.titleSmall)
                            carry.forEach { text ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text, modifier = Modifier.weight(1f))
                                    TextButton(onClick = { TopThree.firstFreeSlot(state.topTasks, today)?.let { vm.setTopTask(today, it, text) } }) { Text("Carry over") }
                                }
                            }
                        }
                    }
                }
                if (habit != null && habit.id !in state.doneToday && planned) {
                    Button(onClick = { vm.setCheckIn(habit.id, today, done = true); nav.popBackStack() }) { Text("Mark “${habit.title}” as done") }
                }
            }
        }
    }
}

@Composable
private fun TaskRow(slot: Int, stored: String, done: Boolean, onCommit: (String) -> Unit, onDone: (Boolean) -> Unit) {
    var text by remember(stored) { mutableStateOf(stored) }
    var hadFocus by remember { mutableStateOf(false) }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = done, onCheckedChange = onDone, enabled = stored.isNotBlank(),
            modifier = Modifier.semantics { contentDescription = "Done: task ${slot + 1}" },
        )
        OutlinedTextField(
            value = text, onValueChange = { text = it }, singleLine = true, label = { Text("Task ${slot + 1}") },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { if (text != stored) onCommit(text) }),
            modifier = Modifier.weight(1f).onFocusChanged { f ->
                if (hadFocus && !f.isFocused && text != stored) onCommit(text)
                hadFocus = f.isFocused
            },
        )
    }
}
