package io.github.sreepv43.fablife.engine

import io.github.sreepv43.fablife.content.Allergen
import io.github.sreepv43.fablife.content.DietType
import io.github.sreepv43.fablife.content.Fibre
import io.github.sreepv43.fablife.content.FitnessLevel
import io.github.sreepv43.fablife.content.Meal
import io.github.sreepv43.fablife.content.MealType
import io.github.sreepv43.fablife.content.MovementNeeds
import io.github.sreepv43.fablife.content.Place
import io.github.sreepv43.fablife.content.Routine
import io.github.sreepv43.fablife.content.RoutineGoal
import io.github.sreepv43.fablife.content.RoutineItem
import org.junit.Assert.assertEquals
import org.junit.Test

class RecommendationsTest {
    private fun meal(id: String, diet: DietType, fibre: Fibre = Fibre.REGULAR, vararg allergens: Allergen) =
        Meal(id, id, MealType.BREAKFAST, diet, fibre, allergens.toList())

    private val meals = listOf(
        meal("oats", DietType.VEGAN),
        meal("curd_rice", DietType.VEGETARIAN, Fibre.GENTLE, Allergen.DAIRY),
        meal("omelette", DietType.EGGETARIAN, Fibre.REGULAR, Allergen.EGGS),
        meal("fish", DietType.NON_VEG, Fibre.GENTLE, Allergen.SEAFOOD),
        meal("idli", DietType.VEGAN, Fibre.GENTLE),
    )

    @Test
    fun dietFilterIsHierarchical() {
        assertEquals(listOf("oats", "idli"), Recommendations.meals(meals, DietType.VEGAN, emptySet(), false).map { it.id })
        assertEquals(listOf("oats", "curd_rice", "idli"), Recommendations.meals(meals, DietType.VEGETARIAN, emptySet(), false).map { it.id })
        assertEquals(5, Recommendations.meals(meals, DietType.NON_VEG, emptySet(), false).size)
    }

    @Test
    fun regularMealsComeFirstWhenGentleOff() {
        assertEquals(
            listOf("oats", "omelette", "curd_rice", "fish", "idli"),
            Recommendations.meals(meals, DietType.NON_VEG, emptySet(), false).map { it.id },
        )
    }

    @Test
    fun gentleModeShowsOnlyGentleMeals() {
        assertEquals(listOf("curd_rice", "fish", "idli"), Recommendations.meals(meals, DietType.NON_VEG, emptySet(), true).map { it.id })
    }

    @Test
    fun avoidedAllergensAreHidden() {
        assertEquals(listOf("idli"), Recommendations.meals(meals, DietType.NON_VEG, setOf(Allergen.DAIRY, Allergen.SEAFOOD), true).map { it.id })
    }

    private fun routine(id: String, needs: List<MovementNeeds>, levels: List<FitnessLevel>, vararg moves: String) =
        Routine(id, id, 5, levels, needs, Place.HOME, RoutineGoal.WAKE_UP, "", 10, moves.map { RoutineItem(move = it, seconds = 30) })

    private val routines = listOf(
        routine("R1", listOf(MovementNeeds.STANDARD), listOf(FitnessLevel.BEGINNER), "march"),
        routine("R2", listOf(MovementNeeds.STANDARD), listOf(FitnessLevel.INTERMEDIATE, FitnessLevel.ACTIVE), "jacks"),
        routine("R3", listOf(MovementNeeds.STANDARD, MovementNeeds.LOW_IMPACT), FitnessLevel.entries, "march"),
        routine("R4", listOf(MovementNeeds.SEATED), FitnessLevel.entries, "seated_march"),
        routine("BAD", listOf(MovementNeeds.LOW_IMPACT), FitnessLevel.entries, "jacks"),
    )
    private val jumping = Recommendations.jumpingRoutineIds(routines, setOf("jacks"))

    @Test
    fun routinesFilteredByNeeds() {
        assertEquals(listOf("R1", "R2", "R3"), Recommendations.routines(routines, MovementNeeds.STANDARD, jumping).map { it.id })
        assertEquals(listOf("R3"), Recommendations.routines(routines, MovementNeeds.LOW_IMPACT, jumping).map { it.id })
        assertEquals(listOf("R4"), Recommendations.routines(routines, MovementNeeds.SEATED, jumping).map { it.id })
    }

    @Test
    fun wakeUpRoutineMatchesLevelAndNeeds() {
        assertEquals("R1", Recommendations.wakeUpRoutine(routines, FitnessLevel.BEGINNER, MovementNeeds.STANDARD, jumping)?.id)
        assertEquals("R2", Recommendations.wakeUpRoutine(routines, FitnessLevel.ACTIVE, MovementNeeds.STANDARD, jumping)?.id)
        assertEquals("R3", Recommendations.wakeUpRoutine(routines, FitnessLevel.ACTIVE, MovementNeeds.LOW_IMPACT, jumping)?.id)
        assertEquals("R4", Recommendations.wakeUpRoutine(routines, FitnessLevel.BEGINNER, MovementNeeds.SEATED, jumping)?.id)
    }
}
