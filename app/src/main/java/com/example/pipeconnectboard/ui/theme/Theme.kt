package com.example.pipeconnectboard.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

val LocalBoardTheme = staticCompositionLocalOf { ClassicBoardTheme }

private val DarkColorScheme = darkColorScheme(
    primary = AccentTealBright,
    onPrimary = BackgroundDark,
    primaryContainer = SurfaceLightDark,
    onPrimaryContainer = TextPrimaryDark,
    secondary = AccentAmberSource,
    onSecondary = BackgroundDark,
    tertiary = AccentEmeraldDestination,
    onTertiary = BackgroundDark,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceLightDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = BorderDark
)

private val LightColorScheme = lightColorScheme(
    primary = AccentTeal,
    onPrimary = SurfaceLight,
    primaryContainer = CardBackgroundLight,
    onPrimaryContainer = TextPrimaryLight,
    secondary = AccentAmberSource,
    onSecondary = SurfaceLight,
    tertiary = AccentEmeraldDestination,
    onTertiary = SurfaceLight,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = BorderLight,
    onSurfaceVariant = TextSecondaryLight,
    outline = BorderLight
)

@Composable
fun PipeConnectBoardTheme(
    themeMode: String = "System", // "Light", "Dark", "System"
    boardThemeName: String = "Classic", // "Classic", "Midnight"
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        "Dark" -> true
        "Light" -> false
        else -> isSystemInDarkTheme()
    }

    val boardTheme = when (boardThemeName) {
        "Midnight" -> MidnightBoardTheme
        else -> ClassicBoardTheme
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalBoardTheme provides boardTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}