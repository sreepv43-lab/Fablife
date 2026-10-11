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
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.navigation.NavHostController
import io.github.sreepv43.fablife.data.JournalEntryEntity
import io.github.sreepv43.fablife.data.JournalKind
import io.github.sreepv43.fablife.engine.JournalPrompts
import io.github.sreepv43.fablife.engine.Mood
import io.github.sreepv43.fablife.ui.AppState
import io.github.sreepv43.fablife.ui.AppViewModel
import io.github.sreepv43.fablife.ui.SectionTitle
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val KINDS = JournalKind.entries

private fun JournalKind.label() = when (this) {
    JournalKind.GRATITUDE -> "Gratitude"
    JournalKind.REFLECTION -> "Reflection"
    JournalKind.MOOD -> "Mood"
}

private fun JournalKind.emoji() = when (this) {
    JournalKind.GRATITUDE -> "🙏"
    JournalKind.REFLECTION -> "📝"
    JournalKind.MOOD -> "🙂"
}

internal fun dayLabel(day: LocalDate, today: LocalDate): String = when (day) {
    today -> "Today"
    today.minusDays(1) -> "Yesterday"
    else -> day.format(DateTimeFormatter.ofPattern(if (day.year == today.year) "EEE d MMM" else "EEE d MMM yyyy", Locale.getDefault()))
}

