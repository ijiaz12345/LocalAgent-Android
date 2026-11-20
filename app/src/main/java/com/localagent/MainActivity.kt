package com.localagent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.localagent.data.SettingsRepository
import com.localagent.data.ThemeMode
import com.localagent.navigation.AppNavHost
import com.localagent.ui.theme.LocalAgentTheme

class MainActivity : ComponentActivity() {
    private lateinit var settings: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settings = SettingsRepository(applicationContext)
        setContent {
            val mode by settings.themeMode.collectAsStateWithLifecycle(initialValue = ThemeMode.SYSTEM)
            val darkTheme = when (mode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            LocalAgentTheme(darkTheme = darkTheme, dynamicColor = true) {
                AppNavHost(
                    darkTheme = darkTheme,
                    onToggleTheme = {
                        kotlinx.coroutines.MainScope().launch {
                            val next = if (darkTheme) ThemeMode.LIGHT else ThemeMode.DARK
                            settings.setThemeMode(next)
                        }
                    }
                )
            }
        }
    }
}
