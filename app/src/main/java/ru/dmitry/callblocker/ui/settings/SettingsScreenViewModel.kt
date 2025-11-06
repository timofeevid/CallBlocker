package ru.dmitry.callblocker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.dmitry.callblocker.domain.model.CallBlockerLanguage
import ru.dmitry.callblocker.domain.model.CallBlockerTheme
import ru.dmitry.callblocker.domain.model.ConfigurationModel
import ru.dmitry.callblocker.domain.usecase.AppConfigurationInteractor
import javax.inject.Inject

@HiltViewModel
class SettingsScreenViewModel @Inject constructor(
    private val appConfigurationInteractor: AppConfigurationInteractor,
) : ViewModel() {

    val uiState: StateFlow<SettingsScreenUiState> = appConfigurationInteractor
        .observeConfiguration()
        .map { config -> config.toUiState() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, SettingsScreenUiState())

    fun updatePushEnabled(enabled: Boolean) {
        val updatedConfig = appConfigurationInteractor.getConfiguration().copy(
            isPushEnable = enabled
        )
        appConfigurationInteractor.updateConfig(updatedConfig)
    }

    fun updateLanguage(language: CallBlockerLanguage) {
        val updatedConfig = appConfigurationInteractor.getConfiguration().copy(
            language = language.langName
        )
        appConfigurationInteractor.updateConfig(updatedConfig)
    }

    fun updateTheme(theme: CallBlockerTheme) {
        val updatedConfig = appConfigurationInteractor.getConfiguration().copy(
            theme = theme.themeName
        )
        appConfigurationInteractor.updateConfig(updatedConfig)
    }

    private fun ConfigurationModel.toUiState(): SettingsScreenUiState {
        return SettingsScreenUiState(
            isScreenRoleGrand = isScreenRoleGrand,
            isBlockUnknownNumberEnable = isBlockUnknownNumberEnable,
            isPushEnable = isPushEnable,
            language = CallBlockerLanguage.entries.find { it.langName == language } ?: CallBlockerLanguage.ENG,
            theme = CallBlockerTheme.entries.find { it.themeName == theme } ?: CallBlockerTheme.DARK
        )
    }
}