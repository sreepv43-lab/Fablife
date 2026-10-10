package io.github.sreepv43.fablife.ui.settings

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.github.sreepv43.fablife.content.Allergen
import io.github.sreepv43.fablife.content.DietType
import io.github.sreepv43.fablife.content.FitnessLevel
import io.github.sreepv43.fablife.content.MovementNeeds
import io.github.sreepv43.fablife.content.Ritual
import io.github.sreepv43.fablife.data.ThemeMode
import io.github.sreepv43.fablife.ui.AppState
import io.github.sreepv43.fablife.ui.AppViewModel
import io.github.sreepv43.fablife.ui.Routes
import io.github.sreepv43.fablife.ui.SectionTitle
import io.github.sreepv43.fablife.ui.TimeField
import io.github.sreepv43.fablife.ui.emoji
import io.github.sreepv43.fablife.ui.label
import io.github.sreepv43.fablife.ui.onboarding.allergenLabel
import io.github.sreepv43.fablife.ui.onboarding.dietLabel
import io.github.sreepv43.fablife.ui.pretty
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(vm: AppViewModel, state: AppState, nav: NavHostController) {
    val p = state.prefs
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var editName by rememberSaveable { mutableStateOf(false) }
    var confirmReset by rememberSaveable { mutableStateOf(false) }
    val export = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) scope.launch {
            val json = vm.exportJson()
            context.contentResolver.openOutputStream(uri)?.use { it.write(json.toByteArray()) }
            Toast.makeText(context, "Your data was exported", Toast.LENGTH_SHORT).show()
        }
    }

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        item { Text("Settings", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp)) }
        item {
            Section("Profile") {
                Row(Modifier.fillMaxWidth().clickable(role = Role.Button) { editName = true }.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("Name", modifier = Modifier.weight(1f))
                    Text(p.name.ifBlank { "Not set" }, color = MaterialTheme.colorScheme.primary)
                }
                TimeField("Wake-up time", p.wakeTime) { t -> vm.updatePrefs { it.copy(wakeTime = t) } }
            }
        }
        item {
            Section("Journey") {
                Row(
                    Modifier.fillMaxWidth().clickable(role = Role.Button) { nav.navigate(Routes.JOURNEYS) }.padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("${state.activeJourney.emoji} ${state.activeJourney.title}")
                        Text("Switch or start another journey", style = MaterialTheme.typography.bodySmall)
                    }
                    Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, contentDescription = null)
                }
            }
        }
        item {
            Section("Rituals & reminders") {
                Text("At most one gentle reminder per ritual per day, only if the ritual isn't done yet.", style = MaterialTheme.typography.bodySmall)
                Ritual.entries.forEach { ritual ->
                    val s = state.ritualSettings[ritual] ?: return@forEach
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
                        Text("${ritual.emoji} ${ritual.label} reminder", modifier = Modifier.weight(1f))
                        Switch(checked = s.reminderEnabled, onCheckedChange = { vm.updateRitualSettings(s.copy(reminderEnabled = it)) })
                    }
                    if (s.reminderEnabled) TimeField("${ritual.label} time", s.reminderTime) { vm.updateRitualSettings(s.copy(reminderTime = it)) }
                }
                Row(
                    Modifier.fillMaxWidth().clickable(role = Role.Button) { nav.navigate(Routes.HABITS) }.padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("My habits", modifier = Modifier.weight(1f))
                    Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, contentDescription = null)
                }
            }
        }
        item {
            Section("Food preferences") {
                Text("Diet", style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DietType.entries.forEach { d ->
                        FilterChip(selected = p.dietType == d, onClick = { vm.updatePrefs { it.copy(dietType = d) } }, label = { Text(dietLabel(d).substringBefore(" (")) })
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 8.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text("Gentle on the stomach (low-fibre)")
                        Text("Easy-to-digest meal ideas. Best for short periods or when advised by your doctor.", style = MaterialTheme.typography.bodySmall)
                    }
                    Switch(checked = p.gentleStomach, onCheckedChange = { vm.setGentle(it, state.today) })
                }
                Text("Foods to avoid", style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Allergen.entries.forEach { a ->
                        FilterChip(
                            selected = a in p.avoidFoods,
                            onClick = { vm.updatePrefs { it.copy(avoidFoods = if (a in it.avoidFoods) it.avoidFoods - a else it.avoidFoods + a) } },
                            label = { Text(allergenLabel(a)) },
                        )
                    }
                }
            }
        }
        item {
            Section("Movement preferences") {
                Text("Fitness level", style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FitnessLevel.entries.forEach { l ->
                        FilterChip(selected = p.fitnessLevel == l, onClick = { vm.updatePrefs { it.copy(fitnessLevel = l) } }, label = { Text(l.pretty()) })
                    }
                }
                Text("Movement needs", style = MaterialTheme.typography.labelLarge)
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MovementNeeds.entries.forEach { n ->
                        val text = when (n) {
                            MovementNeeds.STANDARD -> "Standard"
                            MovementNeeds.LOW_IMPACT -> "Low-impact"
                            MovementNeeds.SEATED -> "Seated"
                        }
                        FilterChip(selected = p.movementNeeds == n, onClick = { vm.updatePrefs { it.copy(movementNeeds = n) } }, label = { Text(text) })
                    }
                }
            }
        }
        item {
            Section("Your day") {
                Text(
                    "When does your day start? Check-ins before this time count for the day before, so a late night doesn't break your streak.",
                    style = MaterialTheme.typography.bodySmall,
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    (0..5).forEach { hour ->
                        FilterChip(
                            selected = p.dayStartOffsetHours == hour,
                            onClick = { vm.updatePrefs { it.copy(dayStartOffsetHours = hour) } },
                            label = { Text(if (hour == 0) "Midnight" else "$hour AM") },
                        )
                    }
                }
            }
        }
        item {
            Section("Appearance") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ThemeMode.entries.forEach { t ->
                        FilterChip(selected = p.theme == t, onClick = { vm.updatePrefs { it.copy(theme = t) } }, label = { Text(t.pretty()) })
                    }
                }
            }
        }
        item {
            Section("Your data") {
                Text("Everything stays on this device. No account, no ads.", style = MaterialTheme.typography.bodySmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                    OutlinedButton(onClick = { export.launch("fablife-export.json") }) { Text("Export my data") }
                    TextButton(onClick = { confirmReset = true }) { Text("Reset all progress", color = MaterialTheme.colorScheme.error) }
                }
            }
        }
        item {
            Text(
                "Fab Life provides general wellness information, not medical advice. Version 0.1.0",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp).padding(vertical = 8.dp),
            )
        }
    }

    if (editName) {
        var value by rememberSaveable { mutableStateOf(p.name) }
        AlertDialog(
            onDismissRequest = { editName = false },
            title = { Text("Your name") },
            text = { OutlinedTextField(value = value, onValueChange = { value = it.take(40) }, label = { Text("First name") }, singleLine = true) },
            confirmButton = { TextButton(onClick = { vm.updatePrefs { it.copy(name = value.trim()) }; editName = false }) { Text("Save") } },
            dismissButton = { TextButton(onClick = { editName = false }) { Text("Cancel") } },
        )
    }
    if (confirmReset) {
        var typed by rememberSaveable { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { confirmReset = false },
            title = { Text("Reset all progress?") },
            text = {
                Column {
                    Text("This deletes your habits, check-ins, journey progress and preferences. It can't be undone. Type RESET to confirm.")
                    OutlinedTextField(value = typed, onValueChange = { typed = it }, singleLine = true, label = { Text("Type RESET") }, modifier = Modifier.padding(top = 8.dp))
                }
            },
            confirmButton = {
                TextButton(enabled = typed == "RESET", onClick = { vm.resetAll(); confirmReset = false }) {
                    Text("Reset everything", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { confirmReset = false }) { Text("Keep my data") } },
        )
    }
}

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer), modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp)) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            SectionTitle(title)
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsScreen(vm: AppViewModel, state: AppState, nav: NavHostController) {
    var adding by rememberSaveable { mutableStateOf<Ritual?>(null) }
    var deleting by rememberSaveable { mutableStateOf<String?>(null) }
    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("My habits") },
            navigationIcon = { IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back") } },
        )
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            item { Text("Arrange each ritual in the order that fits your real day.", modifier = Modifier.widthIn(max = 640.dp).fillMaxWidth()) }
            Ritual.entries.forEach { ritual ->
                item(ritual.name) {
                    Section("${ritual.emoji} ${ritual.label}") {
                        val habits = state.habitsFor(ritual)
                        if (habits.isEmpty()) Text("No habits yet.", style = MaterialTheme.typography.bodyMedium)
                        habits.forEachIndexed { i, h ->
                            if (i > 0) HorizontalDivider()
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(h.title, modifier = Modifier.weight(1f))
                                IconButton(onClick = { vm.moveHabit(h.id, up = true) }, enabled = i > 0) { Icon(Icons.Outlined.KeyboardArrowUp, "Move ${h.title} up") }
                                IconButton(onClick = { vm.moveHabit(h.id, up = false) }, enabled = i < habits.size - 1) { Icon(Icons.Outlined.KeyboardArrowDown, "Move ${h.title} down") }
                                IconButton(onClick = { deleting = h.id }) { Icon(Icons.Outlined.Delete, "Remove ${h.title}") }
                            }
                        }
                        TextButton(onClick = { adding = ritual }) {
                            Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text("Add habit", modifier = Modifier.padding(start = 4.dp))
                        }
                    }
                }
            }
        }
    }
    adding?.let { ritual ->
        var title by rememberSaveable { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { adding = null },
            title = { Text("New ${ritual.label.lowercase()} habit") },
            text = { OutlinedTextField(value = title, onValueChange = { title = it.take(60) }, label = { Text("Habit") }, singleLine = true) },
            confirmButton = { TextButton(enabled = title.isNotBlank(), onClick = { vm.addHabit(title, ritual); adding = null }) { Text("Add habit") } },
            dismissButton = { TextButton(onClick = { adding = null }) { Text("Cancel") } },
        )
    }
    deleting?.let { id ->
        val habit = state.habits.firstOrNull { it.id == id }
        AlertDialog(
            onDismissRequest = { deleting = null },
            title = { Text("Remove “${habit?.title}”?") },
            text = { Text("Its check-in history will be deleted. Completed journey steps are not affected.") },
            confirmButton = { TextButton(onClick = { vm.deleteHabit(id); deleting = null }) { Text("Remove habit", color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { deleting = null }) { Text("Keep it") } },
        )
    }
}
