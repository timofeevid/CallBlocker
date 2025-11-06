package ru.dmitry.callblocker.ui

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import ru.dmitry.callblocker.core.LanguageUtils
import ru.dmitry.callblocker.navigation.AppNavigation
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        val language = LanguageUtils.getCurrentLanguage().code
        val locale = Locale.forLanguageTag(language)
        Locale.setDefault(locale)

        val config = newBase.resources.configuration
        config.setLocale(locale)

        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavigation()
        }
    }
}