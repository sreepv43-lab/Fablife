package io.github.sreepv43.fablife.content

import io.github.sreepv43.fablife.engine.Recommendations
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/** Parses the real generated assets so content and models can't drift apart. */
class ContentAssetsTest {
    private fun asset(name: String) = File("src/main/assets/$name").readText()

    private val journeyFiles get() = File("src/main/assets/journeys").listFiles { f -> f.extension == "json" }!!.sortedBy { it.name }
    private val journeys get() = journeyFiles.map { ContentParser.journey(it.readText()) }

    @Test
    fun energyJourneyParsesWithAllLetters() {
        val journey = ContentParser.journey(asset("journeys/energy.json"))
        assertEquals(8, journey.steps.size)
        assertEquals("energy", journey.goalKey)
        assertEquals(Ritual.MORNING, journey.ritualFocus)
        assertTrue(journey.steps.all { it.letter.body.length > 500 })
        assertTrue(journey.introLetter.body.startsWith("Dear {name}"))
        val habits = journey.steps.mapNotNull { (it.goal as? Goal.Habit)?.habit }
        assertEquals(listOf(1, 2, 3, 4, 5), habits.map { it.order })
        assertEquals(HabitTool(ToolType.ROUTINE, "WAKE_UP"), habits.single { it.id == "morning_movement" }.tool)
        assertTrue(journey.steps.last().goal is Goal.RitualChallenge)
    }

    @Test
    fun everyJourneyIsWellFormed() {
        val all = journeys
        assertTrue("at least the energy journey", all.isNotEmpty())
        assertEquals("journey ids are unique", all.size, all.map { it.id }.toSet().size)
        assertEquals("orders are unique", all.size, all.map { it.order }.toSet().size)
        val stepIds = all.flatMap { j -> j.steps.map { it.id } }
        assertEquals("step ids are unique across journeys", stepIds.size, stepIds.toSet().size)
        val habitIds = all.flatMap { j -> j.steps.mapNotNull { (it.goal as? Goal.Habit)?.habit?.id } }
        assertEquals("habit ids are unique across journeys", habitIds.size, habitIds.toSet().size)
        all.forEach { j ->
            assertTrue("${j.id} has 7-9 steps", j.steps.size in 7..9)
            assertTrue("${j.id} step ids use the journey prefix", j.steps.all { it.id.startsWith(j.id + ".") })
            assertTrue("${j.id} letters start with Dear {name}", (listOf(j.introLetter, j.outroLetter, j.missedDaysLetter, j.welcomeBackLetter) + j.steps.map { it.letter })
                .all { it.body.startsWith("Dear {name},") && it.body.trimEnd().trimEnd('*').endsWith("Your Fab Life coach") })
            j.steps.forEach { step ->
                val goal = step.goal
                if (goal is Goal.Habit) assertTrue("${step.id} has a smaller version", goal.habit.smallerVersion.isNotBlank())
            }
        }
    }

