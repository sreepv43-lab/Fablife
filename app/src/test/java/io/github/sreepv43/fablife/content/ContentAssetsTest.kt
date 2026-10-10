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

    @Test
    fun journeyParsesWithAllLetters() {
        val journey = ContentParser.journey(asset("journey_energy.json"))
        assertEquals(8, journey.steps.size)
        assertTrue(journey.steps.all { it.letter.body.length > 500 })
        assertTrue(journey.introLetter.body.startsWith("Dear {name}"))
        val habits = journey.steps.mapNotNull { (it.goal as? Goal.Habit)?.habit }
        assertEquals(listOf(1, 2, 3, 4, 5), habits.map { it.order })
        assertTrue(journey.steps.last().goal is Goal.RitualChallenge)
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
    fun foodHasGentleMealsForEveryDietAndMeal() {
        val food = ContentParser.food(asset("food.json"))
        assertEquals(8, food.principles.size)
        for (diet in DietType.entries) for (meal in MealType.entries) {
            val gentle = Recommendations.meals(food.meals, diet, emptySet(), gentle = true)
            assertTrue("$diet/$meal", gentle.any { it.meal == meal })
        }
    }
}
