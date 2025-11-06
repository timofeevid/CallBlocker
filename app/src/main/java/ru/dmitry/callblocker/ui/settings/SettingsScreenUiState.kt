package ru.dmitry.callblocker.ui.settings

import ru.dmitry.callblocker.domain.model.Language
import ru.dmitry.callblocker.domain.model.ThemeColor

data class SettingsScreenUiState(
    val isScreenRoleGrand: Boolean = false,
    val isBlockUnknownNumberEnable: Boolean = false,
    val isPushEnable: Boolean = true,
    val language: Language = Language.ENG,
    val theme: ThemeColor = ThemeColor.DARK
)