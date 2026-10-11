package io.github.sreepv43.fablife.data

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import io.github.sreepv43.fablife.content.Allergen
import io.github.sreepv43.fablife.content.DietType
import io.github.sreepv43.fablife.content.FitnessLevel
import io.github.sreepv43.fablife.content.MovementNeeds
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalTime

enum class ThemeMode { SYSTEM, LIGHT, DARK }

data class UserPrefs(
    val onboardingCompleted: Boolean = false,
    val name: String = "",
    val goal: String = "energy",
    val activeJourneyId: String = "energy",
    val obstacles: Set<String> = emptySet(),
    val wakeTime: LocalTime = LocalTime.of(7, 0),
    val bedTime: LocalTime = LocalTime.of(23, 0),
    val dietType: DietType = DietType.VEGETARIAN,
    val gentleStomach: Boolean = false,
    val gentleSince: LocalDate? = null,
    val gentleTipDismissed: Boolean = false,
    val gentleGuidanceSeen: Boolean = false,
    val avoidFoods: Set<Allergen> = emptySet(),
    val fitnessLevel: FitnessLevel = FitnessLevel.BEGINNER,
    val movementNeeds: MovementNeeds = MovementNeeds.STANDARD,
    val exerciseSafetySeen: Boolean = false,
    val favouriteMeals: Set<String> = emptySet(),
    val dayStartOffsetHours: Int = 3,
    val theme: ThemeMode = ThemeMode.SYSTEM,
    val nudgeDismissedDay: LocalDate? = null,
    val welcomeBackPending: Boolean = false,
    val outroSeen: Boolean = false,
    val focusMinutes: Int = 25,
    val breakMinutes: Int = 5,
    val longBreakMinutes: Int = 15,
) {
    val displayName: String get() = name.ifBlank { "friend" }
}

private val Context.dataStore by preferencesDataStore("prefs")

class PrefsRepository(private val context: Context) {
    private object K {
        val onboarding = booleanPreferencesKey("onboarding_completed")
        val name = stringPreferencesKey("name")
        val goal = stringPreferencesKey("goal")
        val activeJourney = stringPreferencesKey("active_journey")
        val obstacles = stringSetPreferencesKey("obstacles")
        val wake = intPreferencesKey("wake_time")
        val bed = intPreferencesKey("bed_time")
        val diet = stringPreferencesKey("diet")
        val gentle = booleanPreferencesKey("gentle")
        val gentleSince = longPreferencesKey("gentle_since")
        val gentleTipDismissed = booleanPreferencesKey("gentle_tip_dismissed")
        val gentleGuidanceSeen = booleanPreferencesKey("gentle_guidance_seen")
        val avoid = stringSetPreferencesKey("avoid")
        val level = stringPreferencesKey("fitness_level")
        val needs = stringPreferencesKey("movement_needs")
        val safetySeen = booleanPreferencesKey("exercise_safety_seen")
        val favourites = stringSetPreferencesKey("favourite_meals")
        val dayOffset = intPreferencesKey("day_offset")
        val theme = stringPreferencesKey("theme")
        val nudgeDismissed = longPreferencesKey("nudge_dismissed")
        val welcomeBack = booleanPreferencesKey("welcome_back_pending")
        val outroSeen = booleanPreferencesKey("outro_seen")
        val focusMinutes = intPreferencesKey("focus_minutes")
        val breakMinutes = intPreferencesKey("break_minutes")
        val longBreakMinutes = intPreferencesKey("long_break_minutes")
    }

    val prefs: Flow<UserPrefs> = context.dataStore.data.map { it.toUserPrefs() }

    suspend fun current(): UserPrefs = prefs.first()

    suspend fun update(transform: (UserPrefs) -> UserPrefs) {
        context.dataStore.edit { p -> p.write(transform(p.toUserPrefs())) }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }

    private inline fun <reified T : Enum<T>> String?.toEnum(default: T): T =
        this?.let { v -> enumValues<T>().firstOrNull { it.name == v } } ?: default

