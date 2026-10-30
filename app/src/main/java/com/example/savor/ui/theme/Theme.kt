package com.example.savor.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SavorLightColorScheme = lightColorScheme(
    primary = SavorPrimary,
    onPrimary = Color.White,
    secondary = SavorSecondary,
    onSecondary = SavorText,
    background = SavorBackground,
    onBackground = SavorText,
    surface = SavorBackground,
    onSurface = SavorText,
    surfaceVariant = SavorCard,
    onSurfaceVariant = SavorText
)

private val SavorDarkColorScheme = darkColorScheme(
    primary = SavorDarkPrimary,
    onPrimary = Color(0xFF4A2800),
    secondary = SavorDarkSecondary,
    onSecondary = Color(0xFF4A2800),
    background = SavorDarkBackground,
    onBackground = SavorDarkText,
    surface = SavorDarkSurface,
    onSurface = SavorDarkText,
    surfaceVariant = SavorDarkCard,
    onSurfaceVariant = SavorDarkText
)

@Composable
fun SavorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) SavorDarkColorScheme else SavorLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
