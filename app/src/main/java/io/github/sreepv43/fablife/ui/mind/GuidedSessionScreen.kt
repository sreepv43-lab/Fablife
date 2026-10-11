package io.github.sreepv43.fablife.ui.mind

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import io.github.sreepv43.fablife.audio.android.PlaybackMode
import io.github.sreepv43.fablife.audio.android.PlaybackState
import io.github.sreepv43.fablife.content.GuidedSession
import io.github.sreepv43.fablife.content.SessionCategory
import io.github.sreepv43.fablife.ui.AppState
import io.github.sreepv43.fablife.ui.AppViewModel
import io.github.sreepv43.fablife.ui.KeepScreenOn

/**
 * Plays a guided session read aloud by the phone's text-to-speech voice, with the words always shown as captions.
 * Playback is owned by the audio controller, so leaving this screen keeps it going (a mini-player appears).
 */
@Composable
fun GuidedSessionScreen(vm: AppViewModel, state: AppState, sessionId: String, habitId: String?, nav: NavHostController) {
    val session = vm.content.audio.sessions.firstOrNull { it.id == sessionId }
    if (session == null) {
        ToolNotFound("Guided session", nav)
        return
    }
    val playback by vm.audio.state.collectAsStateWithLifecycle()
    val mine = playback.mode == PlaybackMode.GUIDED && playback.sessionId == session.id
    var ambient by rememberSaveable { mutableStateOf(session.ambient != null) }

    // The captions are for reading. Sleep sessions are meant to be listened to with the screen off, so only the others hold it on.
    if (mine && playback.isPlaying && session.category != SessionCategory.SLEEP) KeepScreenOn()

    Column(Modifier.fillMaxSize()) {
        ToolTopBar(session.title, nav)
        when {
            mine && playback.finished -> ToolFinished(
                vm, state, habitId, nav, "🌿", "Well done.",
                "You gave yourself ${session.minutes} minutes. Notice how you feel.",
                onClose = {
                    vm.audio.stop()
                    nav.popBackStack()
                },
            )
            mine -> Playing(session, playback, onPauseToggle = { if (playback.isPlaying) vm.audio.pause() else vm.audio.resume() }, onStop = { vm.audio.stop() })
            else -> Setup(session, ambient, playback.error, onAmbient = { ambient = it }, onBegin = { vm.audio.playGuided(session, ambient) })
        }
    }
}

internal fun SessionCategory.label() = when (this) {
    SessionCategory.SLEEP -> "Sleep"
    SessionCategory.MORNING -> "Morning"
    SessionCategory.CALM -> "Calm"
    SessionCategory.FOCUS -> "Focus"
    SessionCategory.EAT -> "Eat"
}

@Composable
private fun Setup(session: GuidedSession, ambient: Boolean, error: String?, onAmbient: (Boolean) -> Unit, onBegin: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Column(Modifier.widthIn(max = 560.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${session.category.label()} · ${session.minutes} min", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
            Text(session.description, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
            Text("Best for: ${session.bestFor}", style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
            if (session.ambient != null) {
                Row(Modifier.fillMaxWidth().padding(top = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Soft background sound")
                        Text("A quiet generated texture under the voice.", style = MaterialTheme.typography.bodySmall)
                    }
                    Switch(checked = ambient, onCheckedChange = onAmbient)
                }
            }
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
            ) {
                Text(
                    "Read aloud by your phone's own voice, with the words shown on screen. Find a quiet place or use headphones. It keeps playing if you lock the screen.",
                    style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(16.dp),
                )
            }
            if (error != null) Text(error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 12.dp))
            Spacer(Modifier.height(24.dp))
            Button(onClick = onBegin) { Text("Begin") }
        }
    }
}

@Composable
private fun Playing(session: GuidedSession, playback: PlaybackState, onPauseToggle: () -> Unit, onStop: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        if (!playback.ttsAvailable) {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer), modifier = Modifier.fillMaxWidth().widthIn(max = 560.dp)) {
                Text(
                    "No voice is available on this device, so the session is showing timed captions. Read at your own pace.",
                    style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(12.dp),
                )
            }
        }
        // Not a live region: with a screen reader on, the voice itself is what is heard, and reading the caption too would talk over it.
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                playback.captionText.orEmpty(),
                style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 560.dp),
            )
        }
        val fraction = if (playback.totalMs > 0) playback.elapsedMs.toFloat() / playback.totalMs else 0f
        LinearProgressIndicator(progress = { fraction.coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth().widthIn(max = 560.dp))
        Text("${formatClock(playback.elapsedMs)} of about ${formatClock(playback.totalMs)}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp, bottom = 16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onPauseToggle) { Text(if (playback.isPlaying) "Pause" else "Resume") }
            TextButton(onClick = onStop) { Text("Stop") }
        }
        Text(session.title, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 12.dp))
    }
}
