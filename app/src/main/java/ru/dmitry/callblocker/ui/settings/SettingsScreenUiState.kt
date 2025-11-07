package ru.dmitry.callblocker.ui.settings

import ru.dmitry.callblocker.domain.model.AppLanguage
import ru.dmitry.callblocker.domain.model.AppThemeColor

data class SettingsScreenUiState(
    val isScreenRoleGrand: Boolean = false,
    val isBlockUnknownNumberEnable: Boolean = false,
    val isPushEnable: Boolean = true,
    val numberOfBlockCallToStore: Int = 0,
    val appLanguage: AppLanguage = AppLanguage.ENG,
    val theme: AppThemeColor = AppThemeColor.DARK
)