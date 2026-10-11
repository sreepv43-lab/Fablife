package io.github.sreepv43.fablife.ui

import androidx.navigation.NavHostController
import io.github.sreepv43.fablife.content.HabitTool
import io.github.sreepv43.fablife.content.RoutineGoal
import io.github.sreepv43.fablife.content.ToolType
import io.github.sreepv43.fablife.data.HabitEntity
import io.github.sreepv43.fablife.engine.Recommendations

/** The tool a habit opens, or null for a plain habit. */
fun HabitEntity.tool(): HabitTool? {
    val type = toolType?.let { t -> ToolType.entries.firstOrNull { it.name == t } } ?: return null
    return HabitTool(type, toolRef)
}

/** Label of the button that opens a habit's tool (see docs/DESIGN_PHASE2.md section 1.4). */
val ToolType.buttonLabel: String
    get() = when (this) {
        ToolType.ROUTINE -> "Start"
        ToolType.BREATHING -> "Breathe"
        ToolType.GUIDED -> "Listen"
        ToolType.SOUND -> "Play"
        ToolType.FOCUS_TIMER -> "Focus"
        ToolType.JOURNAL -> "Write"
        ToolType.TOP3 -> "Plan"
    }

/**
 * The route that opens a habit's tool, or null if there is nothing to open (for example a routine
 * that doesn't exist for the user's needs). Tool screens get the habit id so they can offer to check it in.
 */
fun toolRoute(habit: HabitEntity, vm: AppViewModel, state: AppState): String? {
    val tool = habit.tool() ?: return null
    val ref = tool.ref
    return when (tool.type) {
        ToolType.ROUTINE -> {
            val goal = RoutineGoal.entries.firstOrNull { it.name == ref } ?: RoutineGoal.WAKE_UP
            val c = vm.content
            Recommendations.routineForGoal(c.exercise.routines, goal, state.prefs.fitnessLevel, state.prefs.movementNeeds, c.jumpingRoutineIds)
                ?.let { Routes.routine(it.id, habit.id) }
        }
        ToolType.BREATHING -> ref?.let { Routes.breathing(it, habit.id) }
        ToolType.GUIDED -> ref?.let { Routes.guided(it, habit.id) }
        ToolType.SOUND -> ref?.let { Routes.sound(it, habit.id) }
        ToolType.FOCUS_TIMER -> Routes.focus(habit.id)
        ToolType.JOURNAL -> Routes.journal(ref ?: "GRATITUDE", habit.id)
        ToolType.TOP3 -> Routes.top3(habit.id)
    }
}

/**
 * Opens an action-step shortcut target (docs/DESIGN_PHASE2.md section 1.9). Unknown targets are ignored so
 * content can never crash the app.
 */
fun NavHostController.openShortcut(target: String) {
    val route = when {
        target == "habits" -> Routes.HABITS
        target == "settings" -> "settings"
        target == "explore_move" || target == "explore_eat" || target == "explore_mind" -> "explore"
        target == "top3" -> Routes.top3()
        target == "focus_timer" -> Routes.focus()
        target == "health" -> Routes.HEALTH
        target == "journeys" -> Routes.JOURNEYS
        target.startsWith("journal:") -> Routes.journal(target.removePrefix("journal:"))
        target.startsWith("sound:") -> Routes.sound(target.removePrefix("sound:"))
        target.startsWith("guided:") -> Routes.guided(target.removePrefix("guided:"))
        target.startsWith("breathing:") -> Routes.breathing(target.removePrefix("breathing:"))
        else -> null
    } ?: return
    navigate(route)
}
