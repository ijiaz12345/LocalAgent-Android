package com.localagent.ui.theme

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

private val LightColors = lightColorScheme(
    primary = Color(0xFF9B2F83),
    secondary = Color(0xFF6542B5),
    background = Color(0xFFF7F4F8),
    surface = Color.White,
    onBackground = Color(0xFF211B27),
    onSurface = Color(0xFF211B27),
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFE94FAF),
    secondary = Color(0xFF8B5CF6),
    background = Color(0xFF08080B),
    surface = Color(0xFF15151B),
    onBackground = Color(0xFFF7F3F9),
    onSurface = Color(0xFFF7F3F9),
    error = Color(0xFFFF8A80),
    errorContainer = Color(0xFF30191E),
    onErrorContainer = Color(0xFFFFDAD6)
)

@Composable
fun LocalAgentTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colors = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        darkTheme -> DarkColors
        else -> LightColors
    }
    MaterialTheme(colorScheme = colors, content = content)
}
