package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = CemaPrimaryDark,
    onPrimary = CemaOnPrimaryDark,
    primaryContainer = CemaPrimaryContainerDark,
    onPrimaryContainer = CemaOnPrimaryContainerDark,
    secondary = CemaSecondary,
    onSecondary = CemaOnSecondary,
    secondaryContainer = CemaSecondaryContainer,
    onSecondaryContainer = CemaOnSecondaryContainer,
    background = CemaBackgroundDark,
    surface = CemaSurfaceDark,
    surfaceVariant = CemaSurfaceVariantDark,
    onBackground = CemaOnSurfaceDark,
    onSurface = CemaOnSurfaceDark,
    onSurfaceVariant = CemaOnSurfaceVariantDark,
    outline = CemaOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = CemaPrimary,
    onPrimary = CemaOnPrimary,
    primaryContainer = CemaPrimaryContainer,
    onPrimaryContainer = CemaOnPrimaryContainer,
    secondary = CemaSecondary,
    onSecondary = CemaOnSecondary,
    secondaryContainer = CemaSecondaryContainer,
    onSecondaryContainer = CemaOnSecondaryContainer,
    background = CemaBackgroundLight,
    surface = CemaSurfaceLight,
    surfaceVariant = CemaSurfaceVariantLight,
    onBackground = CemaOnSurfaceLight,
    onSurface = CemaOnSurfaceLight,
    onSurfaceVariant = CemaOnSurfaceVariantLight,
    outline = CemaOutlineLight
)

@Composable
fun CemaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Retain alias for compatibility
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    CemaTheme(darkTheme = darkTheme, content = content)
}
