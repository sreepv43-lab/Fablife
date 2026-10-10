package io.github.sreepv43.fablife.content

import kotlinx.serialization.json.Json

object ContentParser {
    private val json = Json { ignoreUnknownKeys = true }

    fun journey(text: String): Journey = json.decodeFromString(text)
    fun exercise(text: String): ExerciseLibrary = json.decodeFromString(text)
    fun food(text: String): FoodGuide = json.decodeFromString(text)
}
