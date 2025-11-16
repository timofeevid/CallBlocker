package ru.dmitry.callblocker.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import dagger.hilt.android.AndroidEntryPoint
import ru.dmitry.callblocker.core.LanguageUtils
import ru.dmitry.callblocker.domain.model.AppThemeColor
import ru.dmitry.callblocker.navigation.AppNavigation
import ru.dmitry.callblocker.ui.settings.SettingsScreenViewModel
import ru.dmitry.callblocker.ui.theme.CallBlockerTheme
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsScreenViewModel by viewModels()

    override fun attachBaseContext(newBase: Context) {
        val locale = LanguageUtils.getLocale()
        Locale.setDefault(locale)
        val config = newBase.resources.configuration
        config.setLocale(locale)
        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val settingsUiState = settingsViewModel.uiState.value
            val isDarkTheme = settingsUiState.theme == AppThemeColor.DARK
            UpdateSystemBars(isDarkTheme)
            CallBlockerTheme(darkTheme = isDarkTheme) {
                AppNavigation()
            }
        }
    }

    @Composable
    private fun UpdateSystemBars(isDarkTheme: Boolean) {
        val systemBarsStyle = getSystemBarsStyle(isDarkTheme)

        LaunchedEffect(isDarkTheme) {
            window.statusBarColor = systemBarsStyle.statusBarColor.toArgb()
            window.navigationBarColor = systemBarsStyle.navigationBarColor.toArgb()
            WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
                systemBarsStyle.isLightStatusBar
            WindowCompat.getInsetsController(
                window,
                window.decorView
            ).isAppearanceLightNavigationBars = systemBarsStyle.isLightNavigationBar
        }
    }

    private fun getSystemBarsStyle(isDarkTheme: Boolean): SystemBarsStyle {
        return if (isDarkTheme) {
            SystemBarsStyle(
                statusBarColor = Color.Transparent,
                navigationBarColor = Color.Transparent,
                isLightStatusBar = false,
                isLightNavigationBar = false
            )
        } else {
            SystemBarsStyle(
                statusBarColor = Color.Transparent,
                navigationBarColor = Color.Transparent,
                isLightStatusBar = true,
                isLightNavigationBar = true
            )
        }
    }

    data class SystemBarsStyle(
        val statusBarColor: Color,
        val navigationBarColor: Color,
        val isLightStatusBar: Boolean,
        val isLightNavigationBar: Boolean
    )
}