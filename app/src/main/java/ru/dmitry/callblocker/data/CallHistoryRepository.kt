package ru.dmitry.callblocker.data

import android.content.Context
import android.util.Log
import org.json.JSONArray
import org.json.JSONObject
import ru.dmitry.callblocker.core.CONST
import ru.dmitry.callblocker.domain.model.ScreenedCall

// TODO добавить инжект и получение префов. Добавить прослушивание обновлений через flow
object CallHistoryRepository {

    private const val PREFS_NAME = "CallScreenerPrefs"
    private const val KEY_CALL_LOG = "screened_calls_log"

    fun saveScreenedCall(
        context: Context,
        phoneNumber: String,
        wasBlocked: Boolean
    ) {
        try {
            val prefs = getPrefs(context)
            val existingLog = prefs.getString(KEY_CALL_LOG, "[]")
            val logArray = JSONArray(existingLog)

            val callEntry = JSONObject().apply {
                put("phoneNumber", phoneNumber)
                put("timestamp", System.currentTimeMillis())
                put("wasBlocked", wasBlocked)
            }

            logArray.put(callEntry)

            // Keep only last 100 entries
            val trimmedArray = if (logArray.length() > 100) {
                JSONArray().apply {
                    for (i in (logArray.length() - 100) until logArray.length()) {
                        put(logArray.get(i))
                    }
                }
            } else {
                logArray
            }

            prefs.edit().putString(KEY_CALL_LOG, trimmedArray.toString()).apply()
            Log.d(CONST.APP_TAG, "Saved screened call: $phoneNumber, blocked: $wasBlocked")
        } catch (e: Exception) {
            Log.e(CONST.APP_TAG, "Error saving call log", e)
        }
    }

    fun getScreenedCalls(context: Context): List<ScreenedCall> {
        val calls = mutableListOf<ScreenedCall>()

        try {
            val prefs = getPrefs(context)
            val logString = prefs.getString(KEY_CALL_LOG, "[]")
            val logArray = JSONArray(logString)

            for (i in 0 until logArray.length()) {
                val callObj = logArray.getJSONObject(i)
                calls.add(
                    ScreenedCall(
                        phoneNumber = callObj.getString("phoneNumber"),
                        timestamp = callObj.getLong("timestamp"),
                        wasBlocked = callObj.getBoolean("wasBlocked")
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(CONST.APP_TAG, "Error reading call log", e)
        }

        return calls.sortedByDescending { it.timestamp }
    }

    fun clearCallLog(context: Context) {
        val prefs = getPrefs(context)
        prefs.edit().putString(KEY_CALL_LOG, "[]").apply()
    }

    private fun getPrefs(context: Context) = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
}