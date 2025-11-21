package ru.dmitry.callblocker.mock

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import ru.dmitry.callblocker.data.model.CallEntry
import ru.dmitry.callblocker.domain.model.ScreenedCall
import ru.dmitry.callblocker.domain.repository.CallHistoryRepositoryApi

/**
 * Mock implementation of CallHistoryRepository for testing purposes
 */
class MockCallHistoryRepository : CallHistoryRepositoryApi {
    
    private val callHistory: MutableList<CallEntry> = mutableListOf()
    
    override fun saveScreenedCall(phoneNumber: String, wasBlocked: Boolean) {
        callHistory.add(
            CallEntry(
                phoneNumber = phoneNumber,
                timestamp = System.currentTimeMillis(),
                wasBlocked = wasBlocked
            )
        )
    }
    
    override fun getScreenedCalls(): List<ScreenedCall> {
        return callHistory.map {
            ScreenedCall(it.phoneNumber, it.timestamp, it.wasBlocked)
        }.sortedByDescending { it.timestamp }
    }
    
    override fun observeScreenedCalls(): Flow<List<ScreenedCall>> {
        return MutableStateFlow(getScreenedCalls())
    }
    
    override fun clearCallLog() {
        callHistory.clear()
    }
    
    fun getBlockedCallsCount(): Int {
        return callHistory.count { it.wasBlocked }
    }
    
    fun getAllowedCallsCount(): Int {
        return callHistory.count { !it.wasBlocked }
    }
}