    @Test
    fun everyToolAndShortcutPointsAtRealContent() {
        val audio = ContentParser.audio(asset("audio.json"))
        val sessions = audio.sessions.map { it.id }.toSet()
        val breathing = audio.breathing.map { it.id }.toSet()
        val sounds = audio.sounds.map { it.id }.toSet()
        fun checkTool(where: String, tool: HabitTool) {
            val ref = tool.ref
            when (tool.type) {
                ToolType.GUIDED -> assertTrue("$where: unknown guided session $ref", ref in sessions)
                ToolType.BREATHING -> assertTrue("$where: unknown breathing pattern $ref", ref in breathing)
                ToolType.SOUND -> assertTrue("$where: unknown sound $ref", ref in sounds)
                ToolType.ROUTINE -> assertTrue("$where: unknown routine goal $ref", RoutineGoal.entries.any { it.name == ref })
                ToolType.JOURNAL -> assertTrue("$where: unknown journal kind $ref", ref in setOf("GRATITUDE", "REFLECTION", "MOOD"))
                ToolType.FOCUS_TIMER, ToolType.TOP3 -> assertEquals("$where: takes no ref", null, ref)
            }
        }
        journeys.forEach { j ->
            j.steps.forEach { step ->
                when (val goal = step.goal) {
                    is Goal.Habit -> goal.habit.tool?.let { checkTool(step.id, it) }
                    is Goal.Action -> goal.shortcuts.forEach { sc ->
                        val (kind, ref) = sc.target.substringBefore(':') to sc.target.substringAfter(':', "")
                        when (kind) {
                            "guided" -> assertTrue("${step.id}: ${sc.target}", ref in sessions)
                            "breathing" -> assertTrue("${step.id}: ${sc.target}", ref in breathing)
                            "sound" -> assertTrue("${step.id}: ${sc.target}", ref in sounds)
                            "journal" -> assertTrue("${step.id}: ${sc.target}", ref in setOf("GRATITUDE", "REFLECTION", "MOOD"))
                            else -> assertTrue("${step.id}: ${sc.target}", sc.target in setOf("habits", "settings", "explore_move", "explore_eat", "explore_mind", "top3", "focus_timer", "health", "journeys"))
                        }
                    }
                    is Goal.RitualChallenge -> {
                        val earlier = j.steps.takeWhile { it !== step }.mapNotNull { (it.goal as? Goal.Habit)?.habit }
                        assertTrue("${step.id}: ritual challenge needs 2+ habits in ${goal.ritual}", earlier.count { it.ritual == goal.ritual } >= 2)
                    }
                }
            }
        }
    }

    @Test
    fun audioLibraryHasTheCatalog() {
        val audio = ContentParser.audio(asset("audio.json"))
        assertEquals(setOf("box", "relax_478", "coherent", "calm_exhale"), audio.breathing.map { it.id }.toSet())
        assertEquals(setOf("white", "pink", "brown", "rain", "ocean", "fan", "focus_pad"), audio.sounds.map { it.id }.toSet())
        audio.sessions.forEach { s ->
            assertTrue("${s.id} has spoken lines", s.steps.count { !it.say.isNullOrBlank() } >= 8)
            assertTrue("${s.id} ambient is a known sound", s.ambient == null || s.ambient in audio.sounds.map { it.id })
        }
    }

    @Test
    fun everyRoutineMoveExistsAndEveryNeedHasAWakeUpRoutine() {
        val library = ContentParser.exercise(asset("exercise.json"))
        val moveIds = library.moves.map { it.id }.toSet()
        library.routines.flatMap { it.items }.forEach { item ->
            assertTrue("unknown move ${item.move}", item.move == null || item.move in moveIds)
            assertTrue(item.move != null || item.label != null)
        }
        val jumping = Recommendations.jumpingRoutineIds(library.routines, library.moves.filter { it.jumping }.map { it.id }.toSet())
        for (needs in MovementNeeds.entries) for (level in FitnessLevel.entries) {
            assertNotNull("$needs/$level", Recommendations.wakeUpRoutine(library.routines, level, needs, jumping))
        }
    }

    @Test
    fun everyMealHasARecipe() {
        val food = ContentParser.food(asset("food.json"))
        food.meals.forEach { meal ->
            assertTrue("${meal.title} needs ingredients", meal.ingredients.isNotEmpty())
            assertTrue("${meal.title} needs steps", meal.steps.isNotEmpty())
            assertTrue("${meal.title} needs a tip", !meal.tip.isNullOrBlank())
        }
    }

    @Test
    fun foodHasGentleMealsForEveryDietAndMeal() {
        val food = ContentParser.food(asset("food.json"))
        assertEquals(8, food.principles.size)
        for (diet in DietType.entries) for (meal in MealType.entries) {
            val gentle = Recommendations.meals(food.meals, diet, emptySet(), gentle = true)
            assertTrue("$diet/$meal", gentle.any { it.meal == meal })
        }
    }
}
