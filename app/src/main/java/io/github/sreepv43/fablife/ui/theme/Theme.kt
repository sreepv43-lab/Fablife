package io.github.sreepv43.fablife.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.github.sreepv43.fablife.data.ThemeMode

private val Light = lightColorScheme(
    primary = Color(0xFFA8501C),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDBC9),
    onPrimaryContainer = Color(0xFF3A1600),
    secondary = Color(0xFF1F6F65),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFC6EDE6),
    onSecondaryContainer = Color(0xFF00201C),
    tertiary = Color(0xFF7A5900),
    tertiaryContainer = Color(0xFFFFE08F),
    onTertiaryContainer = Color(0xFF261A00),
    background = Color(0xFFFFFBF7),
    surface = Color(0xFFFFFBF7),
    surfaceVariant = Color(0xFFF4E2D8),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFCF3EE),
    surfaceContainer = Color(0xFFF8EEE8),
    surfaceContainerHigh = Color(0xFFF2E7E1),
    surfaceContainerHighest = Color(0xFFECE0DA),
    outlineVariant = Color(0xFFD8C2B7),
)

private val Dark = darkColorScheme(
    primary = Color(0xFFFFB68D),
    onPrimary = Color(0xFF552100),
    primaryContainer = Color(0xFF7A3304),
    onPrimaryContainer = Color(0xFFFFDBC9),
    secondary = Color(0xFF8BD5CA),
    onSecondary = Color(0xFF003731),
    secondaryContainer = Color(0xFF005048),
    onSecondaryContainer = Color(0xFFA7F2E6),
    tertiary = Color(0xFFF0C048),
    tertiaryContainer = Color(0xFF5C4200),
    onTertiaryContainer = Color(0xFFFFDF95),
    background = Color(0xFF1A1512),
    surface = Color(0xFF1A1512),
    surfaceVariant = Color(0xFF52443C),
    surfaceContainerLowest = Color(0xFF140F0D),
    surfaceContainerLow = Color(0xFF221B17),
    surfaceContainer = Color(0xFF261F1B),
    surfaceContainerHigh = Color(0xFF312925),
    surfaceContainerHighest = Color(0xFF3C3430),
    outlineVariant = Color(0xFF52443C),
)

@Composable
fun FabTheme(mode: ThemeMode, content: @Composable () -> Unit) {
    val dark = when (mode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    MaterialTheme(colorScheme = if (dark) Dark else Light, content = content)
}
