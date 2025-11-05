package ru.dmitry.callblocker.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CallEntry(
    val phoneNumber: String,
    val timestamp: Long,
    val wasBlocked: Boolean
)