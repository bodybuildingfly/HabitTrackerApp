package com.example.habittrackerapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DarkRed,
    secondary = Orange,
    tertiary = Color.DarkGray,
    background = DarkGray,
    surface = DarkGray,
    onPrimary = LightGray,
    onBackground = LightGray,
    onSurface = LightGray,
    onPrimaryContainer = LightGray,
    onSecondaryContainer = LightGray,
    onTertiaryContainer = LightGray

//    primary = md_theme_dark_primary,
//    onPrimary = md_theme_dark_onPrimary,
//    primaryContainer = md_theme_dark_primaryContainer,
//    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
//    secondary = md_theme_dark_secondary,
//    onSecondary = md_theme_dark_onSecondary,
//    secondaryContainer = md_theme_dark_secondaryContainer,
//    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
//    tertiary = md_theme_dark_tertiary,
//    onTertiary = md_theme_dark_onTertiary,
//    tertiaryContainer = md_theme_dark_tertiaryContainer,
//    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
//    error = md_theme_dark_error,
//    errorContainer = md_theme_dark_errorContainer,
//    onError = md_theme_dark_onError,
//    onErrorContainer = md_theme_dark_onErrorContainer,
//    background = md_theme_dark_background,
//    onBackground = md_theme_dark_onBackground,
//    surface = md_theme_dark_surface,
//    onSurface = md_theme_dark_onSurface,
//    surfaceVariant = md_theme_dark_surfaceVariant,
//    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
//    outline = md_theme_dark_outline,
//    inverseOnSurface = md_theme_dark_inverseOnSurface,
//    inverseSurface = md_theme_dark_inverseSurface,
//    inversePrimary = md_theme_dark_inversePrimary,
//    surfaceTint = md_theme_dark_surfaceTint,
//    outlineVariant = md_theme_dark_outlineVariant,
//    scrim = md_theme_dark_scrim,
)

private val LightColorScheme = lightColorScheme(

    primary = LightBlue,
    background = LightGray,
    surface = LightGray,
    onPrimary = DarkGray,
    onBackground = DarkGray,
    onSurface = DarkGray

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)



@Composable
fun HabitTrackerAppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
//    val colors = if (!useDarkTheme) {
//        LightColorScheme
//    } else {
//        DarkColorScheme
//    }
    val colors =DarkColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography
    ) {
        // Apply the background color globally
        Surface(
            color = MaterialTheme.colorScheme.background, // Apply background color globally
            contentColor = MaterialTheme.colorScheme.onBackground // Ensure text color is applied
        ) {
            content()
        }
    }
}