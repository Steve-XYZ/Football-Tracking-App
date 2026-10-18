package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val FutbolDarkColorScheme = darkColorScheme(
    primary = NeonGrassGreen,
    onPrimary = PitchDark,
    primaryContainer = PitchCardElevated,
    onPrimaryContainer = NeonGrassGreen,
    secondary = ElectricCyan,
    onSecondary = PitchDark,
    secondaryContainer = PitchSurfaceVariant,
    onSecondaryContainer = ElectricCyan,
    tertiary = TrophyGold,
    onTertiary = PitchDark,
    background = PitchDark,
    onBackground = TextPrimary,
    surface = PitchSurface,
    onSurface = TextPrimary,
    surfaceVariant = PitchSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = PitchBorder,
    error = LiveAlertRed,
    onError = TextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Dark-mode-first as requested
    dynamicColor: Boolean = false, // Keep bespoke pitch aesthetic
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FutbolDarkColorScheme,
        typography = Typography,
        content = content
    )
}
