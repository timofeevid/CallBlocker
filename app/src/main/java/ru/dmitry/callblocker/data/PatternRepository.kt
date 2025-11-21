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
import ru.dmitry.callblocker.data.model.PhonePattern
import ru.dmitry.callblocker.data.model.PhonePatternType
import ru.dmitry.callblocker.domain.repository.PatternRepositoryApi

class PatternRepository(
    private val context: Context
) : PatternRepositoryApi {

    private val preferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private val defaultPattern = PhonePattern(
        pattern = "*",
        isNegativePattern = true,
        type = PhonePatternType.GENERIC
    )

    private val json = Json { 
        prettyPrint = true
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    override fun getPhonePatterns(): List<PhonePattern> {
        val patternJson = preferences.getString(KEY_PATTERNS, null)
        val defaultPatterns = listOf(defaultPattern)
        val patterns = if (patternJson != null) {
            try {
                json.decodeFromString<List<PhonePattern>>(patternJson)
            } catch (e: Exception) {
                Log.e(Const.APP_TAG, "Error while decode patterns from cache", e)
                defaultPatterns
            }
        } else {
            defaultPatterns
        }
        return if (patterns.contains(defaultPattern)) {
            patterns
        } else {
            patterns.plus(defaultPattern)
        }
    }

    override fun savePhonePatterns(patterns: List<PhonePattern>) {
        val jsonString = json.encodeToString(patterns)
        preferences.edit { putString(KEY_PATTERNS, jsonString) }
    }

    override fun observePhonePatterns(): Flow<List<PhonePattern>> = callbackFlow {
        trySend(getPhonePatterns())
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == KEY_PATTERNS) {
                trySend(getPhonePatterns())
            }
        }
        preferences.registerOnSharedPreferenceChangeListener(listener)
        awaitClose {
            preferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    private companion object {
        const val PREFS_NAME = "CallScreenerPatterns"
        const val KEY_PATTERNS = "patterns"
    }
}