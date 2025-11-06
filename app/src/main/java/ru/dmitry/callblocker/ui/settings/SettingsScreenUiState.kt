package ru.dmitry.callblocker.ui.settings

import ru.dmitry.callblocker.domain.model.CallBlockerLanguage
import ru.dmitry.callblocker.domain.model.CallBlockerTheme

data class SettingsScreenUiState(
    val isScreenRoleGrand: Boolean = false,
    val isBlockUnknownNumberEnable: Boolean = false,
    val isPushEnable: Boolean = true,
    val language: CallBlockerLanguage = CallBlockerLanguage.ENG,
    val theme: CallBlockerTheme = CallBlockerTheme.DARK
)