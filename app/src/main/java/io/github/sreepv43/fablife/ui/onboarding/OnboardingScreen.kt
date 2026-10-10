package io.github.sreepv43.fablife.ui.onboarding

import android.Manifest
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.sreepv43.fablife.content.Allergen
import io.github.sreepv43.fablife.content.DietType
import io.github.sreepv43.fablife.content.FitnessLevel
import io.github.sreepv43.fablife.content.MovementNeeds
import io.github.sreepv43.fablife.ui.AppViewModel
import io.github.sreepv43.fablife.ui.TimeField
import io.github.sreepv43.fablife.ui.pretty
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

private const val PAGES = 9

private val GOALS = listOf(
    "energy" to "Have more energy",
    "sleep" to "Sleep better",
    "focus" to "Focus and get things done",
    "eat" to "Eat healthier",
    "calm" to "Feel calmer, less stressed",
)
private val OBSTACLES = listOf("I'm too busy", "I lose motivation", "I forget", "I start too big")

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(vm: AppViewModel) {
    var page by rememberSaveable { mutableIntStateOf(0) }
    var goal by rememberSaveable { mutableStateOf("energy") }
    var obstacles by rememberSaveable { mutableStateOf(listOf<String>()) }
    var wake by rememberSaveable { mutableStateOf(LocalTime.of(7, 0).toSecondOfDay()) }
    var bed by rememberSaveable { mutableStateOf(LocalTime.of(23, 0).toSecondOfDay()) }
    var diet by rememberSaveable { mutableStateOf(DietType.VEGETARIAN) }
    var stomach by rememberSaveable { mutableStateOf("No") }
    var avoid by rememberSaveable { mutableStateOf(listOf<Allergen>()) }
    var level by rememberSaveable { mutableStateOf(FitnessLevel.BEGINNER) }
    var needs by rememberSaveable { mutableStateOf(MovementNeeds.STANDARD) }
    var name by rememberSaveable { mutableStateOf("") }
    var signed by rememberSaveable { mutableStateOf(false) }

    fun finish() = vm.completeOnboarding {
        it.copy(
            goal = goal, obstacles = obstacles.toSet(),
            wakeTime = LocalTime.ofSecondOfDay(wake.toLong()), bedTime = LocalTime.ofSecondOfDay(bed.toLong()),
            dietType = diet, gentleStomach = stomach == "Yes", gentleSince = if (stomach == "Yes") LocalDate.now() else null,
            avoidFoods = avoid.toSet(), fitnessLevel = level, movementNeeds = needs, name = name.trim(),
        )
    }

    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { finish() }

    BackHandler(enabled = page > 0) { page-- }

    Column(Modifier.fillMaxSize().safeDrawingPadding().padding(horizontal = 24.dp, vertical = 16.dp)) {
        if (page > 0) LinearProgressIndicator(progress = { page / (PAGES - 1f) }, modifier = Modifier.fillMaxWidth())
        Column(
            Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()).padding(top = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            when (page) {
                0 -> Column(Modifier.fillMaxWidth().padding(top = 64.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("☀️", fontSize = 72.sp)
                    Spacer(Modifier.height(16.dp))
                    Text("Fab Life", style = MaterialTheme.typography.displayMedium)
                    Text("Small habits. Big change.", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(24.dp))
                    Text(
                        "Build a healthier lifestyle one tiny habit at a time, with a coach that guides you through each step.",
                        textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge,
                    )
                }
                1 -> {
                    Question("What would you like to improve most?")
                    GOALS.forEach { (id, label) -> Choice(label, goal == id) { goal = id } }
                    Hint("You can start a different journey any time from the Journey tab.")
                }
                2 -> {
                    Question("What usually gets in the way?")
                    Hint("Choose any that apply. We'll tailor tips for you.")
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OBSTACLES.forEach { o ->
                            FilterChip(selected = o in obstacles, onClick = { obstacles = obstacles.toggle(o) }, label = { Text(o) })
                        }
                    }
                }
                3 -> {
                    Question("When do you usually wake up and go to bed?")
                    Hint("We'll use this to time your ritual reminders.")
                    TimeField("Wake-up time", LocalTime.ofSecondOfDay(wake.toLong())) { wake = it.toSecondOfDay() }
                    TimeField("Bed time", LocalTime.ofSecondOfDay(bed.toLong())) { bed = it.toSecondOfDay() }
                }
                4 -> {
                    Question("How do you eat?")
                    DietType.entries.forEach { d -> Choice(dietLabel(d), diet == d) { diet = d } }
                    Spacer(Modifier.height(8.dp))
                    Question("Do high-fibre foods (salads, beans, whole grains) upset your stomach?")
                    listOf("Yes", "Sometimes", "No").forEach { s -> Choice(s, stomach == s) { stomach = s } }
                    when (stomach) {
                        "Yes" -> Hint("We'll show gentle, easy-to-digest meal ideas (low-fibre). If stomach problems continue, please see a doctor.")
                        "Sometimes" -> Hint("You can turn on Gentle on the stomach (low-fibre) any time in Explore or Settings.")
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Foods to avoid (optional)", style = MaterialTheme.typography.titleSmall)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Allergen.entries.forEach { a ->
                            FilterChip(selected = a in avoid, onClick = { avoid = avoid.toggle(a) }, label = { Text(allergenLabel(a)) })
                        }
                    }
                }
                5 -> {
                    Question("How active are you right now?")
                    Choice("Beginner: little or no regular exercise", level == FitnessLevel.BEGINNER) { level = FitnessLevel.BEGINNER }
                    Choice("Intermediate: active 1–3 times a week", level == FitnessLevel.INTERMEDIATE) { level = FitnessLevel.INTERMEDIATE }
                    Choice("Active: 4 or more times a week", level == FitnessLevel.ACTIVE) { level = FitnessLevel.ACTIVE }
                    Spacer(Modifier.height(8.dp))
                    Question("Any movement needs?")
                    Choice("Standard", needs == MovementNeeds.STANDARD) { needs = MovementNeeds.STANDARD }
                    Choice("Low-impact / joint-friendly (no jumping)", needs == MovementNeeds.LOW_IMPACT) { needs = MovementNeeds.LOW_IMPACT }
                    Choice("Seated / limited mobility", needs == MovementNeeds.SEATED) { needs = MovementNeeds.SEATED }
                    Hint(SAFETY_NOTE)
                }
                6 -> {
                    Question("What should your coach call you?")
                    OutlinedTextField(
                        value = name, onValueChange = { name = it.take(40) }, label = { Text("Your first name (optional)") },
                        singleLine = true, modifier = Modifier.fillMaxWidth(),
                    )
                }
                7 -> Commitment(signed) { signed = true }
                8 -> Column(Modifier.fillMaxWidth().padding(top = 48.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔔", fontSize = 64.sp)
                    Question("One gentle reminder")
                    Text(
                        "We'll send one gentle reminder for your morning ritual. You can change this any time.",
                        textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
        Text(
            "Fab Life gives general wellness information, not medical advice.",
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (page in 1 until PAGES) TextButton(onClick = { page-- }) { Text("Back") }
            Spacer(Modifier.weight(1f))
            if (page == 4 || page == 5 || page == 2) TextButton(onClick = { page++ }) { Text("Skip") }
            Button(
                enabled = page != 7 || signed,
                onClick = {
                    if (page < PAGES - 1) page++
                    else if (Build.VERSION.SDK_INT >= 33) permission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    else finish()
                },
            ) {
                Text(
                    when (page) {
                        0 -> "Get started"
                        PAGES - 1 -> "Turn on reminder"
                        else -> "Continue"
                    },
                )
            }
        }
        if (page == PAGES - 1) TextButton(onClick = { finish() }, modifier = Modifier.align(Alignment.End)) { Text("Not now") }
    }
}

const val SAFETY_NOTE = "Start slowly and stop if you feel pain, dizziness, chest discomfort or unusual shortness of breath. " +
    "If you are pregnant, have a medical condition, are recovering from injury or haven't exercised in a long time, " +
    "check with your doctor before starting a new exercise routine."

fun dietLabel(d: DietType) = when (d) {
    DietType.VEGETARIAN -> "Vegetarian"
    DietType.VEGAN -> "Vegan"
    DietType.EGGETARIAN -> "Eggetarian (vegetarian + eggs)"
    DietType.NON_VEG -> "Non-vegetarian"
}

fun allergenLabel(a: Allergen) = if (a == Allergen.DAIRY) "Dairy/lactose" else a.pretty()

private fun <T> List<T>.toggle(item: T) = if (item in this) this - item else this + item

@Composable
private fun Question(text: String) = Text(text, style = MaterialTheme.typography.headlineSmall)

@Composable
private fun Hint(text: String) = Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

@Composable
fun Choice(label: String, selected: Boolean, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainer,
        ),
        modifier = Modifier.fillMaxWidth().selectable(selected, role = Role.RadioButton, onClick = onClick),
    ) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = selected, onClick = null)
            Text(label, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(start = 8.dp))
        }
    }
}

