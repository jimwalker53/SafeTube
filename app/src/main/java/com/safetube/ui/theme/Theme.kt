package com.safetube.ui.theme

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

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimaryLight,
    primaryContainer = Primary.copy(alpha = 0.1f),
    onPrimaryContainer = Primary,
    secondary = Secondary,
    onSecondary = OnSecondaryLight,
    secondaryContainer = Secondary.copy(alpha = 0.1f),
    onSecondaryContainer = Secondary,
    tertiary = Success,
    onTertiary = OnPrimaryLight,
    error = Error,
    onError = OnError,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = DividerLight,
    outlineVariant = DividerLight.copy(alpha = 0.5f)
)

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimaryDark,
    primaryContainer = Primary.copy(alpha = 0.2f),
    onPrimaryContainer = Primary,
    secondary = Secondary,
    onSecondary = OnSecondaryDark,
    secondaryContainer = Secondary.copy(alpha = 0.2f),
    onSecondaryContainer = Secondary,
    tertiary = Success,
    onTertiary = OnPrimaryDark,
    error = Error,
    onError = OnError,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = DividerDark,
    outlineVariant = DividerDark.copy(alpha = 0.5f)
)

@Composable
fun SafeTubeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
