package ru.dmitry.callblocker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.dmitry.callblocker.data.AppConfigurationRepository
import ru.dmitry.callblocker.data.CallHistoryRepository
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val appConfigurationRepository: AppConfigurationRepository,
    private val callHistoryRepository: CallHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeScreenUiState())
    val uiState: StateFlow<HomeScreenUiState> = _uiState.asStateFlow()

    init {
        observeCallLog()
    }

    fun updatePermissionStatus(hasPermissions: Boolean) {
        _uiState.update { it.copy(hasPermissions = hasPermissions) }
    }

    fun hasScreeningRole(boolean: Boolean) {
        _uiState.update { it.copy(hasScreeningRole = boolean) }
    }

    fun loadCallLog() {
        viewModelScope.launch {
            val calls = callHistoryRepository.getScreenedCalls()
            val blockEnabled = appConfigurationRepository.shouldBlockUnknownNumbers()
            val lastCallTime = appConfigurationRepository.getLastCallScreenedTime()
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
    }

    private fun observeCallLog() {
        viewModelScope.launch {
            callHistoryRepository.observeScreenedCalls().collectLatest { calls ->
                val blockEnabled = appConfigurationRepository.shouldBlockUnknownNumbers()
                val lastCallTime = appConfigurationRepository.getLastCallScreenedTime()
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
        }
    }

    fun toggleBlockUnknownCalls(enabled: Boolean) {
        appConfigurationRepository.setBlockUnknownNumbers(enabled)
        _uiState.update { it.copy(blockUnknownCalls = enabled) }
    }

    fun clearCallLog() {
        callHistoryRepository.clearCallLog()
    }
}