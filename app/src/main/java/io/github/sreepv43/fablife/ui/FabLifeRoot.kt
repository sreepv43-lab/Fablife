package io.github.sreepv43.fablife.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import io.github.sreepv43.fablife.content.Ritual
import io.github.sreepv43.fablife.data.StepState
import io.github.sreepv43.fablife.data.JournalKind
import io.github.sreepv43.fablife.ui.explore.ExploreScreen
import io.github.sreepv43.fablife.ui.health.HealthScreen
import io.github.sreepv43.fablife.ui.journey.JourneysScreen
import io.github.sreepv43.fablife.ui.mind.BreathingScreen
import io.github.sreepv43.fablife.ui.mind.FocusTimerScreen
import io.github.sreepv43.fablife.ui.mind.GuidedSessionScreen
import io.github.sreepv43.fablife.ui.mind.JournalScreen
import io.github.sreepv43.fablife.ui.mind.SoundScreen
import io.github.sreepv43.fablife.ui.mind.Top3Screen
import io.github.sreepv43.fablife.ui.explore.MealDetailScreen
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
    fun meal(id: String) = "meal/$id"
    const val HABITS = "habits"
    const val HEALTH = "health"
    const val JOURNEYS = "journeys"
    const val SOUNDS = "sounds"

    // Tool routes. Each takes an optional habit id so the tool can offer to check that habit in when finished.
    private fun habitQuery(habit: String?) = if (habit == null) "" else "?habit=$habit"
    fun routine(id: String, habit: String? = null) = "routine/$id" + habitQuery(habit)
    fun breathing(id: String, habit: String? = null) = "breathing/$id" + habitQuery(habit)
    fun guided(id: String, habit: String? = null) = "guided/$id" + habitQuery(habit)
    fun sound(id: String, habit: String? = null) = "sound/$id" + habitQuery(habit)
    fun focus(habit: String? = null) = "focus" + habitQuery(habit)
    fun top3(habit: String? = null) = "top3" + habitQuery(habit)
    fun journal(kind: String = "GRATITUDE", habit: String? = null) = "journal?kind=$kind" + (if (habit == null) "" else "&habit=$habit")
}

private val habitArg = navArgument("habit") { type = NavType.StringType; nullable = true; defaultValue = null }
private val kindArg = navArgument("kind") { type = NavType.StringType; defaultValue = "GRATITUDE" }

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
    val playback by vm.audio.state.collectAsStateWithLifecycle()

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
            if (Tab.entries.any { it.route == route }) Column {
                MiniPlayer(playback, vm.audio) { nav.navigate(it) }
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
            composable("routine/{id}?habit={habit}", arguments = listOf(habitArg)) { entry ->
                RoutinePlayerScreen(vm, state, entry.arguments?.getString("id").orEmpty(), entry.arguments?.getString("habit"), nav)
            }
            composable("breathing/{id}?habit={habit}", arguments = listOf(habitArg)) { entry ->
                BreathingScreen(vm, state, entry.arguments?.getString("id").orEmpty(), entry.arguments?.getString("habit"), nav)
            }
            composable("guided/{id}?habit={habit}", arguments = listOf(habitArg)) { entry ->
                GuidedSessionScreen(vm, state, entry.arguments?.getString("id").orEmpty(), entry.arguments?.getString("habit"), nav)
            }
            composable("sound/{id}?habit={habit}", arguments = listOf(habitArg)) { entry ->
                SoundScreen(vm, state, entry.arguments?.getString("id"), entry.arguments?.getString("habit"), nav)
            }
            composable(Routes.SOUNDS) { SoundScreen(vm, state, null, null, nav) }
            composable("focus?habit={habit}", arguments = listOf(habitArg)) { entry ->
                FocusTimerScreen(vm, state, entry.arguments?.getString("habit"), nav)
            }
            composable("top3?habit={habit}", arguments = listOf(habitArg)) { entry ->
                Top3Screen(vm, state, entry.arguments?.getString("habit"), nav)
            }
            composable("journal?kind={kind}&habit={habit}", arguments = listOf(kindArg, habitArg)) { entry ->
                val kind = JournalKind.entries.firstOrNull { it.name == entry.arguments?.getString("kind") } ?: JournalKind.GRATITUDE
                JournalScreen(vm, state, kind, entry.arguments?.getString("habit"), nav)
            }
            composable(Routes.HEALTH) { HealthScreen(vm, state, nav) }
            composable(Routes.JOURNEYS) { JourneysScreen(vm, state, nav) }
            composable("meal/{id}") { entry ->
                MealDetailScreen(vm, state, entry.arguments?.getString("id").orEmpty(), nav)
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
            vm.content.journeyOfStep(e.step.id)?.steps?.let { all -> all.getOrNull(all.indexOfFirst { it.id == e.step.id } + 1)?.id }
        })
    }
}

fun NavHostController.goToToday() {
    navigate("today") { popUpTo(graph.findStartDestination().id) { inclusive = false }; launchSingleTop = true }
}
