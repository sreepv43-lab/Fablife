package io.github.sreepv43.fablife.ui.mind

import android.provider.Settings
import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
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
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.github.sreepv43.fablife.audio.BreathPhase
import io.github.sreepv43.fablife.audio.BreathingPacer
import io.github.sreepv43.fablife.content.BreathingPattern
import io.github.sreepv43.fablife.data.SessionKind
import io.github.sreepv43.fablife.ui.AppState
import io.github.sreepv43.fablife.ui.AppViewModel
import io.github.sreepv43.fablife.ui.KeepScreenOn
import io.github.sreepv43.fablife.ui.vibrateTick

private enum class Stage { SETUP, RUNNING, DONE }

private class Length(val label: String, val rounds: Int)

private const val MIN_LOGGED_MS = 30_000L

/** A paced-breathing session: pick a length, then follow the circle. Optional soft vibration on each change. */
@Composable
fun BreathingScreen(vm: AppViewModel, state: AppState, patternId: String, habitId: String?, nav: NavHostController) {
    val pattern = vm.content.audio.breathing.firstOrNull { it.id == patternId }
    if (pattern == null) {
        ToolNotFound("Breathe", nav)
        return
    }
    val context = LocalContext.current
    val reducedMotion = remember { Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f }
    val lengths = remember(pattern) { lengthsFor(pattern) }
    var chosen by rememberSaveable(patternId) { mutableIntStateOf(lengths.indexOfFirst { it.rounds == BreathingPacer.roundsFor(pattern, pattern.defaultMinutes) }.coerceAtLeast(0)) }
    var stage by rememberSaveable { mutableStateOf(Stage.SETUP) }
    var paused by rememberSaveable { mutableStateOf(false) }
    var vibration by rememberSaveable { mutableStateOf(false) }
    var elapsedMs by rememberSaveable { mutableLongStateOf(0L) }
    var logged by rememberSaveable { mutableStateOf(false) }
    val rounds = lengths[chosen.coerceIn(0, lengths.lastIndex)].rounds
    val pacer = BreathingPacer.stateAt(pattern, elapsedMs, rounds)

    LaunchedEffect(stage, paused) {
        if (stage != Stage.RUNNING || paused) return@LaunchedEffect
        var last = withInfiniteAnimationFrameMillis { it }
        while (true) {
            val now = withInfiniteAnimationFrameMillis { it }
            elapsedMs += (now - last).coerceIn(0, 100) // a long gap (screen was off) must not skip ahead
            last = now
            if (BreathingPacer.stateAt(pattern, elapsedMs, rounds).finished) {
                stage = Stage.DONE
                return@LaunchedEffect
            }
        }
    }
    LaunchedEffect(pacer.phase, pacer.cycle, stage) {
        if (stage == Stage.RUNNING && !paused && vibration) vibrateTick(context)
    }
    LaunchedEffect(stage) {
        if (stage == Stage.DONE && !logged) {
            logged = true
            vm.logSession(SessionKind.BREATHING, pattern.id, minutesOf(elapsedMs))
        }
    }
    if (stage == Stage.RUNNING) KeepScreenOn()

    Column(Modifier.fillMaxSize()) {
        ToolTopBar(pattern.name, nav)
        when (stage) {
            Stage.SETUP -> Setup(
                pattern, lengths, chosen, vibration,
                onChoose = { chosen = it },
                onVibration = { vibration = it },
                onBegin = { elapsedMs = 0; paused = false; logged = false; stage = Stage.RUNNING },
            )
            Stage.RUNNING -> Running(
                pattern, pacer.phase, pacer.secondsLeft, pacer.scale, elapsedMs.toFloat() / BreathingPacer.totalMs(pattern, rounds), paused, reducedMotion,
                onPauseToggle = { paused = !paused },
                onEnd = {
                    if (elapsedMs >= MIN_LOGGED_MS) stage = Stage.DONE else { elapsedMs = 0; paused = false; stage = Stage.SETUP }
                },
            )
            Stage.DONE -> ToolFinished(
                vm, state, habitId, nav, "🌬️", "Nicely done.",
                "You breathed for about ${minutesOf(elapsedMs)} ${if (minutesOf(elapsedMs) == 1) "minute" else "minutes"}. Notice how you feel right now.",
            )
        }
    }
}

