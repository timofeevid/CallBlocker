package ru.dmitry.callblocker.data

import android.content.Context
import android.content.SharedPreferences

object AppConfigurationRepository {

    private const val PREFS_NAME = "CallScreenerPrefs"
    private const val KEY_BLOCK_UNKNOWN = "block_unknown_numbers"
    private const val KEY_SERVICE_ACTIVE = "service_active"
    private const val KEY_LAST_CALL_TIME = "last_call_screened_time"

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun shouldBlockUnknownNumbers(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_BLOCK_UNKNOWN, false)
    }

    fun setBlockUnknownNumbers(context: Context, shouldBlock: Boolean) {
        getPreferences(context).edit().putBoolean(KEY_BLOCK_UNKNOWN, shouldBlock).apply()
    }

    fun markServiceActive(context: Context) {
        getPreferences(context).edit()
            .putBoolean(KEY_SERVICE_ACTIVE, true)
            .putLong(KEY_LAST_CALL_TIME, System.currentTimeMillis())
            .apply()
    }

    fun isServiceActive(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_SERVICE_ACTIVE, false)
    }

    fun getLastCallScreenedTime(context: Context): Long {
        return getPreferences(context).getLong(KEY_LAST_CALL_TIME, 0L)
    }
}