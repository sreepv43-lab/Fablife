package io.github.sreepv43.fablife.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.github.sreepv43.fablife.content.Ritual
import io.github.sreepv43.fablife.data.StepState
import io.github.sreepv43.fablife.ui.explore.ExploreScreen
import io.github.sreepv43.fablife.ui.explore.RoutinePlayerScreen
import io.github.sreepv43.fablife.ui.journey.JourneyScreen
import io.github.sreepv43.fablife.ui.journey.LetterScreen
import io.github.sreepv43.fablife.ui.onboarding.OnboardingScreen
import io.github.sreepv43.fablife.ui.progress.ProgressScreen
import io.github.sreepv43.fablife.ui.ritual.RitualPlayerScreen
import io.github.sreepv43.fablife.ui.settings.HabitsScreen
import io.github.sreepv43.fablife.ui.settings.SettingsScreen
import io.github.sreepv43.fablife.ui.today.TodayScreen

private enum class Tab(val route: String, val label: String, val icon: ImageVector) {
    TODAY("today", "Today", Icons.Outlined.Home),
    JOURNEY("journey", "Journey", Icons.Outlined.Map),
    EXPLORE("explore", "Explore", Icons.Outlined.Explore),
    PROGRESS("progress", "Progress", Icons.Outlined.BarChart),
    SETTINGS("settings", "Settings", Icons.Outlined.Settings),
}

object Routes {
    fun letter(id: String) = "letter/$id"
    fun ritual(ritual: Ritual) = "ritual/${ritual.name}"
    fun routine(id: String) = "routine/$id"
    const val HABITS = "habits"
}

@Composable
fun FabLifeRoot(vm: AppViewModel, openRitual: Ritual?, onRitualOpened: () -> Unit) {
    val prefs by vm.prefs.collectAsStateWithLifecycle()
    val state by vm.state.collectAsStateWithLifecycle()
    val p = prefs
    when {
        p == null || (p.onboardingCompleted && state == null) -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
        !p.onboardingCompleted -> OnboardingScreen(vm)
        else -> MainApp(vm, state!!, openRitual, onRitualOpened)
    }
}

@Composable
private fun MainApp(vm: AppViewModel, state: AppState, openRitual: Ritual?, onRitualOpened: () -> Unit) {
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val route = backStack?.destination?.route
    var celebration by remember { mutableStateOf<UiEvent?>(null) }

    LaunchedEffect(Unit) { vm.events.collect { celebration = it } }
    // First launch after onboarding: show the intro letter, then step 1.
    val needsIntro = state.steps.firstOrNull()?.progress?.state == StepState.UNLOCKED && state.habits.isEmpty()
    LaunchedEffect(needsIntro) { if (needsIntro) nav.navigate(Routes.letter("intro")) }
    LaunchedEffect(openRitual) {
        if (openRitual != null) {
            nav.navigate(Routes.ritual(openRitual))
            onRitualOpened()
        }
    }

    Scaffold(
        bottomBar = {
            if (Tab.entries.any { it.route == route }) {
                NavigationBar {
                    Tab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = route == tab.route,
                            onClick = {
                                nav.navigate(tab.route) {
                                    popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                val unread = tab == Tab.JOURNEY && state.currentStep?.progress?.state == StepState.UNLOCKED
                                BadgedBox(badge = { if (unread) Badge() }) { Icon(tab.icon, contentDescription = null) }
                            },
                            label = { Text(tab.label) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(nav, startDestination = Tab.TODAY.route, modifier = Modifier.padding(padding)) {
            composable(Tab.TODAY.route) { TodayScreen(vm, state, nav) }
            composable(Tab.JOURNEY.route) { JourneyScreen(vm, state, nav) }
            composable(Tab.EXPLORE.route) { ExploreScreen(vm, state, nav) }
            composable(Tab.PROGRESS.route) { ProgressScreen(vm, state) }
            composable(Tab.SETTINGS.route) { SettingsScreen(vm, state, nav) }
            composable("letter/{id}") { entry ->
                LetterScreen(vm, state, entry.arguments?.getString("id").orEmpty(), nav)
            }
            composable("ritual/{ritual}") { entry ->
                RitualPlayerScreen(vm, state, Ritual.valueOf(entry.arguments?.getString("ritual")!!), nav)
            }
            composable("routine/{id}") { entry ->
                RoutinePlayerScreen(vm, state, entry.arguments?.getString("id").orEmpty(), nav)
            }
            composable(Routes.HABITS) { HabitsScreen(vm, state, nav) }
        }
    }

    celebration?.let { event ->
        CelebrationDialog(event, onDismiss = { celebration = null }, onReadOutro = {
            celebration = null
            nav.navigate(Routes.letter("outro"))
        }, onReadNext = { stepId ->
            celebration = null
            nav.navigate(Routes.letter(stepId))
        }, nextStepId = (event as? UiEvent.StepCompleted)?.let { e ->
            vm.content.journey.steps.getOrNull(vm.content.journey.steps.indexOfFirst { it.id == e.step.id } + 1)?.id
        })
    }
}

fun NavHostController.goToToday() {
    navigate("today") { popUpTo(graph.findStartDestination().id) { inclusive = false }; launchSingleTop = true }
}
