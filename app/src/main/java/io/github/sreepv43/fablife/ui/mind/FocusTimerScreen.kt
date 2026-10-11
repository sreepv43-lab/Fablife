package io.github.sreepv43.fablife.ui.mind

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.github.sreepv43.fablife.audio.SoundId
import io.github.sreepv43.fablife.data.SessionKind
import io.github.sreepv43.fablife.engine.FocusPlan
import io.github.sreepv43.fablife.engine.FocusSettings
import io.github.sreepv43.fablife.engine.Segment
import io.github.sreepv43.fablife.engine.SegmentTimer
import io.github.sreepv43.fablife.engine.SegmentType
import io.github.sreepv43.fablife.ui.AppState
import io.github.sreepv43.fablife.ui.AppViewModel
import io.github.sreepv43.fablife.ui.KeepScreenOn
import io.github.sreepv43.fablife.ui.chime
import io.github.sreepv43.fablife.ui.vibrate
import kotlinx.coroutines.delay

private const val NOT_RUNNING = -1L
private val FOCUS_SOUNDS = listOf<SoundId?>(null, SoundId.BROWN, SoundId.PINK, SoundId.FOCUS_PAD)

/**
 * Pomodoro-style timer: focus blocks with short breaks and a longer one every fourth round. The countdown is measured
 * against a clock that keeps running in the background, so it stays right if the screen is off. Finished focus time is
 * logged; breaks and abandoned starts are not.
 */
@Composable
fun FocusTimerScreen(vm: AppViewModel, state: AppState, habitId: String?, nav: NavHostController) {
    val context = LocalContext.current
    val settings = remember(state.prefs.focusMinutes, state.prefs.breakMinutes, state.prefs.longBreakMinutes) {
        FocusSettings(state.prefs.focusMinutes, state.prefs.breakMinutes, state.prefs.longBreakMinutes).coerced()
    }
    var started by rememberSaveable { mutableStateOf(false) }
    var finished by rememberSaveable { mutableStateOf(false) }
    var index by rememberSaveable { mutableIntStateOf(0) }
    var remainingMs by rememberSaveable { mutableLongStateOf(0L) }
    var endAt by rememberSaveable { mutableLongStateOf(NOT_RUNNING) }
    var loggedMinutes by rememberSaveable { mutableIntStateOf(0) }
    var soundKey by rememberSaveable { mutableStateOf<String?>(null) }
    var ownsAudio by rememberSaveable { mutableStateOf(false) }
    var awaitingStart by rememberSaveable { mutableStateOf(false) } // the next focus block is ready but not started
    var now by remember { mutableLongStateOf(vm.clock()) }

    val segment = FocusPlan.segment(settings, index)
    val timer = SegmentTimer(segment.durationMs, remainingMs, endAt.takeIf { it != NOT_RUNNING })

    fun stopOwnAudio() {
        if (ownsAudio) vm.audio.stop()
        ownsAudio = false
    }

    fun startSegment(i: Int, run: Boolean) {
        val next = FocusPlan.segment(settings, i)
        index = i
        remainingMs = next.durationMs
        endAt = if (run) vm.clock() + next.durationMs else NOT_RUNNING
        awaitingStart = !run
        now = vm.clock()
        val sound = SoundId.fromKey(soundKey)
        if (run && next.type == SegmentType.FOCUS && sound != null) {
            vm.audio.playSound(sound)
            ownsAudio = true
        }
    }

    fun logFocus(seg: Segment, elapsedMs: Long, completed: Boolean) {
        val minutes = FocusPlan.minutesToLog(seg, elapsedMs, completed)
        if (minutes > 0) {
            vm.logSession(SessionKind.FOCUS, "pomodoro", minutes)
            loggedMinutes += minutes
        }
    }

    fun finishSession() {
        if (segment.type == SegmentType.FOCUS) logFocus(segment, timer.elapsed(vm.clock()), completed = false)
        stopOwnAudio()
        endAt = NOT_RUNNING
        if (loggedMinutes > 0 || segment.type == SegmentType.FOCUS && timer.elapsed(vm.clock()) >= FocusPlan.MIN_PARTIAL_MINUTES * 60_000L) finished = true
        else started = false
    }

    // Ticks while a segment is running and moves on when it ends.
    LaunchedEffect(index, endAt) {
        if (endAt == NOT_RUNNING) return@LaunchedEffect
        while (true) {
            now = vm.clock()
            if (now >= endAt) {
                chime(context)
                vibrate(context)
                logFocus(segment, segment.durationMs, completed = true)
                stopOwnAudio()
                // A break starts by itself; the next focus block waits until you are ready.
                startSegment(index + 1, run = segment.type == SegmentType.FOCUS)
                return@LaunchedEffect
            }
            delay(250)
        }
    }

    if (started && !finished) KeepScreenOn()

    Column(Modifier.fillMaxSize()) {
        ToolTopBar("Focus timer", nav)
        when {
            finished -> ToolFinished(
                vm, state, habitId, nav, "🎯", "Good focus.",
                "You focused for $loggedMinutes ${if (loggedMinutes == 1) "minute" else "minutes"}. Take a proper break.",
                onClose = { stopOwnAudio(); nav.popBackStack() },
            )
            !started -> Setup(
                settings, soundKey,
                onFocus = { vm.updatePrefs { p -> p.copy(focusMinutes = it) } },
                onBreak = { vm.updatePrefs { p -> p.copy(breakMinutes = it) } },
                onLong = { vm.updatePrefs { p -> p.copy(longBreakMinutes = it) } },
                onSound = { soundKey = it },
                onStart = { started = true; loggedMinutes = 0; startSegment(0, run = true) },
            )
            else -> Running(
                segment, timer.remaining(now), timer.running, awaitingStart, loggedMinutes,
                onPauseToggle = {
                    val t = vm.clock()
                    if (timer.running) {
                        remainingMs = timer.pause(t).remainingMs
                        endAt = NOT_RUNNING
                        if (ownsAudio) vm.audio.pause()
                    } else {
                        endAt = t + remainingMs
                        awaitingStart = false
                        if (ownsAudio) vm.audio.resume()
                    }
                    now = t
                },
                onSkip = {
                    if (segment.type == SegmentType.FOCUS) logFocus(segment, timer.elapsed(vm.clock()), completed = false)
                    stopOwnAudio()
                    startSegment(index + 1, run = segment.type == SegmentType.FOCUS)
                },
                onFinish = ::finishSession,
            )
        }
    }
}

