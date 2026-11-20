package com.localagent.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.localagent.ui.history.HistoryScreen
import com.localagent.ui.home.HomeScreen
import com.localagent.ui.settings.SettingsScreen
import com.localagent.ui.terminal.TerminalScreen

@Composable
fun AppNavHost(
    darkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf(
        Triple("home", "الرئيسية", HomeRoute),
        Triple("history", "السجل", HistoryRoute),
        Triple("settings", "الإعدادات", SettingsRoute)
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { (route, label, _) ->
                    val selected = currentDestination?.hierarchy?.any { it.route == route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(
                            when (route) {
                                "home" -> Icons.Default.Home
                                "history" -> Icons.Default.History
                                else -> Icons.Default.Settings
                            },
                            contentDescription = label
                        )},
                        label = { Text(label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fadeIn(tween(220)) + slideInHorizontally(tween(280)) { it / 12 } },
            exitTransition = { fadeOut(tween(160)) + slideOutHorizontally(tween(220)) { -it / 16 } },
            popEnterTransition = { fadeIn(tween(200)) + slideInHorizontally(tween(240)) { -it / 16 } },
            popExitTransition = { fadeOut(tween(160)) + slideOutHorizontally(tween(220)) { it / 12 } }
        ) {
            composable("home") {
                HomeScreen(
                    darkTheme = darkTheme,
                    onToggleTheme = onToggleTheme,
                    onOpenTerminal = { navController.navigate("terminal") }
                )
            }
            composable("history") { HistoryScreen() }
            composable("settings") { SettingsScreen(darkTheme = darkTheme, onToggleTheme = onToggleTheme) }
            composable("terminal") {
                TerminalScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
