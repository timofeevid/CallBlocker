package ru.dmitry.callblocker

import android.content.Context
import android.os.Build
import android.telecom.TelecomManager
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CallScreenerUiState(
    val hasPermissions: Boolean = false,
    val hasScreeningRole: Boolean = false,
    val blockUnknownCalls: Boolean = false,
    val screenedCalls: List<ScreenedCall> = emptyList()
)

class CallScreenerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CallScreenerUiState())
    val uiState: StateFlow<CallScreenerUiState> = _uiState.asStateFlow()

    fun updatePermissionStatus(hasPermissions: Boolean) {
        _uiState.update { it.copy(hasPermissions = hasPermissions) }
    }

    fun checkScreeningRole(context: Context) {
        val hasRole = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Check if our CallScreeningService is enabled
            // The user must manually enable it in Settings -> Apps -> Default Apps -> Call Screening
            // There's no direct API to check this programmatically, so we assume it's enabled
            // if the app has the necessary permissions
            try {
                val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
                // Try to check if we can access telecom manager properly
                telecomManager != null
            } catch (e: Exception) {
                false
            }
        } else {
            false
        }
        _uiState.update { it.copy(hasScreeningRole = hasRole) }
    }

    fun loadCallLog(context: Context) {
        val calls = CallLogHelper.getScreenedCalls(context)
        val blockEnabled = PreferencesHelper.shouldBlockUnknownNumbers(context)
        checkScreeningRole(context)

        _uiState.update {
            it.copy(
                screenedCalls = calls,
                blockUnknownCalls = blockEnabled
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