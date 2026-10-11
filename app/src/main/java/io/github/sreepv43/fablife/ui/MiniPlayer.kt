package io.github.sreepv43.fablife.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.sreepv43.fablife.audio.android.AudioController
import io.github.sreepv43.fablife.audio.android.PlaybackMode
import io.github.sreepv43.fablife.audio.android.PlaybackState

/** The route that shows what [playback] is playing, or null if nothing is. */
fun playerRoute(playback: PlaybackState): String? = when (playback.mode) {
    PlaybackMode.SOUND -> playback.soundId?.let { Routes.sound(it.key) }
    PlaybackMode.GUIDED -> playback.sessionId?.let { Routes.guided(it) }
    PlaybackMode.IDLE -> null
}

/** A slim bar above the tabs while a sound or guided session is playing, so it can be paused or stopped from anywhere. */
@Composable
fun MiniPlayer(playback: PlaybackState, audio: AudioController, onOpen: (String) -> Unit) {
    if (!playback.active) return
    val route = playerRoute(playback)
    Surface(color = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(start = 16.dp, end = 4.dp).let { if (route != null) it.clickable(role = Role.Button, onClickLabel = "Open player") { onOpen(route) } else it },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                playback.title.orEmpty(), style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            TextButton(onClick = { if (playback.isPlaying) audio.pause() else audio.resume() }) { Text(if (playback.isPlaying) "Pause" else "Resume") }
            TextButton(onClick = { audio.stop() }) { Text("Stop") }
        }
    }
}
