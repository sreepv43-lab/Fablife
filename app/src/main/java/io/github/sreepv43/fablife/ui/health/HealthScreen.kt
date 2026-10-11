package io.github.sreepv43.fablife.ui.health

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.health.connect.client.HealthConnectClient
import androidx.navigation.NavHostController
import io.github.sreepv43.fablife.health.DayHealth
import io.github.sreepv43.fablife.health.HealthUi
import io.github.sreepv43.fablife.health.formatSleep
import io.github.sreepv43.fablife.health.loadHealth
import io.github.sreepv43.fablife.ui.AppState
import io.github.sreepv43.fablife.ui.AppViewModel
import io.github.sreepv43.fablife.ui.SectionTitle
import io.github.sreepv43.fablife.ui.mind.ToolTopBar
import io.github.sreepv43.fablife.ui.mind.dayLabel
import java.text.NumberFormat

private const val HEALTH_CONNECT_PACKAGE = "com.google.android.apps.healthdata"
private val GOAL_RANGE = 2_000..20_000

const val HEALTH_PRIVACY_NOTE = "Fab Life only reads your steps and sleep. They are shown on this screen and never saved by the app, uploaded, or shared, not even with the optional AI coach."
const val HEALTH_DISCLAIMER = "For your information only. This isn't medical advice."

/** Loads the Health Connect state; [refresh] is bumped to load again (after a permission answer, or on request). */
@Composable
fun rememberHealthUi(vm: AppViewModel, state: AppState, refresh: Int = 0): HealthUi =
    produceState<HealthUi>(HealthUi.Loading, refresh, state.today) { value = loadHealth(vm.health, state.today) }.value

