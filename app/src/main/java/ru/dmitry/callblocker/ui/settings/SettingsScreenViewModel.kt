package ru.dmitry.callblocker.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import ru.dmitry.callblocker.domain.model.AppLanguage
import ru.dmitry.callblocker.domain.model.ConfigurationModel
import ru.dmitry.callblocker.domain.model.ThemeColor
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

    fun updateLanguage(appLanguage: AppLanguage) {
        val updatedConfig = appConfigurationInteractor.getConfiguration().copy(
            language = appLanguage.code
        )
        appConfigurationInteractor.updateConfig(updatedConfig)
    }

    fun updateTheme(theme: ThemeColor) {
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
            appLanguage = AppLanguage.entries.find { it.code == language } ?: AppLanguage.ENG,
            theme = ThemeColor.entries.find { it.themeName == theme } ?: ThemeColor.DARK
        )
    }
}