package io.github.sreepv43.fablife.ui

import android.app.Activity
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.WindowManager
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun CelebrationDialog(
    event: UiEvent,
    onDismiss: () -> Unit,
    onReadOutro: () -> Unit,
    onReadNext: (String) -> Unit,
    nextStepId: String?,
) {
    val scale = remember { Animatable(0.4f) }
    LaunchedEffect(event) { scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy)) }
    val (emoji, title, message) = when (event) {
        is UiEvent.RitualCompleted -> Triple("🎉", "${event.ritual.label} ritual complete!", "You showed up for yourself today. Take a second to feel proud.")
        is UiEvent.StepCompleted -> Triple("🏅", event.step.completionTitle, event.step.completionMessage)
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Text(emoji, fontSize = 56.sp, modifier = Modifier.scale(scale.value)) },
        title = { Text(title, textAlign = TextAlign.Center) },
        text = { Text(message, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
        confirmButton = {
            when {
                event is UiEvent.StepCompleted && event.isLast -> Button(onClick = onReadOutro) { Text("Read your final letter") }
                event is UiEvent.StepCompleted && nextStepId != null -> Button(onClick = { onReadNext(nextStepId) }) { Text("Read the next letter") }
                else -> Button(onClick = onDismiss) { Text("Celebrate 🎉") }
            }
        },
        dismissButton = if (event is UiEvent.StepCompleted) ({ TextButton(onClick = onDismiss) { Text("Later") } }) else null,
    )
}

/** Keeps the screen on while composed (ritual and routine players). */
@Composable
fun KeepScreenOn() {
    val activity = LocalContext.current as? Activity
    DisposableEffect(activity) {
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose { activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) }
    }
}

fun vibrate(context: android.content.Context) {
    context.getSystemService(Vibrator::class.java)?.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE))
}

/** A very short, soft tick for paced exercises. */
fun vibrateTick(context: android.content.Context) {
    context.getSystemService(Vibrator::class.java)?.vibrate(VibrationEffect.createOneShot(35, 60))
}

/**
 * A countdown measured against wall-clock time, so it stays accurate if the app is paused.
 * Calls [onFinished] once when it reaches zero.
 */
@Composable
fun CountdownTimer(totalSeconds: Int, key: Any, autoStart: Boolean, onFinished: () -> Unit) {
    val context = LocalContext.current
    var running by rememberSaveable(key) { mutableStateOf(autoStart) }
    var remainingMs by rememberSaveable(key) { mutableLongStateOf(totalSeconds * 1000L) }
    LaunchedEffect(running, key) {
        if (!running) return@LaunchedEffect
        val end = System.currentTimeMillis() + remainingMs
        while (remainingMs > 0) {
            delay(200)
            remainingMs = (end - System.currentTimeMillis()).coerceAtLeast(0)
        }
        running = false
        vibrate(context)
        onFinished()
    }
    val seconds = ((remainingMs + 999) / 1000).toInt()
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(
            progress = { 1f - remainingMs / (totalSeconds * 1000f) },
            modifier = Modifier.size(140.dp),
            strokeWidth = 10.dp,
        )
        Text("%d:%02d".format(seconds / 60, seconds % 60), style = MaterialTheme.typography.displaySmall, modifier = Modifier.padding(top = 12.dp))
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (remainingMs > 0) {
                OutlinedButton(onClick = { running = !running }) { Text(if (running) "Pause" else if (remainingMs < totalSeconds * 1000L) "Resume" else "Start timer") }
            }
        }
    }
}

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(text, style = MaterialTheme.typography.titleMedium, modifier = modifier.padding(top = 8.dp, bottom = 4.dp))
}
