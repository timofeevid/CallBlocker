package ru.dmitry.callblocker.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.dmitry.callblocker.domain.model.ThemeColor
import ru.dmitry.callblocker.ui.home.HomeScreen
import ru.dmitry.callblocker.ui.settings.SettingsScreen
import ru.dmitry.callblocker.ui.settings.SettingsScreenViewModel
import ru.dmitry.callblocker.ui.theme.CallBlockerTheme

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val settingsViewModel: SettingsScreenViewModel = hiltViewModel()
    val settingsUiState by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val isDarkTheme = settingsUiState.theme == ThemeColor.DARK

    CallBlockerTheme(darkTheme = isDarkTheme) {
        Scaffold(
            bottomBar = {
                BottomNavigationBar(navController = navController)
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ) {
                    composable(route = Screen.Home.route) {
                        HomeScreen()
                    }
                    composable(route = Screen.Settings.route) {
                        SettingsScreen()
                    }
                }
            }
        }
    }
}