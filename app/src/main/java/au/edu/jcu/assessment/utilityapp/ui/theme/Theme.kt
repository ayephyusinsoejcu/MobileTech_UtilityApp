
package au.edu.jcu.assessment.utilityapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Dark theme variant
private val ColorDarkVariant = Color(0xFF393246)

// Light theme colors
private val LightColors = lightColorScheme(
    primary = StudyPurple,
    onPrimary = LightSurface,
    primaryContainer = StudyPurpleLight,
    onPrimaryContainer = LightText,
    background = LightBackground,
    onBackground = LightText,
    surface = LightSurface,
    onSurface = LightText,
    surfaceVariant = StudyPurpleLight,
    onSurfaceVariant = LightSecondaryText
)

// Dark theme colors
private val DarkColors = darkColorScheme(
    primary = StudyPurpleDark,
    onPrimary = DarkBackground,
    primaryContainer = StudyPurple,
    onPrimaryContainer = DarkText,
    background = DarkBackground,
    onBackground = DarkText,
    surface = DarkSurface,
    onSurface = DarkText,
    surfaceVariant = ColorDarkVariant,
    onSurfaceVariant = DarkSecondaryText
)

// Main StudyFlow Theme
@Composable
fun MobileTech_UtilityAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColors
    } else {
        LightColors
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
