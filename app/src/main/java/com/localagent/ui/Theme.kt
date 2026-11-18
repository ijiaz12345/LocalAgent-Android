package com.localagent.ui
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Dark = darkColorScheme(primary = Color(0xFF7FB8FF), onPrimary = Color.Black, surface = Color(0xFF0E0E10), onSurface = Color(0xFFEDEDED), background = Color(0xFF070708), onBackground = Color(0xFFEDEDED), surfaceVariant = Color(0xFF1C1C1F))
private val Light = lightColorScheme(primary = Color(0xFF2F6FEB), onPrimary = Color.White, surface = Color(0xFFF7F7F9), onSurface = Color(0xFF141416), background = Color(0xFFFBFBFD), onBackground = Color(0xFF141416), surfaceVariant = Color(0xFFEEEEF2))

@Composable fun LocalAgentTheme(dark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (dark) Dark else Light, content = content)
}