/** Three small ways to write: gratitude, a reflection prompt, or a mood check. Everything stays on the device. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(vm: AppViewModel, state: AppState, kind: JournalKind, habitId: String?, nav: NavHostController) {
    var current by rememberSaveable { mutableStateOf(kind) }
    var saved by rememberSaveable { mutableStateOf(false) }
    var deleting by remember { mutableStateOf<JournalEntryEntity?>(null) }
    val entries = state.journal.sortedByDescending { it.createdAtMillis }

    Column(Modifier.fillMaxSize()) {
        ToolTopBar("Journal", nav)
        if (saved && habitId != null) {
            ToolFinished(vm, state, habitId, nav, "📝", "Saved.", "Writing it down is the habit. It stays on your device.")
            return@Column
        }
        SecondaryTabRow(selectedTabIndex = KINDS.indexOf(current)) {
            KINDS.forEach { k -> Tab(selected = k == current, onClick = { current = k; saved = false }, text = { Text(k.label()) }) }
        }
        LazyColumn(
            contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize(),
        ) {
            item(key = "composer") {
                Column(Modifier.fillMaxWidth().widthIn(max = 640.dp)) {
                    when (current) {
                        JournalKind.GRATITUDE -> GratitudeComposer(state.today) { text -> vm.addJournalEntry(current, text); saved = true }
                        JournalKind.REFLECTION -> ReflectionComposer(state.today) { text -> vm.addJournalEntry(current, text); saved = true }
                        JournalKind.MOOD -> MoodComposer { mood, note -> vm.addJournalEntry(current, note, mood); saved = true }
                    }
                    if (saved) Text("Saved ✓", color = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(top = 8.dp))
                    Text(
                        "Entries stay on this device and are never shared.",
                        style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp),
                    )
                }
            }
            item(key = "history") { SectionTitle("Past entries", Modifier.fillMaxWidth().widthIn(max = 640.dp)) }
            if (entries.isEmpty()) {
                item(key = "empty") {
                    Text("Nothing here yet. What you write will show up here.", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp))
                }
            }
            entries.groupBy { it.day }.forEach { (day, list) ->
                item(key = "day-$day") {
                    Text(dayLabel(day, state.today), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary, modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp))
                }
                list.forEach { e ->
                    item(key = "entry-${e.id}") { EntryCard(e, onDelete = { deleting = e }) }
                }
            }
        }
    }

    deleting?.let { e ->
        AlertDialog(
            onDismissRequest = { deleting = null },
            title = { Text("Delete this entry?") },
            text = { Text("It will be removed from this device. This can't be undone.") },
            confirmButton = { TextButton(onClick = { vm.deleteJournalEntry(e.id); deleting = null }) { Text("Delete entry") } },
            dismissButton = { TextButton(onClick = { deleting = null }) { Text("Keep it") } },
        )
    }
}

@Composable
private fun GratitudeComposer(today: LocalDate, onSave: (String) -> Unit) {
    var a by rememberSaveable { mutableStateOf("") }
    var b by rememberSaveable { mutableStateOf("") }
    var c by rememberSaveable { mutableStateOf("") }
    Text("Three small things you're grateful for", style = MaterialTheme.typography.titleMedium)
    Text("Tiny is fine: a warm cup of tea, a text from a friend.", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 8.dp))
    listOf(Triple("I'm grateful for…", a) { v: String -> a = v }, Triple("And…", b) { v: String -> b = v }, Triple("And…", c) { v: String -> c = v }).forEach { (label, value, set) ->
        OutlinedTextField(
            value = value, onValueChange = set, label = { Text(label) }, singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        )
    }
    val text = JournalPrompts.joinLines(listOf(a, b, c))
    Button(onClick = { onSave(text); a = ""; b = ""; c = "" }, enabled = text.isNotEmpty()) { Text("Save gratitude") }
}

@Composable
private fun ReflectionComposer(today: LocalDate, onSave: (String) -> Unit) {
    var shift by rememberSaveable { mutableIntStateOf(0) }
    var text by rememberSaveable { mutableStateOf("") }
    val prompt = JournalPrompts.forDay(today, shift)
    Text(prompt, style = MaterialTheme.typography.titleMedium)
    TextButton(onClick = { shift++ }) { Text("Another prompt") }
    OutlinedTextField(
        value = text, onValueChange = { text = it }, label = { Text("Your thoughts") }, minLines = 4,
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
    )
    Button(onClick = { onSave("$prompt\n${text.trim()}"); text = "" }, enabled = text.isNotBlank()) { Text("Save reflection") }
}

@Composable
private fun MoodComposer(onSave: (Int, String) -> Unit) {
    var mood by rememberSaveable { mutableIntStateOf(0) }
    var note by rememberSaveable { mutableStateOf("") }
    Text("How are you feeling right now?", style = MaterialTheme.typography.titleMedium)
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Mood.FACES.forEachIndexed { i, face ->
            val value = i + 1
            Column(
                Modifier.selectable(selected = mood == value, role = Role.RadioButton, onClick = { mood = value })
                    .semantics { contentDescription = Mood.label(value) }.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(face, fontSize = if (mood == value) 40.sp else 30.sp)
                Text(Mood.label(value), style = MaterialTheme.typography.labelSmall, color = if (mood == value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
            }
        }
    }
    OutlinedTextField(
        value = note, onValueChange = { note = it }, label = { Text("A note (optional)") }, minLines = 2,
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
    )
    Text(
        "A mood check is just a note to yourself. If low moods are lasting or heavy, please talk to someone you trust or a health professional.",
        style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 8.dp),
    )
    Button(onClick = { onSave(mood, note.trim()); mood = 0; note = "" }, enabled = mood in 1..5) { Text("Save mood") }
}

@Composable
private fun EntryCard(e: JournalEntryEntity, onDelete: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp),
    ) {
        Row(Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp), verticalAlignment = Alignment.Top) {
            Column(Modifier.weight(1f).padding(top = 8.dp)) {
                Text("${e.kind.emoji()} ${e.kind.label()}", style = MaterialTheme.typography.labelMedium)
                when (e.kind) {
                    JournalKind.GRATITUDE -> JournalPrompts.splitLines(e.text).forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
                    JournalKind.MOOD -> {
                        Text("${Mood.face(e.mood)} ${Mood.label(e.mood)}", style = MaterialTheme.typography.bodyLarge)
                        if (e.text.isNotBlank()) Text(e.text, style = MaterialTheme.typography.bodyMedium)
                    }
                    JournalKind.REFLECTION -> Text(e.text, style = MaterialTheme.typography.bodyMedium)
                }
            }
            IconButton(onClick = onDelete) { Icon(Icons.Outlined.Delete, contentDescription = "Delete entry from ${e.day}") }
        }
    }
}
