package io.github.sreepv43.fablife.ui.explore

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.github.sreepv43.fablife.content.Fibre
import io.github.sreepv43.fablife.content.Meal
import io.github.sreepv43.fablife.content.MealType
import io.github.sreepv43.fablife.content.Place
import io.github.sreepv43.fablife.content.Routine
import io.github.sreepv43.fablife.content.RoutineGoal
import io.github.sreepv43.fablife.engine.Recommendations
import io.github.sreepv43.fablife.ui.AppState
import io.github.sreepv43.fablife.ui.AppViewModel
import io.github.sreepv43.fablife.ui.Routes
import io.github.sreepv43.fablife.ui.SectionTitle
import io.github.sreepv43.fablife.ui.onboarding.SAFETY_NOTE
import io.github.sreepv43.fablife.ui.onboarding.dietLabel
import io.github.sreepv43.fablife.ui.pretty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(vm: AppViewModel, state: AppState, nav: NavHostController) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    Column(Modifier.fillMaxSize()) {
        SecondaryTabRow(selectedTabIndex = tab) {
            Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Move") })
            Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Eat") })
        }
        if (tab == 0) MoveTab(vm, state, nav) else EatTab(vm, state, nav)
    }
}

private const val WEEKLY_TARGET = 150

@Composable
private fun MoveTab(vm: AppViewModel, state: AppState, nav: NavHostController) {
    val library = vm.content.exercise
    val p = state.prefs
    val suitable = Recommendations.routines(library.routines, p.movementNeeds, vm.content.jumpingRoutineIds)
    val recommended = suitable.filter { p.fitnessLevel in it.levels }.sortedBy { it.minutes }.take(3)
    var minutes by rememberSaveable { mutableStateOf<Int?>(null) }
    var goal by rememberSaveable { mutableStateOf<RoutineGoal?>(null) }
    var pending by rememberSaveable { mutableStateOf<String?>(null) }
    val filtered = suitable.filter { (minutes == null || it.minutes == minutes) && (goal == null || it.goal == goal) }

    fun open(routine: Routine) {
        if (p.exerciseSafetySeen) nav.navigate(Routes.routine(routine.id)) else pending = routine.id
    }

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        item {
            Column(Modifier.fillMaxWidth().widthIn(max = 640.dp)) {
                Text("Move your way: short, simple routines for every body.", style = MaterialTheme.typography.titleMedium)
            }
        }
        item { WeeklyCard(state.weeklyMinutes) }
        item { Column(Modifier.fillMaxWidth().widthIn(max = 640.dp)) { SectionTitle("Recommended for you") } }
        items(recommended, key = { "rec_" + it.id }) { RoutineCard(it) { open(it) } }
        item {
            Column(Modifier.fillMaxWidth().widthIn(max = 640.dp)) {
                SectionTitle("All routines")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listOf(5, 10, 20, 30)) { m ->
                        FilterChip(selected = minutes == m, onClick = { minutes = if (minutes == m) null else m }, label = { Text("$m min") })
                    }
                }
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(RoutineGoal.entries) { g ->
                        FilterChip(selected = goal == g, onClick = { goal = if (goal == g) null else g }, label = { Text(g.pretty()) })
                    }
                }
            }
        }
        if (filtered.isEmpty()) item {
            Text("No routines match all your filters. Try a different time or goal.", modifier = Modifier.widthIn(max = 640.dp).fillMaxWidth())
        }
        items(filtered, key = { it.id }) { RoutineCard(it) { open(it) } }
        item {
            Text(
                SAFETY_NOTE, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.widthIn(max = 640.dp).padding(top = 8.dp),
            )
        }
    }

    pending?.let { id ->
        AlertDialog(
            onDismissRequest = { pending = null },
            title = { Text("Before you start") },
            text = { Text(SAFETY_NOTE) },
            confirmButton = {
                TextButton(onClick = {
                    vm.updatePrefs { it.copy(exerciseSafetySeen = true) }
                    pending = null
                    nav.navigate(Routes.routine(id))
                }) { Text("I understand") }
            },
            dismissButton = { TextButton(onClick = { pending = null }) { Text("Cancel") } },
        )
    }
}