@Composable
private fun Stepper(label: String, value: Int, range: IntRange, step: Int, onChange: (Int) -> Unit) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(label, modifier = Modifier.weight(1f))
        IconButton(onClick = { onChange((value - step).coerceIn(range)) }, enabled = value > range.first) {
            Icon(Icons.Outlined.Remove, contentDescription = "Shorten $label")
        }
        Text("$value min", style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center, modifier = Modifier.widthIn(min = 64.dp))
        IconButton(onClick = { onChange((value + step).coerceIn(range)) }, enabled = value < range.last) {
            Icon(Icons.Outlined.Add, contentDescription = "Lengthen $label")
        }
    }
}

@Composable
private fun Setup(
    settings: FocusSettings,
    soundKey: String?,
    onFocus: (Int) -> Unit,
    onBreak: (Int) -> Unit,
    onLong: (Int) -> Unit,
    onSound: (String?) -> Unit,
    onStart: () -> Unit,
) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Column(Modifier.widthIn(max = 560.dp)) {
            Text(
                "Pick one thing to work on, then work on only that until the timer ends. After every fourth block you get a longer break.",
                style = MaterialTheme.typography.bodyLarge,
            )
            Spacer(Modifier.height(16.dp))
            Stepper("Focus", settings.focusMinutes, FocusSettings.FOCUS_RANGE, 5, onFocus)
            Stepper("Break", settings.breakMinutes, FocusSettings.BREAK_RANGE, 1, onBreak)
            Stepper("Long break", settings.longBreakMinutes, FocusSettings.LONG_BREAK_RANGE, 5, onLong)
            Text("Sound while you focus", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
            Row(Modifier.horizontalScroll(rememberScrollState()).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FOCUS_SOUNDS.forEach { sound ->
                    FilterChip(
                        selected = soundKey == sound?.key, onClick = { onSound(sound?.key) },
                        label = { Text(when (sound) { null -> "None"; SoundId.BROWN -> "Brown noise"; SoundId.PINK -> "Pink noise"; else -> "Focus tone" }) },
                    )
                }
            }
            Text("Generated ambient sound, off during breaks.", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(24.dp))
            Button(onClick = onStart, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("Start focusing") }
        }
    }
}

@Composable
private fun Running(
    segment: Segment,
    remainingMs: Long,
    running: Boolean,
    waiting: Boolean,
    loggedMinutes: Int,
    onPauseToggle: () -> Unit,
    onSkip: () -> Unit,
    onFinish: () -> Unit,
) {
    val label = when (segment.type) {
        SegmentType.FOCUS -> "Focus · round ${segment.round}"
        SegmentType.SHORT_BREAK -> "Short break"
        SegmentType.LONG_BREAK -> "Long break"
    }
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(progress = { 1f - remainingMs / segment.durationMs.toFloat() }, modifier = Modifier.size(240.dp), strokeWidth = 12.dp)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(label, style = MaterialTheme.typography.titleMedium)
                Text(formatClock(((remainingMs + 999) / 1000) * 1000), style = MaterialTheme.typography.displayMedium)
                if (waiting) Text("Ready when you are", style = MaterialTheme.typography.bodyMedium)
            }
        }
        Text("Focused so far: $loggedMinutes min", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = onPauseToggle) { Text(if (running) "Pause" else if (waiting) "Start" else "Resume") }
            TextButton(onClick = onSkip) { Text(if (segment.type == SegmentType.FOCUS) "Skip to break" else "Skip break") }
            TextButton(onClick = onFinish) { Text("Finish") }
        }
    }
}
