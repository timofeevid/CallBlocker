package ru.dmitry.callblocker.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class AppConfigurationRepository(
    private val context: Context
) {

    private val preferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun shouldBlockUnknownNumbers(): Boolean {
        return preferences.getBoolean(KEY_BLOCK_UNKNOWN, false)
    }

    fun setBlockUnknownNumbers(shouldBlock: Boolean) {
        preferences.edit { putBoolean(KEY_BLOCK_UNKNOWN, shouldBlock) }
    }

    fun markServiceActive() {
        preferences.edit {
            putBoolean(KEY_SERVICE_ACTIVE, true)
                .putLong(KEY_LAST_CALL_TIME, System.currentTimeMillis())
        }
    }

    fun isServiceActive(): Boolean {
        return preferences.getBoolean(KEY_SERVICE_ACTIVE, false)
    }

    fun getLastCallScreenedTime(): Long {
        return preferences.getLong(KEY_LAST_CALL_TIME, 0L)
    }

    private companion object {
        const val PREFS_NAME = "CallScreenerPrefs"
        const val KEY_BLOCK_UNKNOWN = "block_unknown_numbers"
        const val KEY_SERVICE_ACTIVE = "service_active"
        const val KEY_LAST_CALL_TIME = "last_call_screened_time"
    }
}