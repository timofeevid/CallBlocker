package ru.dmitry.callblocker.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ConfigurationModel(
    val isScreenRoleGrand: Boolean,
    val isBlockUnknownNumberEnable: Boolean,
    val isPushEnable: Boolean,
    val numberOfBlockCallToStore: Int,
    val language: String,
    val theme: String
)