/** Press and hold for 1.5 s to sign the commitment. */
@Composable
private fun Commitment(signed: Boolean, onSigned: () -> Unit) {
    val progress = remember { Animatable(if (signed) 1f else 0f) }
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    Column(Modifier.fillMaxWidth().padding(top = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Question("Your commitment")
        Spacer(Modifier.height(16.dp))
        Text(
            "“I commit to taking care of myself, one small step at a time.”",
            style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center, modifier = Modifier.widthIn(max = 420.dp),
        )
        Spacer(Modifier.height(40.dp))
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp)) {
            CircularProgressIndicator(progress = { progress.value }, modifier = Modifier.size(160.dp), strokeWidth = 8.dp)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(132.dp).background(
                    if (signed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer, CircleShape,
                ).pointerInput(signed) {
                    if (signed) return@pointerInput
                    detectTapGestures(onPress = {
                        val job = scope.launch {
                            progress.animateTo(1f, tween(((1f - progress.value) * 1500).toInt(), easing = LinearEasing))
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            onSigned()
                        }
                        val released = tryAwaitRelease()
                        if (released && progress.value < 1f) {
                            job.cancel()
                            scope.launch { progress.animateTo(0f, tween(250)) }
                        }
                    })
                },
            ) {
                Text(
                    if (signed) "Signed ✓" else "Press\n& hold",
                    textAlign = TextAlign.Center, style = MaterialTheme.typography.titleMedium,
                    color = if (signed) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
        if (!signed) Text("Hold the button to sign", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 16.dp))
    }
}

