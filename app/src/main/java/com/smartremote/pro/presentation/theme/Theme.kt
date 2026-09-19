package com.smartremote.pro.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = BackgroundDark,
    primaryContainer = DeepBlue,
    onPrimaryContainer = TextPrimaryDark,
    secondary = AccentOrange,
    onSecondary = BackgroundDark,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    error = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = DeepBlue,
    onPrimary = SurfaceLight,
    primaryContainer = ElectricBlue,
    onPrimaryContainer = SurfaceLight,
    secondary = AccentOrange,
    onSecondary = SurfaceLight,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = BackgroundLight,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    error = ErrorRed
)

@Composable
fun SmartRemoteProTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
