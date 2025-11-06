package ru.dmitry.callblocker.ui.home

import ru.dmitry.callblocker.domain.model.ScreenedCall

data class HomeScreenUiState(
    val hasPermissions: Boolean = false,
    val hasScreeningRole: Boolean = false,
    val blockUnknownCalls: Boolean = false,
    val screenedCalls: List<ScreenedCall> = emptyList(),
    val lastBlockedCall: ScreenedCall? = null
)