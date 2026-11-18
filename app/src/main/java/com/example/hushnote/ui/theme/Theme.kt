package com.example.hushnote.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = WarmAmber,
    onPrimary = MidnightNavy,
    primaryContainer = SlateBlue,
    onPrimaryContainer = SoftIvory,
    secondary = MutedSage,
    onSecondary = MidnightNavy,
    background = MidnightNavy,
    onBackground = SoftIvory,
    surface = SurfaceDark,
    onSurface = SoftIvory,
    error = MutedCoral,
    onError = SoftIvory
)

private val LightColorScheme = lightColorScheme(
    primary = MidnightNavy,
    onPrimary = SoftIvory,
    primaryContainer = SlateBlue,
    onPrimaryContainer = SoftIvory,
    secondary = WarmAmber,
    onSecondary = MidnightNavy,
    background = SoftIvory,
    onBackground = MidnightNavy,
    surface = SurfaceLight,
    onSurface = MidnightNavy,
    error = MutedCoral,
    onError = SoftIvory
)

@Composable
fun HushNoteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Default to false to preserve brand identity
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}