private fun minutesOf(ms: Long) = ((ms + 30_000) / 60_000).toInt().coerceAtLeast(1)

private fun lengthsFor(p: BreathingPattern): List<Length> {
    val byMinutes = listOf(1, 3, 5, 10).map { Length("$it min", BreathingPacer.roundsFor(p, it)) }
    val list = if (p.id == "relax_478") listOf(Length("3 breaths", 3)) + byMinutes else byMinutes
    return list.distinctBy { it.rounds }
}

private fun patternLine(p: BreathingPattern): String {
    fun n(x: Float) = if (x % 1f == 0f) x.toInt().toString() else x.toString()
    val parts = mutableListOf("in ${n(p.inhale)}")
    if (p.hold1 > 0) parts += "hold ${n(p.hold1)}"
    parts += "out ${n(p.exhale)}"
    if (p.hold2 > 0) parts += "hold ${n(p.hold2)}"
    return parts.joinToString(" · ")
}

@Composable
private fun Setup(
    pattern: BreathingPattern,
    lengths: List<Length>,
    chosen: Int,
    vibration: Boolean,
    onChoose: (Int) -> Unit,
    onVibration: (Boolean) -> Unit,
    onBegin: () -> Unit,
) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(Modifier.widthIn(max = 560.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(pattern.description, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge)
            Text(patternLine(pattern), style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 12.dp))
            if (pattern.id == "relax_478") {
                Text("New to this pattern? Start with 3 breaths.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(top = 4.dp))
            }
            Text("How long?", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(top = 24.dp, bottom = 8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                lengths.forEachIndexed { i, l -> FilterChip(selected = i == chosen, onClick = { onChoose(i) }, label = { Text(l.label) }) }
            }
            Row(Modifier.fillMaxWidth().padding(top = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Soft vibration on each change", modifier = Modifier.weight(1f))
                Switch(checked = vibration, onCheckedChange = onVibration)
            }
            Spacer(Modifier.height(24.dp))
            Button(onClick = onBegin) { Text("Begin") }
            Text(
                "Sit comfortably. If you feel dizzy, stop and breathe normally. This isn't medical advice.",
                style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 16.dp),
            )
        }
    }
}

private fun cue(phase: BreathPhase) = when (phase) {
    BreathPhase.INHALE -> "Breathe in"
    BreathPhase.EXHALE -> "Breathe out"
    BreathPhase.HOLD_IN, BreathPhase.HOLD_OUT -> "Hold"
}

@Composable
private fun Running(
    pattern: BreathingPattern,
    phase: BreathPhase,
    secondsLeft: Int,
    scale: Float,
    progress: Float,
    paused: Boolean,
    reducedMotion: Boolean,
    onPauseToggle: () -> Unit,
    onEnd: () -> Unit,
) {
    val label = if (paused) "Paused" else cue(phase)
    Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        LinearProgressIndicator(progress = { progress.coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth().widthIn(max = 560.dp))
        Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            // The circle grows on the way in and shrinks on the way out. With animations turned off in system settings it stays still.
            val size = if (reducedMotion) 220.dp else (140 + 120 * scale).dp
            Box(
                Modifier.size(size).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer)
                    .semantics { contentDescription = "$label, $secondsLeft seconds" },
                contentAlignment = Alignment.Center,
            ) {
                Text(if (paused) "" else secondsLeft.toString(), style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
            }
        }
        // Only the cue is announced to a screen reader; the seconds would be read out every second.
        Text(label, style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
        Text(pattern.name, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp, bottom = 16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onPauseToggle) { Text(if (paused) "Resume" else "Pause") }
            TextButton(onClick = onEnd) { Text("End") }
        }
    }
}
