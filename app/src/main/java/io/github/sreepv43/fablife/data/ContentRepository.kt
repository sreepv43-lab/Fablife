package io.github.sreepv43.fablife.data

import android.content.Context
import io.github.sreepv43.fablife.content.ContentParser
import io.github.sreepv43.fablife.content.ExerciseLibrary
import io.github.sreepv43.fablife.content.FoodGuide
import io.github.sreepv43.fablife.content.Journey
import io.github.sreepv43.fablife.engine.Recommendations

/** Read-only content shipped in assets/, parsed once on first use. */
class ContentRepository(private val context: Context) {
    private fun read(name: String) = context.assets.open(name).bufferedReader().use { it.readText() }

    val journey: Journey by lazy { ContentParser.journey(read("journey_energy.json")) }
    val exercise: ExerciseLibrary by lazy { ContentParser.exercise(read("exercise.json")) }
    val food: FoodGuide by lazy { ContentParser.food(read("food.json")) }

    val jumpingRoutineIds: Set<String> by lazy {
        Recommendations.jumpingRoutineIds(exercise.routines, exercise.moves.filter { it.jumping }.map { it.id }.toSet())
    }
}
