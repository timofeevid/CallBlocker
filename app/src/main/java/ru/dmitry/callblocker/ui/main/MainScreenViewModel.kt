package ru.dmitry.callblocker.ui.main

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ru.dmitry.callblocker.data.CallHistoryRepository
import ru.dmitry.callblocker.data.AppConfigurationRepository

class MainScreenViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MainScreenUiState())
    val uiState: StateFlow<MainScreenUiState> = _uiState.asStateFlow()

    fun updatePermissionStatus(hasPermissions: Boolean) {
        _uiState.update { it.copy(hasPermissions = hasPermissions) }
    }

    fun hasScreeningRole(boolean: Boolean) {
        _uiState.update { it.copy(hasScreeningRole = boolean) }
    }

    fun loadCallLog(context: Context) {
        val calls = CallHistoryRepository.getScreenedCalls(context)
        val blockEnabled = AppConfigurationRepository.shouldBlockUnknownNumbers(context)
        val lastCallTime = AppConfigurationRepository.getLastCallScreenedTime(context)
        val lastBlocked = calls.firstOrNull { it.wasBlocked }

        _uiState.update {
            it.copy(
                screenedCalls = calls,
                blockUnknownCalls = blockEnabled,
                lastCallScreenedTime = lastCallTime,
                lastBlockedCall = lastBlocked
            )
        }
    }

    fun toggleBlockUnknownCalls(context: Context, enabled: Boolean) {
        AppConfigurationRepository.setBlockUnknownNumbers(context, enabled)
        _uiState.update { it.copy(blockUnknownCalls = enabled) }
    }

    fun clearCallLog(context: Context) {
        CallHistoryRepository.clearCallLog(context)
        _uiState.update { it.copy(screenedCalls = emptyList()) }
    }
}