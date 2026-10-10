package io.github.sreepv43.fablife.content

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class Ritual { MORNING, AFTERNOON, EVENING }

enum class DietType { VEGETARIAN, VEGAN, EGGETARIAN, NON_VEG }

enum class Allergen { DAIRY, GLUTEN, NUTS, EGGS, SOY, SEAFOOD }

enum class Fibre { REGULAR, GENTLE }

enum class MealType { BREAKFAST, LUNCH, DINNER, SNACK }

enum class FitnessLevel { BEGINNER, INTERMEDIATE, ACTIVE }

enum class MovementNeeds { STANDARD, LOW_IMPACT, SEATED }

enum class RoutineGoal { WAKE_UP, STRENGTH, CARDIO, FLEXIBILITY, WIND_DOWN }

enum class Place { HOME, OUTDOORS }

@Serializable
data class Letter(val title: String, val body: String)

@Serializable
data class HabitTemplate(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val ritual: Ritual,
    val order: Int,
    val durationSeconds: Int? = null,
    val smallerVersion: String,
)

@Serializable
sealed interface Goal {
    @Serializable
    @SerialName("habit")
    data class Habit(val habit: HabitTemplate, val targetDays: Int, val windowDays: Int) : Goal

    @Serializable
    @SerialName("action")
    data class Action(val title: String, val description: String) : Goal

    @Serializable
    @SerialName("ritual")
    data class RitualChallenge(val ritual: Ritual, val targetDays: Int, val windowDays: Int) : Goal
}

@Serializable
data class Step(
    val id: String,
    val title: String,
    val letter: Letter,
    val goal: Goal,
    val buttonLabel: String,
    val tips: List<String> = emptyList(),
    val completionTitle: String,
    val completionMessage: String,
    val gentleNote: String? = null,
)

@Serializable
data class Journey(
    val id: String,
    val title: String,
    val subtitle: String,
    val introLetter: Letter,
    val outroLetter: Letter,
    val missedDaysLetter: Letter,
    val welcomeBackLetter: Letter,
    val steps: List<Step>,
)

@Serializable
data class Move(
    val id: String,
    val name: String,
    val howTo: String,
    val easier: String,
    val harder: String,
    val seated: Boolean = false,
    val jumping: Boolean = false,
)

@Serializable
data class RoutineItem(val move: String? = null, val label: String? = null, val seconds: Int? = null, val reps: Int? = null)

@Serializable
data class Routine(
    val id: String,
    val title: String,
    val minutes: Int,
    val levels: List<FitnessLevel>,
    val needs: List<MovementNeeds>,
    val place: Place,
    val goal: RoutineGoal,
    val description: String,
    val restSeconds: Int = 10,
    val items: List<RoutineItem>,
)

@Serializable
data class ExerciseLibrary(val moves: List<Move>, val routines: List<Routine>)

@Serializable
data class Meal(
    val id: String,
    val title: String,
    val meal: MealType,
    val diet: DietType,
    val fibre: Fibre,
    val allergens: List<Allergen> = emptyList(),
    val prepMinutes: Int? = null,
    val ingredients: List<String> = emptyList(),
    val steps: List<String> = emptyList(),
    val tip: String? = null,
)

@Serializable
data class FoodGuide(val principles: List<Letter>, val gentleGuidance: Letter, val meals: List<Meal>)
