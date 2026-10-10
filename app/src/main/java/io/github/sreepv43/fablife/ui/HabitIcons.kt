package io.github.sreepv43.fablife.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.automirrored.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.EggAlt
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.sreepv43.fablife.content.Ritual

fun habitIcon(name: String): ImageVector = when (name) {
    "water" -> Icons.Outlined.WaterDrop
    "breakfast" -> Icons.Outlined.EggAlt
    "movement" -> Icons.AutoMirrored.Outlined.DirectionsRun
    "light" -> Icons.Outlined.WbSunny
    "celebrate" -> Icons.Outlined.Celebration
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
