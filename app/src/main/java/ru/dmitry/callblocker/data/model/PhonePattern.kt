package ru.dmitry.callblocker.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PhonePattern(
    val pattern: String,
    val isNegativePattern: Boolean
)