package ru.dmitry.callblocker

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CallScreenerUiState(
    val hasPermissions: Boolean = false,
    val hasScreeningRole: Boolean = false,
    val blockUnknownCalls: Boolean = false,
    val screenedCalls: List<ScreenedCall> = emptyList(),
    val lastCallScreenedTime: Long = 0L,
    val lastBlockedCall: ScreenedCall? = null
)

class CallScreenerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CallScreenerUiState())
    val uiState: StateFlow<CallScreenerUiState> = _uiState.asStateFlow()

    fun updatePermissionStatus(hasPermissions: Boolean) {
        _uiState.update { it.copy(hasPermissions = hasPermissions) }
    }

    fun hasScreeningRole(boolean: Boolean) {
        _uiState.update { it.copy(hasScreeningRole = boolean) }
    }

    fun loadCallLog(context: Context) {
        val calls = CallLogHelper.getScreenedCalls(context)
        val blockEnabled = PreferencesHelper.shouldBlockUnknownNumbers(context)
        val lastCallTime = PreferencesHelper.getLastCallScreenedTime(context)
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
        PreferencesHelper.setBlockUnknownNumbers(context, enabled)
        _uiState.update { it.copy(blockUnknownCalls = enabled) }
    }

    fun clearCallLog(context: Context) {
        CallLogHelper.clearCallLog(context)
        _uiState.update { it.copy(screenedCalls = emptyList()) }
    }
}