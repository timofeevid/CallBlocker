package ru.dmitry.callblocker.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import ru.dmitry.callblocker.data.CallHistoryRepository
import ru.dmitry.callblocker.domain.usecase.AppConfigurationInteractor
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val appConfigurationInteractor: AppConfigurationInteractor,
    private val callHistoryRepository: CallHistoryRepository
) : ViewModel() {

    private val permissionStateFlow = MutableStateFlow(false)
    val uiState: StateFlow<HomeScreenUiState> = combine(
        flow = callHistoryRepository.observeScreenedCalls(),
        flow2 = appConfigurationInteractor.observeConfiguration(),
        flow3 = permissionStateFlow,
        transform = { calls, config, isPermissionGrand ->
            HomeScreenUiState(
                hasPermissions = isPermissionGrand,
                screenedCalls = calls,
                hasScreeningRole = config.isScreenRoleGrand,
                blockUnknownCalls = config.isBlockUnknownNumberEnable,
                blockByPattern = config.isBlockByPatternEnable,
                lastBlockedCall = calls.firstOrNull { it.wasBlocked }
            )
        }
    ).stateIn(viewModelScope, SharingStarted.Eagerly, HomeScreenUiState())


    fun updatePermissionStatus(hasPermissions: Boolean) {
        permissionStateFlow.value = hasPermissions
    }

    fun hasScreeningRole(boolean: Boolean) {
        val currentConfig = appConfigurationInteractor.getConfiguration()
        val newConfig = currentConfig.copy(isScreenRoleGrand = boolean)
        appConfigurationInteractor.updateConfig(newConfig)
    }

    fun toggleBlockUnknownCalls(enabled: Boolean) {
        val currentConfig = appConfigurationInteractor.getConfiguration()
        val newConfig = currentConfig.copy(isBlockUnknownNumberEnable = enabled)
        appConfigurationInteractor.updateConfig(newConfig)
    }

    fun clearCallLog() {
        callHistoryRepository.clearCallLog()
    }

    fun toggleBlockByPattern(enabled: Boolean) {
        val currentConfig = appConfigurationInteractor.getConfiguration()
        val newConfig = currentConfig.copy(isBlockByPatternEnable = enabled)
        appConfigurationInteractor.updateConfig(newConfig)
    }
}