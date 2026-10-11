package io.github.sreepv43.fablife.ui.mind

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import io.github.sreepv43.fablife.ui.AppState
import io.github.sreepv43.fablife.ui.AppViewModel

/** Back-arrow top bar shared by the tool screens. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolTopBar(title: String, nav: NavHostController, onBack: () -> Unit = { nav.popBackStack() }) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back") } },
    )
}

/**
 * The "well done" page every tool shows when it finishes. If the tool was opened from a habit that isn't done yet
 * it offers to check that habit in; checking in is always the user's choice.
 */
@Composable
fun ToolFinished(
    vm: AppViewModel,
    state: AppState,
    habitId: String?,
    nav: NavHostController,
    emoji: String,
    title: String,
    message: String,
    onClose: () -> Unit = { nav.popBackStack() },
) {
    val habit = habitId?.let { id -> state.habits.firstOrNull { it.id == id } }
    val offerCheckIn = habit != null && habit.id !in state.doneToday
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(emoji, fontSize = 64.sp)
        Text(title, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
        Text(message, textAlign = TextAlign.Center, modifier = Modifier.padding(8.dp))
        Spacer(Modifier.height(16.dp))
        if (offerCheckIn && habit != null) {
            Button(onClick = {
                vm.setCheckIn(habit.id, state.today, done = true)
                onClose()
            }) { Text("Mark “${habit.title}” as done") }
            TextButton(onClick = onClose) { Text("Close") }
        } else {
            Button(onClick = onClose) { Text("Close") }
        }
    }
}

/** A tool opened with an id that isn't in the content (it can only happen if content and app drift apart). */
@Composable
fun ToolNotFound(title: String, nav: NavHostController) {
    Column(Modifier.fillMaxSize()) {
        ToolTopBar(title, nav)
        Text("This one isn't available any more.", modifier = Modifier.padding(24.dp))
    }
}

fun formatClock(ms: Long): String {
    val seconds = (ms / 1000).coerceAtLeast(0).toInt()
    return "%d:%02d".format(seconds / 60, seconds % 60)
}
