package ru.dmitry.callblocker.ui.main

import ru.dmitry.callblocker.domain.model.ScreenedCall

data class MainScreenUiState(
    val hasPermissions: Boolean = false,
    val hasScreeningRole: Boolean = false,
    val blockUnknownCalls: Boolean = false,
    val screenedCalls: List<ScreenedCall> = emptyList(),
    val lastCallScreenedTime: Long = 0L,
    val lastBlockedCall: ScreenedCall? = null
)