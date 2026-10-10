package io.github.sreepv43.fablife.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Bedtime
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DirectionsWalk
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.EggAlt
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.material.icons.outlined.Hotel
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.NightsStay
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.sreepv43.fablife.content.Ritual

/** Habit icon ids are listed in docs/DESIGN_PHASE2.md section 1.3; unknown ids fall back to a check mark. */
fun habitIcon(name: String): ImageVector = when (name) {
    "water" -> Icons.Outlined.WaterDrop
    "breakfast" -> Icons.Outlined.EggAlt
    "movement" -> Icons.AutoMirrored.Outlined.DirectionsRun
    "light" -> Icons.Outlined.WbSunny
    "celebrate" -> Icons.Outlined.Celebration
    "moon" -> Icons.Outlined.NightsStay
    "bed" -> Icons.Outlined.Bedtime
    "book" -> Icons.Outlined.MenuBook
    "journal" -> Icons.Outlined.EditNote
    "breathe" -> Icons.Outlined.Spa
    "meditate" -> Icons.Outlined.SelfImprovement
    "focus" -> Icons.Outlined.GpsFixed
    "timer" -> Icons.Outlined.Timer
    "plan" -> Icons.Outlined.EditNote
    "phone_off" -> Icons.Outlined.PhoneAndroid
    "plate" -> Icons.Outlined.RestaurantMenu
    "apple" -> Icons.Outlined.Eco
    "walk" -> Icons.Outlined.DirectionsWalk
    "stretch" -> Icons.Outlined.FitnessCenter
    "heart" -> Icons.Outlined.FavoriteBorder
    "sound" -> Icons.Outlined.MusicNote
    else -> Icons.Outlined.CheckCircle
}

val Ritual.label: String get() = when (this) {
    Ritual.MORNING -> "Morning"
    Ritual.AFTERNOON -> "Afternoon"
    Ritual.EVENING -> "Evening"
}

val Ritual.emoji: String get() = when (this) {
    Ritual.MORNING -> "☀️"
    Ritual.AFTERNOON -> "🌤️"
    Ritual.EVENING -> "🌙"
}

fun <T : Enum<T>> T.pretty(): String = name.lowercase().replace('_', ' ').replaceFirstChar { it.uppercase() }
