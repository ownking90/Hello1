package com.smartmate.ai.ui.theme

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val SmartMatePurple = Color(0xFF6750A4)
val SmartMatePurpleDark = Color(0xFFD0BCFF)
val SmartMateTeal = Color(0xFF03857B)
val SmartMateBackgroundLight = Color(0xFFFFFBFE)
val SmartMateBackgroundDark = Color(0xFF1C1B1F)

private val LightColors = lightColorScheme(
    primary = SmartMatePurple,
    secondary = SmartMateTeal,
    background = SmartMateBackgroundLight,
    surface = SmartMateBackgroundLight
)

private val DarkColors = darkColorScheme(
    primary = SmartMatePurpleDark,
    secondary = SmartMateTeal,
    background = SmartMateBackgroundDark,
    surface = SmartMateBackgroundDark
)

@Composable
fun SmartMateAITheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        useDarkTheme -> DarkColors
        else -> LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SmartMateTypography,
        content = content
    )
}