    private fun Preferences.toUserPrefs(): UserPrefs {
        val d = UserPrefs()
        return UserPrefs(
            onboardingCompleted = this[K.onboarding] ?: false,
            name = this[K.name] ?: "",
            goal = this[K.goal] ?: d.goal,
            activeJourneyId = this[K.activeJourney] ?: d.activeJourneyId,
            obstacles = this[K.obstacles] ?: emptySet(),
            wakeTime = this[K.wake]?.let { LocalTime.ofSecondOfDay(it.toLong()) } ?: d.wakeTime,
            bedTime = this[K.bed]?.let { LocalTime.ofSecondOfDay(it.toLong()) } ?: d.bedTime,
            dietType = this[K.diet].toEnum(d.dietType),
            gentleStomach = this[K.gentle] ?: false,
            gentleSince = this[K.gentleSince]?.let(LocalDate::ofEpochDay),
            gentleTipDismissed = this[K.gentleTipDismissed] ?: false,
            gentleGuidanceSeen = this[K.gentleGuidanceSeen] ?: false,
            avoidFoods = (this[K.avoid] ?: emptySet()).mapNotNull { v -> Allergen.entries.firstOrNull { it.name == v } }.toSet(),
            fitnessLevel = this[K.level].toEnum(d.fitnessLevel),
            movementNeeds = this[K.needs].toEnum(d.movementNeeds),
            exerciseSafetySeen = this[K.safetySeen] ?: false,
            favouriteMeals = this[K.favourites] ?: emptySet(),
            dayStartOffsetHours = this[K.dayOffset] ?: d.dayStartOffsetHours,
            theme = this[K.theme].toEnum(d.theme),
            nudgeDismissedDay = this[K.nudgeDismissed]?.let(LocalDate::ofEpochDay),
            welcomeBackPending = this[K.welcomeBack] ?: false,
            outroSeen = this[K.outroSeen] ?: false,
            focusMinutes = this[K.focusMinutes] ?: d.focusMinutes,
            breakMinutes = this[K.breakMinutes] ?: d.breakMinutes,
            longBreakMinutes = this[K.longBreakMinutes] ?: d.longBreakMinutes,
        )
    }

    private fun MutablePreferences.write(u: UserPrefs) {
        this[K.onboarding] = u.onboardingCompleted
        this[K.name] = u.name
        this[K.goal] = u.goal
        this[K.activeJourney] = u.activeJourneyId
        this[K.obstacles] = u.obstacles
        this[K.wake] = u.wakeTime.toSecondOfDay()
        this[K.bed] = u.bedTime.toSecondOfDay()
        this[K.diet] = u.dietType.name
        this[K.gentle] = u.gentleStomach
        if (u.gentleSince != null) this[K.gentleSince] = u.gentleSince.toEpochDay() else remove(K.gentleSince)
        this[K.gentleTipDismissed] = u.gentleTipDismissed
        this[K.gentleGuidanceSeen] = u.gentleGuidanceSeen
        this[K.avoid] = u.avoidFoods.map { it.name }.toSet()
        this[K.level] = u.fitnessLevel.name
        this[K.needs] = u.movementNeeds.name
        this[K.safetySeen] = u.exerciseSafetySeen
        this[K.favourites] = u.favouriteMeals
        this[K.dayOffset] = u.dayStartOffsetHours
        this[K.theme] = u.theme.name
        if (u.nudgeDismissedDay != null) this[K.nudgeDismissed] = u.nudgeDismissedDay.toEpochDay() else remove(K.nudgeDismissed)
        this[K.welcomeBack] = u.welcomeBackPending
        this[K.outroSeen] = u.outroSeen
        this[K.focusMinutes] = u.focusMinutes
        this[K.breakMinutes] = u.breakMinutes
        this[K.longBreakMinutes] = u.longBreakMinutes
    }
}
