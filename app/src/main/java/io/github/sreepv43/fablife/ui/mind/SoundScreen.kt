package io.github.sreepv43.fablife.ui.mind

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import io.github.sreepv43.fablife.audio.SoundId
import io.github.sreepv43.fablife.audio.android.PlaybackMode
import io.github.sreepv43.fablife.audio.android.PlaybackState
import io.github.sreepv43.fablife.content.SoundInfo
import io.github.sreepv43.fablife.ui.AppState
import io.github.sreepv43.fablife.ui.AppViewModel
import io.github.sreepv43.fablife.ui.Routes
import io.github.sreepv43.fablife.ui.SectionTitle

private val TIMER_CHOICES = listOf<Int?>(null, 15, 30, 60)
private const val CHECK_IN_AFTER_MS = 60_000L

const val SOUNDS_HONESTY_NOTE = "Generated ambient sounds: textures made by your phone as it plays, not recordings or music."

/** The sound library when [soundId] is null, otherwise the player for that sound. */
@Composable
fun SoundScreen(vm: AppViewModel, state: AppState, soundId: String?, habitId: String?, nav: NavHostController) {
    val sounds = vm.content.audio.sounds
    val playback by vm.audio.state.collectAsStateWithLifecycle()
    if (soundId == null) {
        Column(Modifier.fillMaxSize()) {
            ToolTopBar("Sounds", nav)
            SoundLibrary(sounds, playback, nav, onStop = { vm.audio.stop() })
        }
        return
    }
    val info = sounds.firstOrNull { it.id == soundId }
    val id = SoundId.fromKey(soundId)
    if (info == null || id == null) {
        ToolNotFound("Sounds", nav)
        return
    }
    Column(Modifier.fillMaxSize()) {
        ToolTopBar(info.name, nav)
        SoundPlayer(vm, state, info, id, playback, habitId)
    }
}

@Composable
private fun SoundLibrary(sounds: List<SoundInfo>, playback: PlaybackState, nav: NavHostController, onStop: () -> Unit) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize(),
    ) {
        item { Text(SOUNDS_HONESTY_NOTE, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.widthIn(max = 640.dp)) }
        if (playback.mode == PlaybackMode.SOUND && playback.active) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp),
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Now playing: ${playback.title.orEmpty()}", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                        TextButton(onClick = onStop) { Text("Stop") }
                    }
                }
            }
        }
        items(sounds, key = { it.id }) { s ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp).clickable(role = Role.Button) { nav.navigate(Routes.sound(s.id)) },
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(s.name, style = MaterialTheme.typography.titleMedium)
                    Text(s.description, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun SoundPlayer(vm: AppViewModel, state: AppState, info: SoundInfo, id: SoundId, playback: PlaybackState, habitId: String?) {
    val mine = playback.mode == PlaybackMode.SOUND && playback.soundId == id && playback.active
    var timerChoice by rememberSaveable { mutableStateOf<Int?>(null) }
    val habit = habitId?.let { h -> state.habits.firstOrNull { it.id == h } }
    val offerCheckIn = habit != null && habit.id !in state.doneToday && mine && playback.elapsedMs >= CHECK_IN_AFTER_MS

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(Modifier.widthIn(max = 560.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(info.description, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
            Text(SOUNDS_HONESTY_NOTE, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))

            Text("Volume", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 24.dp).align(Alignment.Start))
            Slider(
                value = playback.volume, onValueChange = { vm.audio.setVolume(it) },
                modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Volume" },
            )

            Text("Stop after", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 8.dp).align(Alignment.Start))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(vertical = 8.dp)) {
                TIMER_CHOICES.forEach { minutes ->
                    FilterChip(
                        selected = timerChoice == minutes,
                        onClick = {
                            timerChoice = minutes
                            if (mine) vm.audio.setSleepTimer(minutes)
                        },
                        label = { Text(if (minutes == null) "Off" else "$minutes min") },
                    )
                }
            }
            if (mine && playback.sleepTimerRemainingMs != null) {
                Text("Stops in about ${(playback.sleepTimerRemainingMs + 59_999) / 60_000} min, fading out gently.", style = MaterialTheme.typography.bodyMedium)
            }
            if (playback.error != null && !mine) Text(playback.error, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))

            Row(Modifier.padding(top = 24.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (!mine) {
                    Button(onClick = { vm.audio.playSound(id, timerMinutes = timerChoice) }) { Text(if (playback.finished && playback.soundId == id) "Play again" else "Play") }
                } else {
                    OutlinedButton(onClick = { if (playback.isPlaying) vm.audio.pause() else vm.audio.resume() }) { Text(if (playback.isPlaying) "Pause" else "Resume") }
                    TextButton(onClick = { vm.audio.stop() }) { Text("Stop") }
                }
            }
            if (playback.finished && playback.soundId == id) {
                Text("Timer finished. Sleep well, or rest easy.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 12.dp))
            }
            if (offerCheckIn && habit != null) {
                Button(onClick = { vm.setCheckIn(habit.id, state.today, done = true) }, modifier = Modifier.padding(top = 20.dp)) {
                    Text("Mark “${habit.title}” as done")
                }
            }
        }
    }
}
