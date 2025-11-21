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
import ru.dmitry.callblocker.data.model.CallEntry
import ru.dmitry.callblocker.domain.model.ScreenedCall
import ru.dmitry.callblocker.domain.repository.AppConfigurationRepositoryApi
import ru.dmitry.callblocker.domain.repository.CallHistoryRepositoryApi

class CallHistoryRepository(
    private val context: Context,
    private val appConfigurationRepository: AppConfigurationRepositoryApi,
) : CallHistoryRepositoryApi {

    companion object {
        private const val PREFS_NAME = "CallScreenerPrefs"
        private const val KEY_CALL_LOG = "screened_calls_log"
    }

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private val numberCallsToStore: Int
        get() = appConfigurationRepository.configuration.numberOfBlockCallToStore

    override fun saveScreenedCall(
        phoneNumber: String,
        wasBlocked: Boolean
    ) {
        try {
            val existingLog = prefs.getString(KEY_CALL_LOG, "[]")
            val logArray = if (existingLog.isNullOrEmpty()) {
                mutableListOf()
            } else {
                Json.decodeFromString<List<CallEntry>>(existingLog).toMutableList()
            }

            val callEntry = CallEntry(
                phoneNumber = phoneNumber,
                timestamp = System.currentTimeMillis(),
                wasBlocked = wasBlocked
            )

            logArray.add(callEntry)
            val trimmedArray = if (logArray.size > numberCallsToStore) {
                logArray.takeLast(numberCallsToStore)
            } else {
                logArray
            }

            val jsonString = Json.encodeToString(trimmedArray)
            prefs.edit { putString(KEY_CALL_LOG, jsonString) }

            Log.d(Const.APP_TAG, "Saved screened call: $phoneNumber, blocked: $wasBlocked")
        } catch (e: Exception) {
            Log.e(Const.APP_TAG, "Error saving call log", e)
        }
    }

    override fun getScreenedCalls(): List<ScreenedCall> {
        return try {
            val logString = prefs.getString(KEY_CALL_LOG, "[]")
            if (logString.isNullOrEmpty()) {
                emptyList()
            } else {
                val logArray = Json.decodeFromString<List<CallEntry>>(logString)
                logArray.map {
                    ScreenedCall(it.phoneNumber, it.timestamp, it.wasBlocked)
                }.sortedByDescending { it.timestamp }
            }
        } catch (e: Exception) {
            Log.e(Const.APP_TAG, "Error reading call log", e)
            emptyList()
        }
    }

    override fun observeScreenedCalls(): Flow<List<ScreenedCall>> = callbackFlow {
        trySend(getScreenedCalls())
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == KEY_CALL_LOG) {
                trySend(getScreenedCalls())
            }
        }
        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose {
            prefs.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    override fun clearCallLog() {
        prefs.edit { putString(KEY_CALL_LOG, "[]") }
    }
}