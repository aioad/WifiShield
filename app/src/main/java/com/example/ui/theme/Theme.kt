package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CyberBlue,
    secondary = CyberGreen,
    tertiary = CyberOrange,
    background = CyberBlack,
    surface = CyberSlate,
    surfaceVariant = CyberCard,
    error = CyberRed,
    onPrimary = CyberBlack,
    onSecondary = CyberBlack,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = CyberBlue,
    secondary = CyberGreen,
    tertiary = CyberOrange,
    background = Color(0xFFF3F4F6),
    surface = Color.White,
    surfaceVariant = Color(0xFFE5E7EB),
    error = CyberRed,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF111827),
    onSurface = Color(0xFF111827)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force Dark Cyber Theme by default
    dynamicColor: Boolean = false, // Disable dynamic color to maintain branding
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
