package com.neelcortex.deskby9.metro.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = MetroBlue80,
    onPrimary = Grey10,
    primaryContainer = MetroBlue20,
    onPrimaryContainer = MetroBlue80,
    secondary = MetroOrange80,
    onSecondary = Grey10,
    secondaryContainer = MetroOrange20,
    onSecondaryContainer = MetroOrange80,
    tertiary = MetroGreen80,
    onTertiary = Grey10,
    tertiaryContainer = MetroGreen20,
    onTertiaryContainer = MetroGreen80,
    background = Grey10,
    onBackground = Grey90,
    surface = Grey20,
    onSurface = Grey90,
    surfaceVariant = Grey20,
    onSurfaceVariant = Grey90
)

private val LightColorScheme = lightColorScheme(
    primary = MetroBlue40,
    onPrimary = Grey99,
    primaryContainer = MetroBlue80,
    onPrimaryContainer = MetroBlue20,
    secondary = MetroOrange40,
    onSecondary = Grey99,
    secondaryContainer = MetroOrange80,
    onSecondaryContainer = MetroOrange20,
    tertiary = MetroGreen40,
    onTertiary = Grey99,
    tertiaryContainer = MetroGreen80,
    onTertiaryContainer = MetroGreen20,
    background = Grey99,
    onBackground = Grey10,
    surface = Grey95,
    onSurface = Grey10,
    surfaceVariant = Grey90,
    onSurfaceVariant = Grey20
)

@Composable
fun DeskBy9Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
