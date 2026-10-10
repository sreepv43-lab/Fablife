package io.github.sreepv43.fablife.ui.journey

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.github.sreepv43.fablife.content.Goal
import io.github.sreepv43.fablife.data.StepState
import io.github.sreepv43.fablife.ui.AppState
import io.github.sreepv43.fablife.ui.AppViewModel
import io.github.sreepv43.fablife.ui.MarkdownText
import io.github.sreepv43.fablife.ui.Routes
import io.github.sreepv43.fablife.ui.StepView
import io.github.sreepv43.fablife.ui.fillPlaceholders
import io.github.sreepv43.fablife.ui.goToToday

@Composable
fun JourneyScreen(vm: AppViewModel, state: AppState, nav: NavHostController) {
    val journey = vm.content.journey
    var confirmSkip by remember { mutableStateOf<StepView?>(null) }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        item {
            Column(Modifier.fillMaxWidth().widthIn(max = 640.dp).padding(bottom = 8.dp)) {
                Text("☀️", style = MaterialTheme.typography.displaySmall)
                Text(journey.title, style = MaterialTheme.typography.headlineMedium)
                Text(journey.subtitle, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(12.dp))
                Text("Step ${minOf(state.completedSteps + 1, state.steps.size)} of ${state.steps.size}", style = MaterialTheme.typography.labelLarge)
                LinearProgressIndicator(
                    progress = { state.completedSteps / state.steps.size.coerceAtLeast(1).toFloat() },
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                )
            }
        }
        item { LetterRow("Your Energy Is a Skill", "Introduction") { nav.navigate(Routes.letter("intro")) } }
        itemsIndexed(state.steps) { i, sv -> StepRow(i + 1, sv, nav, onSkip = { confirmSkip = sv }) }
        if (state.journeyComplete) item { LetterRow(journey.outroLetter.title, "Final letter") { nav.navigate(Routes.letter("outro")) } }
    }
    confirmSkip?.let { sv ->
        AlertDialog(
            onDismissRequest = { confirmSkip = null },
            title = { Text("Skip ahead?") },
            text = { Text("We recommend practising a habit for a few days before adding the next. Skip anyway?") },
            confirmButton = { TextButton(onClick = { vm.skipStep(sv.step.id); confirmSkip = null }) { Text("Skip ahead") } },
            dismissButton = { TextButton(onClick = { confirmSkip = null }) { Text("Keep practising") } },
        )
    }
}

@Composable
private fun LetterRow(title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp).clickable(role = Role.Button, onClick = onClick),
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.MailOutline, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Column(Modifier.padding(start = 16.dp)) {
                Text(subtitle, style = MaterialTheme.typography.labelMedium)
                Text(title, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun StepRow(number: Int, sv: StepView, nav: NavHostController, onSkip: () -> Unit) {
    val state = sv.progress.state
    val locked = state == StepState.LOCKED
    val current = state == StepState.UNLOCKED || state == StepState.ACTIVE
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (current) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
        ),
        modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp)
            .clickable(enabled = !locked, role = Role.Button, onClickLabel = "Read letter") { nav.navigate(Routes.letter(sv.step.id)) },
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                when {
                    state == StepState.COMPLETED -> Icon(Icons.Filled.CheckCircle, contentDescription = "Completed", tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(32.dp))
                    locked -> Icon(Icons.Outlined.Lock, contentDescription = "Locked", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    else -> {
                        CircularProgressIndicator(progress = { sv.doneCount / sv.target.toFloat() }, modifier = Modifier.size(36.dp))
                        Text("$number", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
            Column(Modifier.weight(1f).padding(start = 16.dp)) {
                Text("Step $number", style = MaterialTheme.typography.labelMedium)
                Text(sv.step.title, style = MaterialTheme.typography.titleMedium)
                val detail = when {
                    locked -> "Unlocks later"
                    state == StepState.UNLOCKED -> "New letter waiting ✉️"
                    state == StepState.COMPLETED -> "Completed"
                    sv.step.goal is Goal.Action -> "One-time action"
                    else -> "${sv.doneCount} of ${sv.target} days"
                }
                Text(detail, style = MaterialTheme.typography.bodySmall)
            }
            if (state == StepState.ACTIVE) TextButton(onClick = onSkip) { Text("Skip ahead") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LetterScreen(vm: AppViewModel, state: AppState, id: String, nav: NavHostController) {
    val letter = vm.letter(id)
    val stepView = state.steps.firstOrNull { it.step.id == id }
    LaunchedEffect(id) {
        when (id) {
            "outro" -> vm.updatePrefs { it.copy(outroSeen = true) }
            "gentle" -> vm.updatePrefs { it.copy(gentleGuidanceSeen = true) }
            "welcome" -> vm.updatePrefs { it.copy(welcomeBackPending = false) }
        }
    }
    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(if (stepView != null) "Step ${state.steps.indexOf(stepView) + 1}" else "Letter") },
            navigationIcon = { IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back") } },
        )
        if (letter == null || stepView?.progress?.state == StepState.LOCKED) {
            Text("This letter unlocks later.", modifier = Modifier.padding(24.dp))
            return
        }
        Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(Modifier.widthIn(max = 600.dp)) {
                Text(letter.title, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(vertical = 16.dp))
                val note = stepView?.step?.gentleNote
                if (note != null && state.prefs.gentleStomach) {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer), modifier = Modifier.padding(bottom = 16.dp)) {
                        Text("🌿 $note", modifier = Modifier.padding(16.dp))
                    }
                }
                MarkdownText(fillPlaceholders(letter.body, state.placeholders))
                Spacer(Modifier.height(24.dp))
            }
        }
        Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.End) {
            when {
                id == "intro" -> {
                    val first = state.steps.firstOrNull()
                    Button(onClick = {
                        if (first != null && first.progress.state == StepState.UNLOCKED) {
                            nav.navigate(Routes.letter(first.step.id)) { popUpTo("letter/intro") { inclusive = true } }
                        } else nav.popBackStack()
                    }) { Text("Let's begin") }
                }
                stepView != null && stepView.progress.state == StepState.UNLOCKED -> Button(onClick = {
                    vm.acceptStep(id)
                    nav.goToToday()
                }) { Text(stepView.step.buttonLabel) }
                id == "outro" -> {
                    OutlinedButton(onClick = { nav.popBackStack() }, modifier = Modifier.padding(end = 8.dp)) { Text("Celebrate 🎉") }
                    Button(onClick = { nav.goToToday() }) { Text("Keep my ritual going") }
                }
                id == "welcome" -> Button(onClick = { nav.goToToday() }) { Text("Let's start again") }
                id == "missed" -> Button(onClick = { nav.popBackStack() }) { Text("Keep going") }
                id == "gentle" -> Button(onClick = { nav.popBackStack() }) { Text("Got it") }
                else -> OutlinedButton(onClick = { nav.popBackStack() }) { Text("Back") }
            }
        }
    }
}
