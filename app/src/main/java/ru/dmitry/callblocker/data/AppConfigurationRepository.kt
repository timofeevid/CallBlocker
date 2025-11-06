package ru.dmitry.callblocker.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.dmitry.callblocker.domain.model.AppLanguage
import ru.dmitry.callblocker.domain.model.ConfigurationModel
import ru.dmitry.callblocker.domain.model.ThemeColor

class AppConfigurationRepository(
    private val context: Context
) {

    private val preferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    var configuration: ConfigurationModel
        get() = preferences.getString(KEY_CONFIG, null)
            ?.let { config -> Json.decodeFromString<ConfigurationModel>(config) }
            ?: createDefaultConfig()
        set(value) {
            val jsonString = Json.encodeToString(value)
            preferences.edit { putString(KEY_CONFIG, jsonString) }
        }

    fun observe(): Flow<ConfigurationModel> = callbackFlow {
        trySend(configuration)
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { value, key ->
            if (key == KEY_CONFIG) {
                trySend(configuration)
            }
        }
        preferences.registerOnSharedPreferenceChangeListener(listener)
        awaitClose {
            preferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    private fun createDefaultConfig(): ConfigurationModel {
        return ConfigurationModel(
            isScreenRoleGrand = false,
            isBlockUnknownNumberEnable = false,
            isPushEnable = true,
            language = AppLanguage.RU.code,
            theme = ThemeColor.DARK.themeName
        )
    }

    private companion object {
        const val PREFS_NAME = "CallScreenerPrefs"
        const val KEY_CONFIG = "config"
    }
}