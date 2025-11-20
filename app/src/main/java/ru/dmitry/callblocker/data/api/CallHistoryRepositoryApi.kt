package ru.dmitry.callblocker.data.api

import kotlinx.coroutines.flow.Flow
import ru.dmitry.callblocker.domain.model.ScreenedCall

interface CallHistoryRepositoryApi {
    fun saveScreenedCall(phoneNumber: String, wasBlocked: Boolean)
    fun getScreenedCalls(): List<ScreenedCall>
    fun observeScreenedCalls(): Flow<List<ScreenedCall>>
    fun clearCallLog()
}