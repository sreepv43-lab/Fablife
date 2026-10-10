package io.github.sreepv43.fablife.data

import android.content.Context
import io.github.sreepv43.fablife.content.AudioLibrary
import io.github.sreepv43.fablife.content.ContentParser
import io.github.sreepv43.fablife.content.ExerciseLibrary
import io.github.sreepv43.fablife.content.FoodGuide
import io.github.sreepv43.fablife.content.Journey
import io.github.sreepv43.fablife.engine.Recommendations

/** Read-only content shipped in assets/, parsed once on first use. */
class ContentRepository(private val context: Context) {
    private fun read(name: String) = context.assets.open(name).bufferedReader().use { it.readText() }

    /** Every journey in assets/journeys, ordered by `order`. */
    val journeys: List<Journey> by lazy {
        (context.assets.list("journeys") ?: emptyArray())
            .filter { it.endsWith(".json") }
            .map { ContentParser.journey(read("journeys/$it")) }
            .sortedBy { it.order }
    }
    val exercise: ExerciseLibrary by lazy { ContentParser.exercise(read("exercise.json")) }
    val food: FoodGuide by lazy { ContentParser.food(read("food.json")) }
    val audio: AudioLibrary by lazy { ContentParser.audio(read("audio.json")) }

    val jumpingRoutineIds: Set<String> by lazy {
        Recommendations.jumpingRoutineIds(exercise.routines, exercise.moves.filter { it.jumping }.map { it.id }.toSet())
    }

    fun journey(id: String): Journey? = journeys.firstOrNull { it.id == id }

    /** The journey an onboarding goal starts; falls back to the first journey. */
    fun journeyForGoal(goal: String): Journey = journeys.firstOrNull { it.goalKey == goal } ?: journeys.first()

    fun journeyOfStep(stepId: String): Journey? = journeys.firstOrNull { j -> j.steps.any { it.id == stepId } }
}
