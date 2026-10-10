package io.github.sreepv43.fablife.ui.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.sreepv43.fablife.content.Ritual
import io.github.sreepv43.fablife.data.CheckInStatus
import io.github.sreepv43.fablife.data.HabitEntity
import io.github.sreepv43.fablife.ui.AppState
import io.github.sreepv43.fablife.ui.AppViewModel
import io.github.sreepv43.fablife.ui.SectionTitle
import io.github.sreepv43.fablife.ui.label
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private fun HabitEntity.createdDay(): LocalDate = Instant.ofEpochMilli(createdAtMillis).atZone(ZoneId.systemDefault()).toLocalDate()

/** Share (0..1) of the habits that existed on [day] that were done that day; null when none existed. */
private fun AppState.completion(day: LocalDate): Float? {
    val scheduled = habits.filter { !it.createdDay().isAfter(day) }
    if (scheduled.isEmpty() || day.isAfter(today)) return null
    val done = doneByDay[day].orEmpty()
    return scheduled.count { it.id in done } / scheduled.size.toFloat()
}

@Composable
fun ProgressScreen(vm: AppViewModel, state: AppState) {
    var month by rememberSaveable { mutableStateOf(YearMonth.from(state.today).toString()) }
    var selectedDay by rememberSaveable { mutableStateOf<String?>(null) }
    val ym = YearMonth.parse(month)
    val doneCheckIns = state.checkIns.filter { it.status == CheckInStatus.DONE }

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        item { Text("Your progress", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp)) }
        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer), modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp)) {
                Column(Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { month = ym.minusMonths(1).toString() }) { Icon(Icons.AutoMirrored.Outlined.KeyboardArrowLeft, "Previous month") }
                        Text(
                            ym.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + ym.year,
                            style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center, modifier = Modifier.weight(1f),
                        )
                        IconButton(onClick = { month = ym.plusMonths(1).toString() }, enabled = ym.isBefore(YearMonth.from(state.today))) {
                            Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, "Next month")
                        }
                    }
                    CalendarGrid(state, ym) { selectedDay = it.toString() }
                    Legend()
                }
            }
        }
        item {
            Column(Modifier.fillMaxWidth().widthIn(max = 640.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Streaks for the ritual with the most habits; falls back to the morning ritual.
                val focus = Ritual.entries.maxByOrNull { state.habitsFor(it).size }?.takeIf { state.habitsFor(it).isNotEmpty() } ?: Ritual.MORNING
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Stat("🔥 ${state.streak(focus)}", "${focus.label} streak", Modifier.weight(1f))
                    Stat("🏆 ${state.longestStreak(focus)}", "Longest ${focus.label.lowercase()} streak", Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Stat("${doneCheckIns.size}", "Total check-ins", Modifier.weight(1f))
                    Stat("${doneCheckIns.map { it.day }.toSet().size}", "Days active", Modifier.weight(1f))
                }
                Stat("${state.completedSteps} / ${state.activeJourney.steps.size}", "${state.activeJourney.title}: steps completed", Modifier.fillMaxWidth())
            }
        }
        if (state.habits.isNotEmpty()) item {
            Column(Modifier.fillMaxWidth().widthIn(max = 640.dp)) {
                SectionTitle("Last 30 days")
                state.habits.sortedWith(compareBy({ it.ritual }, { it.order })).forEach { habit ->
                    val start = maxOf(habit.createdDay(), state.today.minusDays(29))
                    val days = (java.time.temporal.ChronoUnit.DAYS.between(start, state.today) + 1).coerceAtLeast(1)
                    val done = doneCheckIns.count { it.habitId == habit.id && !it.day.isBefore(start) }
                    val rate = (done / days.toFloat()).coerceAtMost(1f)
                    Column(Modifier.padding(vertical = 6.dp)) {
                        Row {
                            Text(habit.title, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                            Text("${(rate * 100).toInt()}%", style = MaterialTheme.typography.labelLarge)
                        }
                        LinearProgressIndicator(progress = { rate }, modifier = Modifier.fillMaxWidth().padding(top = 4.dp))
                    }
                }
            }
        }
    }

    selectedDay?.let { DayDialog(vm, state, LocalDate.parse(it)) { selectedDay = null } }
}

@Composable
private fun CalendarGrid(state: AppState, ym: YearMonth, onDay: (LocalDate) -> Unit) {
    val first = ym.atDay(1)
    val leading = first.dayOfWeek.value - 1 // Monday first
    val cells = leading + ym.lengthOfMonth()
    val rows = (cells + 6) / 7
    val scheme = MaterialTheme.colorScheme
    Row(Modifier.fillMaxWidth()) {
        listOf("M", "T", "W", "T", "F", "S", "S").forEach {
            Text(it, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, modifier = Modifier.weight(1f))
        }
    }
    for (r in 0 until rows) {
        Row(Modifier.fillMaxWidth()) {
            for (c in 0 until 7) {
                val dayNumber = r * 7 + c - leading + 1
                Box(Modifier.weight(1f).aspectRatio(1f).padding(3.dp), contentAlignment = Alignment.Center) {
                    if (dayNumber in 1..ym.lengthOfMonth()) {
                        val day = ym.atDay(dayNumber)
                        val pct = state.completion(day)
                        val color = when {
                            pct == null -> scheme.surface
                            pct == 0f -> scheme.surfaceVariant
                            pct < 0.5f -> scheme.primary.copy(alpha = 0.3f)
                            pct < 1f -> scheme.primary.copy(alpha = 0.6f)
                            else -> scheme.primary
                        }
                        Box(
                            Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(8.dp)).background(color)
                                .clickable(enabled = !day.isAfter(state.today), role = Role.Button, onClickLabel = "Show ${day.format(DateTimeFormatter.ISO_DATE)}") { onDay(day) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                "$dayNumber", style = MaterialTheme.typography.labelMedium,
                                color = if (pct != null && pct >= 0.5f) scheme.onPrimary else scheme.onSurface,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Legend() {
    val scheme = MaterialTheme.colorScheme
    Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("Less", style = MaterialTheme.typography.labelSmall)
        listOf(scheme.surfaceVariant, scheme.primary.copy(alpha = 0.3f), scheme.primary.copy(alpha = 0.6f), scheme.primary).forEach {
            Box(Modifier.size(14.dp).clip(RoundedCornerShape(3.dp)).background(it))
        }
        Text("All habits done", style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun Stat(value: String, label: String, modifier: Modifier) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer), modifier = modifier) {
        Column(Modifier.padding(16.dp)) {
            Text(value, style = MaterialTheme.typography.headlineSmall)
            Text(label, style = MaterialTheme.typography.bodySmall)
        }
    }
}

/** Check-ins for one day; today and yesterday can be edited. */
@Composable
private fun DayDialog(vm: AppViewModel, state: AppState, day: LocalDate, onDismiss: () -> Unit) {
    val editable = day == state.today || day == state.today.minusDays(1)
    val habits = state.habits.filter { !it.createdDay().isAfter(day) }.sortedWith(compareBy({ it.ritual }, { it.order }))
    val done = state.doneByDay[day].orEmpty()
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(day.format(DateTimeFormatter.ofPattern("EEEE, d MMMM"))) },
        text = {
            Column {
                if (habits.isEmpty()) Text("No habits yet on this day.")
                habits.forEach { h ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = h.id in done, enabled = editable, onCheckedChange = { vm.setCheckIn(h.id, day, it) })
                        Text("${h.title} (${h.ritual.label.lowercase()})")
                    }
                }
                if (!editable && habits.isNotEmpty()) Text("Only today and yesterday can be changed.", style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } },
    )
}
