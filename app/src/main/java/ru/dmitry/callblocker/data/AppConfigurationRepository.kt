package ru.dmitry.callblocker.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.serialization.json.Json
import ru.dmitry.callblocker.core.Const
import ru.dmitry.callblocker.domain.model.AppLanguage
import ru.dmitry.callblocker.domain.model.AppThemeColor
import ru.dmitry.callblocker.domain.model.ConfigurationModel

class AppConfigurationRepository(
    private val context: Context
) {

    private val preferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    var configuration: ConfigurationModel
        get() = preferences.getString(KEY_CONFIG, null)
            ?.let { config ->
                try {
                    Json.decodeFromString<ConfigurationModel>(config)
                } catch (e: Exception) {
                    Log.e(Const.APP_TAG, "Error while decode from cache", e)
                    createDefaultConfig()
                }
            }
            ?: createDefaultConfig()
        set(value) {
            val jsonString = Json.encodeToString(value)
            preferences.edit { putString(KEY_CONFIG, jsonString) }
        }

    fun observe(): Flow<ConfigurationModel> = callbackFlow {
        trySend(configuration)
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
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
            isBlockByPatternEnable = true,
            isPushEnable = true,
            numberOfBlockCallToStore = 100,
            language = AppLanguage.RU.code,
            theme = AppThemeColor.DARK.themeName
        )
    }

    private companion object {
        const val PREFS_NAME = "CallScreenerPrefs"
        const val KEY_CONFIG = "config"
    }
}