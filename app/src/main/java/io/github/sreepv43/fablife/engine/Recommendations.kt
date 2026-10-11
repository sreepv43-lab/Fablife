package io.github.sreepv43.fablife.engine

import io.github.sreepv43.fablife.content.Allergen
import io.github.sreepv43.fablife.content.DietType
import io.github.sreepv43.fablife.content.Fibre
import io.github.sreepv43.fablife.content.FitnessLevel
import io.github.sreepv43.fablife.content.Meal
import io.github.sreepv43.fablife.content.MovementNeeds
import io.github.sreepv43.fablife.content.Routine
import io.github.sreepv43.fablife.content.RoutineGoal

object Recommendations {

    /** Diet types whose meals a user of [diet] can eat (vegan meals suit everyone). */
    private fun allowedDiets(diet: DietType): Set<DietType> = when (diet) {
        DietType.VEGAN -> setOf(DietType.VEGAN)
        DietType.VEGETARIAN -> setOf(DietType.VEGAN, DietType.VEGETARIAN)
        DietType.EGGETARIAN -> setOf(DietType.VEGAN, DietType.VEGETARIAN, DietType.EGGETARIAN)
        DietType.NON_VEG -> DietType.entries.toSet()
    }

    /**
     * Meals the user can eat. Gentle mode shows only gentle (low-fibre) meals; otherwise regular
     * meals come first, then gentle ones.
     */
    fun meals(all: List<Meal>, diet: DietType, avoid: Set<Allergen>, gentle: Boolean): List<Meal> {
        val diets = allowedDiets(diet)
        val suitable = all.filter { it.diet in diets && it.allergens.none { a -> a in avoid } }
        return if (gentle) suitable.filter { it.fibre == Fibre.GENTLE }
        else suitable.sortedBy { if (it.fibre == Fibre.REGULAR) 0 else 1 }
    }

    /**
     * Routines tagged for the user's movement needs. As a safety net, low-impact and seated users never
     * see routines that contain jumping moves, even if mis-tagged.
     */
    fun routines(all: List<Routine>, needs: MovementNeeds, jumpingRoutineIds: Set<String>): List<Routine> = all.filter {
        needs in it.needs && (needs == MovementNeeds.STANDARD || it.id !in jumpingRoutineIds)
    }

    /** The routine a movement habit's Start button opens: one with the habit's goal matching level and needs. */
    fun routineForGoal(all: List<Routine>, goal: RoutineGoal, level: FitnessLevel, needs: MovementNeeds, jumpingRoutineIds: Set<String>): Routine? {
        val candidates = routines(all, needs, jumpingRoutineIds).filter { it.goal == goal }
        return candidates.firstOrNull { level in it.levels && needs in it.needs }
            ?: candidates.firstOrNull { needs in it.needs }
            ?: candidates.firstOrNull()
    }

    fun wakeUpRoutine(all: List<Routine>, level: FitnessLevel, needs: MovementNeeds, jumpingRoutineIds: Set<String>): Routine? =
        routineForGoal(all, RoutineGoal.WAKE_UP, level, needs, jumpingRoutineIds)

    /** Ids of routines that include any move flagged as jumping. */
    fun jumpingRoutineIds(routines: List<Routine>, jumpingMoveIds: Set<String>): Set<String> =
        routines.filter { r -> r.items.any { it.move in jumpingMoveIds } }.map { it.id }.toSet()
}
