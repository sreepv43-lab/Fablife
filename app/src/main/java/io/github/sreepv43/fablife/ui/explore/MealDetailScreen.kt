package io.github.sreepv43.fablife.ui.explore

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
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import io.github.sreepv43.fablife.content.Fibre
import io.github.sreepv43.fablife.ui.AppState
import io.github.sreepv43.fablife.ui.AppViewModel
import io.github.sreepv43.fablife.ui.SectionTitle
import io.github.sreepv43.fablife.ui.onboarding.allergenLabel
import io.github.sreepv43.fablife.ui.onboarding.dietLabel
import io.github.sreepv43.fablife.ui.pretty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailScreen(vm: AppViewModel, state: AppState, mealId: String, nav: NavHostController) {
    val meal = vm.content.food.meals.firstOrNull { it.id == mealId }
    val favourite = mealId in state.prefs.favouriteMeals
    Column(Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Meal idea") },
            navigationIcon = { IconButton(onClick = { nav.popBackStack() }) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back") } },
            actions = {
                if (meal != null) IconButton(onClick = {
                    vm.updatePrefs { it.copy(favouriteMeals = if (mealId in it.favouriteMeals) it.favouriteMeals - mealId else it.favouriteMeals + mealId) }
                }) {
                    Icon(
                        if (favourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (favourite) "Remove from favourites" else "Save to favourites",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            },
        )
        if (meal == null) {
            Text("This meal idea isn't available any more.", modifier = Modifier.padding(24.dp))
            return
        }
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
            Column(Modifier.widthIn(max = 640.dp).padding(horizontal = 24.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(meal.title, style = MaterialTheme.typography.headlineMedium)
                val tags = buildList {
                    add(meal.meal.pretty())
                    add(dietLabel(meal.diet).substringBefore(" ("))
                    meal.prepMinutes?.let { add("$it min") }
                    if (meal.fibre == Fibre.GENTLE) add("🌿 Gentle on the stomach")
                }
                Text(tags.joinToString(" · "), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                if (meal.allergens.isNotEmpty()) {
                    Text("Contains: " + meal.allergens.joinToString { allergenLabel(it).lowercase() }, style = MaterialTheme.typography.bodyMedium)
                }
                if (meal.ingredients.isNotEmpty()) {
                    SectionTitle("Ingredients (for one)")
                    meal.ingredients.forEach { Row { Text("•  ", style = MaterialTheme.typography.bodyLarge); Text(it, style = MaterialTheme.typography.bodyLarge) } }
                }
                if (meal.steps.isNotEmpty()) {
                    SectionTitle("How to make it")
                    meal.steps.forEachIndexed { i, step ->
                        Row { Text("${i + 1}.  ", style = MaterialTheme.typography.bodyLarge); Text(step, style = MaterialTheme.typography.bodyLarge) }
                    }
                }
                meal.tip?.let {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer), modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        Text("💡 $it", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
                    }
                }
                if (meal.fibre == Fibre.GENTLE) {
                    Text(
                        "Gentle meals are best for short periods or when advised by your doctor. Add fibre back slowly when you feel better.",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    "General healthy-eating idea, not medical advice. Check ingredient labels if you have allergies.",
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 24.dp),
                )
            }
        }
    }
}
