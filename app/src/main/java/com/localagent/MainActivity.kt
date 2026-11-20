package com.localagent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.localagent.data.SettingsRepository
import com.localagent.data.ThemeMode
import com.localagent.ui.HomeScreen
import com.localagent.ui.theme.LocalAgentTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settings = SettingsRepository(applicationContext)
        setContent {
            val mode by settings.themeMode.collectAsStateWithLifecycle(initialValue = ThemeMode.SYSTEM)
            val darkTheme = when (mode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            var dynamicColor by remember { mutableStateOf(true) }
            LocalAgentTheme(darkTheme = darkTheme, dynamicColor = dynamicColor) {
                HomeScreen(
                    onToggleTheme = { scope ->
                        scope.launch {
                            val next = if (darkTheme) ThemeMode.LIGHT else ThemeMode.DARK
                            settings.setThemeMode(next)
                        }
                    }
                )
            }
        }
    }
}