/** Steps and sleep from Health Connect, with a clear state for everything that can be true on a device. */
@Composable
fun HealthScreen(vm: AppViewModel, state: AppState, nav: NavHostController) {
    var refresh by rememberSaveable { mutableIntStateOf(0) }
    val ui = rememberHealthUi(vm, state, refresh)
    val permissions = rememberLauncherForActivityResult(vm.health.permissionContract) { refresh++ }

    Column(Modifier.fillMaxSize()) {
        ToolTopBar("Steps and sleep", nav)
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(Modifier.fillMaxWidth().widthIn(max = 640.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                when (val u = ui) {
                    HealthUi.Loading -> Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) { CircularProgressIndicator() }
                    HealthUi.Unavailable -> Message(
                        "Not available on this device",
                        "This device doesn't support Health Connect, so steps and sleep can't be shown here. Everything else in Fab Life works as usual.",
                    )
                    HealthUi.NotInstalled -> NotInstalled()
                    HealthUi.NeedsPermission -> NeedsPermission(
                        onConnect = { permissions.launch(vm.health.requiredPermissions) },
                        onNotNow = { nav.popBackStack() },
                    )
                    HealthUi.Failed -> {
                        Message("Couldn't read Health Connect", "Health Connect didn't answer. Check that it is up to date, then try again.")
                        Button(onClick = { refresh++ }) { Text("Try again") }
                    }
                    is HealthUi.Ready -> Ready(vm, state, u, onRefresh = { refresh++ })
                }
                Text(HEALTH_PRIVACY_NOTE, style = MaterialTheme.typography.bodySmall)
                Text(HEALTH_DISCLAIMER, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
private fun Message(title: String, body: String) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(body, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun NotInstalled() {
    val context = LocalContext.current
    Message(
        "Health Connect isn't installed",
        "Health Connect is Android's own store for steps and sleep from your phone and watch. Install it, then come back to connect it. This is optional.",
    )
    Button(onClick = {
        val market = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$HEALTH_CONNECT_PACKAGE")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(market)
        } catch (e: ActivityNotFoundException) {
            runCatching {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$HEALTH_CONNECT_PACKAGE")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                )
            }
        }
    }) { Text("Get Health Connect") }
}

@Composable
private fun NeedsPermission(onConnect: () -> Unit, onNotNow: () -> Unit) {
    Message(
        "See your steps and sleep here",
        "Fab Life can show today's steps and last night's sleep from Health Connect. It only reads them, never changes them, and you choose what to allow. This is optional.",
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = onConnect) { Text("Connect Health Connect") }
        TextButton(onClick = onNotNow) { Text("Not now") }
    }
}

@Composable
private fun Ready(vm: AppViewModel, state: AppState, ui: HealthUi.Ready, onRefresh: () -> Unit) {
    val context = LocalContext.current
    val goal = state.prefs.stepGoal.coerceIn(GOAL_RANGE)
    val numbers = NumberFormat.getIntegerInstance()
    val today = ui.summary.forDay(state.today)

    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Steps today", style = MaterialTheme.typography.titleMedium)
            if (today?.steps == null) {
                Text("No steps recorded yet today.", style = MaterialTheme.typography.bodyMedium)
            } else {
                Text(numbers.format(today.steps), style = MaterialTheme.typography.displaySmall)
                LinearProgressIndicator(progress = { (today.steps / goal.toFloat()).coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth())
                Text("of your ${numbers.format(goal)} step goal", style = MaterialTheme.typography.bodySmall)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Daily goal", modifier = Modifier.weight(1f))
                IconButton(onClick = { vm.updatePrefs { it.copy(stepGoal = (goal - 500).coerceIn(GOAL_RANGE)) } }, enabled = goal > GOAL_RANGE.first) {
                    Icon(Icons.Outlined.Remove, contentDescription = "Lower step goal")
                }
                Text(numbers.format(goal), style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = { vm.updatePrefs { it.copy(stepGoal = (goal + 500).coerceIn(GOAL_RANGE)) } }, enabled = goal < GOAL_RANGE.last) {
                    Icon(Icons.Outlined.Add, contentDescription = "Raise step goal")
                }
            }
        }
    }
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Last night", style = MaterialTheme.typography.titleMedium)
            val minutes = today?.nightSleepMinutes
            if (minutes == null) Text("No sleep recorded for last night.", style = MaterialTheme.typography.bodyMedium)
            else Text(formatSleep(minutes), style = MaterialTheme.typography.displaySmall)
        }
    }
    SectionTitle("Last 7 days")
    ui.summary.days.reversed().forEach { DayRow(day = it, today = state.today, goal = goal, numbers = numbers) }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(onClick = onRefresh) { Text("Refresh") }
        TextButton(onClick = {
            runCatching { context.startActivity(HealthConnectClient.getHealthConnectManageDataIntent(context).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
        }) { Text("Manage access") }
    }
}

@Composable
private fun DayRow(day: DayHealth, today: java.time.LocalDate, goal: Int, numbers: NumberFormat) {
    Column(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(Modifier.fillMaxWidth()) {
            Text(dayLabel(day.day, today), modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
            Text(
                listOf(day.steps?.let { "${numbers.format(it)} steps" } ?: "no steps", day.nightSleepMinutes?.let { formatSleep(it) + " sleep" } ?: "no sleep").joinToString(" · "),
                style = MaterialTheme.typography.bodySmall,
            )
        }
        LinearProgressIndicator(progress = { ((day.steps ?: 0L) / goal.toFloat()).coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth().padding(top = 2.dp))
    }
}

/** A slim summary card for the Progress tab: today's steps and last night's sleep, or a gentle invitation to connect. */
@Composable
fun HealthCard(vm: AppViewModel, state: AppState, nav: NavHostController) {
    val ui = rememberHealthUi(vm, state)
    val numbers = NumberFormat.getIntegerInstance()
    when (ui) {
        is HealthUi.Ready -> {
            val today = ui.summary.forDay(state.today)
            HealthCardFrame(nav) {
                Text("Steps and sleep", style = MaterialTheme.typography.titleSmall)
                Text(
                    listOf(
                        today?.steps?.let { "${numbers.format(it)} steps today" } ?: "No steps yet today",
                        today?.nightSleepMinutes?.let { "${formatSleep(it)} last night" } ?: "no sleep recorded",
                    ).joinToString(" · "),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        HealthUi.NeedsPermission -> HealthCardFrame(nav) {
            Text("Steps and sleep (optional)", style = MaterialTheme.typography.titleSmall)
            Text("Connect Health Connect to see them here.", style = MaterialTheme.typography.bodyMedium)
        }
        // Nothing to offer, so the card stays out of the way.
        else -> Unit
    }
}

@Composable
private fun HealthCardFrame(nav: NavHostController, content: @Composable () -> Unit) {
    Card(
        onClick = { nav.navigate(io.github.sreepv43.fablife.ui.Routes.HEALTH) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp),
    ) { Column(Modifier.padding(16.dp)) { content() } }
}
