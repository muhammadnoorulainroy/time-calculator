package com.example.timecalculator.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Theme color definitions
val GreenPrimary = Color(0xFF2E7D32)
val GreenLight = Color(0xFF4CAF50)
val GreenDark = Color(0xFF1B5E20)
val TealAccent = Color(0xFF26A69A)
val GradientStart = Color(0xFF1B8A5A)
val GradientEnd = Color(0xFF7BC67E)

// Light theme colors
private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8F5E9),
    onPrimaryContainer = Color(0xFF1B5E20),
    secondary = TealAccent,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2F1),
    onSecondaryContainer = Color(0xFF004D40),
    tertiary = Color(0xFF43A047),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFC8E6C9),
    onTertiaryContainer = Color(0xFF1B5E20),
    error = Color(0xFFE53935),
    errorContainer = Color(0xFFFFCDD2),
    onError = Color.White,
    onErrorContainer = Color(0xFFB71C1C),
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF1A237E),
    surface = Color.White,
    onSurface = Color(0xFF1A237E),
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF37474F)
)

// Dark theme colors
private val DarkColorScheme = darkColorScheme(
    primary = GreenLight,
    onPrimary = Color(0xFF003300),
    primaryContainer = Color(0xFF1B5E20),
    onPrimaryContainer = Color(0xFFA5D6A7),
    secondary = Color(0xFF80CBC4),
    onSecondary = Color(0xFF003D33),
    secondaryContainer = Color(0xFF00695C),
    onSecondaryContainer = Color(0xFFB2DFDB),
    tertiary = Color(0xFF81C784),
    onTertiary = Color(0xFF003300),
    tertiaryContainer = Color(0xFF2E7D32),
    onTertiaryContainer = Color(0xFFC8E6C9),
    error = Color(0xFFEF5350),
    errorContainer = Color(0xFFB71C1C),
    onError = Color.White,
    onErrorContainer = Color(0xFFFFCDD2),
    background = Color(0xFF121212),
    onBackground = Color(0xFFE8F5E9),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE8F5E9),
    surfaceVariant = Color(0xFF2D2D2D),
    onSurfaceVariant = Color(0xFFA5D6A7)
)

// Main theme composable
@Composable
fun TimeCalculatorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
