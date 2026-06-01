package com.puppyadopt.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Material You 动态颜色方案
private val LightColorScheme = lightColorScheme(
    primary = Pink40,
    onPrimary = Color.White,
    primaryContainer = Pink20,
    onPrimaryContainer = Pink90,
    secondary = Gold,
    onSecondary = Color.White,
    secondaryContainer = GoldLight,
    onSecondaryContainer = Color(0xFF5D4037),
    tertiary = Cyan,
    onTertiary = Color.White,
    tertiaryContainer = CyanLight,
    onTertiaryContainer = Color(0xFF00695C),
    background = PageBackground,
    onBackground = DarkText,
    surface = CardBackground,
    onSurface = DarkText,
    surfaceVariant = Pink10,
    onSurfaceVariant = LightText,
    outline = DividerColor,
    error = Color(0xFFB00020),
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Pink30,
    onPrimary = Pink90,
    primaryContainer = Pink80,
    onPrimaryContainer = Pink10,
    secondary = GoldLight,
    onSecondary = Color(0xFF5D4037),
    secondaryContainer = Gold,
    tertiary = CyanLight,
    onTertiary = Color(0xFF00695C),
    background = Color(0xFF1C1C1E),
    onBackground = Color(0xFFF5E6EA),
    surface = Color(0xFF2C2C2E),
    onSurface = Color(0xFFF5E6EA),
    surfaceVariant = Color(0xFF3C3C3E),
    onSurfaceVariant = Color(0xFFCCB0B4),
    outline = Color(0xFF5C4C4E),
    error = Color(0xFFCF6679),
    onError = Color.Black
)

@Composable
fun PuppyAdoptTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalView.current.context
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
        typography = Typography(),
        content = content
    )
}