@Composable
private fun WeeklyCard(minutes: Int) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp),
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { (minutes / WEEKLY_TARGET.toFloat()).coerceAtMost(1f) },
                    modifier = Modifier.size(64.dp),
                    strokeWidth = 6.dp,
                    trackColor = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.15f),
                )
                Text("$minutes", style = MaterialTheme.typography.titleMedium)
            }
            Column(Modifier.padding(start = 16.dp)) {
                Text("Minutes moved this week", style = MaterialTheme.typography.titleSmall)
                Text(
                    when {
                        minutes >= WEEKLY_TARGET -> "You've reached the weekly guidance of $WEEKLY_TARGET minutes. Amazing! 💪"
                        minutes == 0 -> "Every minute counts. Health guidelines suggest about $WEEKLY_TARGET minutes a week. Start with five."
                        else -> "Great work! Building towards about $WEEKLY_TARGET minutes a week, one step at a time."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun RoutineCard(routine: Routine, onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp).clickable(role = Role.Button, onClickLabel = "Start routine", onClick = onClick),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(routine.title, style = MaterialTheme.typography.titleMedium)
            Text(
                listOf(
                    "${routine.minutes} min",
                    routine.levels.joinToString("–") { it.pretty() }.takeIf { routine.levels.size < 3 } ?: "Any level",
                    if (routine.place == Place.OUTDOORS) "Outdoors" else "Home",
                    routine.goal.pretty(),
                ).joinToString(" · "),
                style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary,
            )
            Text(routine.description, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
private fun EatTab(vm: AppViewModel, state: AppState, nav: NavHostController) {
    val food = vm.content.food
    val p = state.prefs
    var mealType by rememberSaveable { mutableStateOf(MealType.BREAKFAST) }
    val meals = Recommendations.meals(food.meals, p.dietType, p.avoidFoods, p.gentleStomach)
        .filter { it.meal == mealType }
        .sortedBy { if (it.id in p.favouriteMeals) 0 else 1 }

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        item {
            Column(Modifier.fillMaxWidth().widthIn(max = 640.dp)) {
                Text("Eat well, feel well: simple, healthy meal ideas.", style = MaterialTheme.typography.titleMedium)
                Text("Showing ${dietLabel(p.dietType).lowercase()} meals. Change this in Settings.", style = MaterialTheme.typography.bodySmall)
            }
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.widthIn(max = 640.dp)) {
                items(food.principles) { principle ->
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer), modifier = Modifier.width(240.dp)) {
                        Column(Modifier.padding(14.dp)) {
                            Text(principle.title, style = MaterialTheme.typography.titleSmall)
                            Text(principle.body, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
            }
        }
        item {
            Card(modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp)) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Gentle on the stomach (low-fibre)", style = MaterialTheme.typography.titleSmall)
                        Text("Shows easy-to-digest meals. Best for short periods or when advised by your doctor.", style = MaterialTheme.typography.bodySmall)
                        TextButton(onClick = { nav.navigate(Routes.letter("gentle")) }, contentPadding = PaddingValues(0.dp)) { Text("About gentle meals") }
                    }
                    Switch(checked = p.gentleStomach, onCheckedChange = { on ->
                        vm.setGentle(on, state.today)
                        if (on && !p.gentleGuidanceSeen) nav.navigate(Routes.letter("gentle"))
                    })
                }
            }
        }
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp)) {
                items(MealType.entries) { m ->
                    FilterChip(selected = mealType == m, onClick = { mealType = m }, label = { Text(if (m == MealType.SNACK) "Snacks" else m.pretty()) })
                }
            }
        }
        if (meals.isEmpty()) item { Text("No meals match all your filters yet. Try removing one filter in Settings.") }
        items(meals, key = { it.id }) { meal ->
            MealCard(meal, favourite = meal.id in p.favouriteMeals) {
                vm.updatePrefs { it.copy(favouriteMeals = if (meal.id in it.favouriteMeals) it.favouriteMeals - meal.id else it.favouriteMeals + meal.id) }
            }
        }
        item {
            Text(
                "Fab Life gives general healthy-eating ideas, not medical or dietary treatment. If you have a medical condition, " +
                    "are pregnant, or have ongoing stomach problems, follow the advice of your doctor or a registered dietitian.",
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.widthIn(max = 640.dp).padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun MealCard(meal: Meal, favourite: Boolean, onToggleFavourite: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth().widthIn(max = 640.dp),
    ) {
        Row(Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(meal.title, style = MaterialTheme.typography.bodyLarge)
                val tags = buildList {
                    add(dietLabel(meal.diet).substringBefore(" ("))
                    meal.prepMinutes?.let { add("$it min") }
                    if (meal.fibre == Fibre.GENTLE) add("🌿 Gentle")
                }
                Text(tags.joinToString(" · "), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onToggleFavourite) {
                Icon(
                    if (favourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (favourite) "Remove from favourites" else "Save to favourites",
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}
