package ru.dmitry.callblocker.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.dmitry.callblocker.domain.model.ConfigurationModel

interface AppConfigurationRepositoryApi {
    var configuration: ConfigurationModel
    fun observe(): Flow<ConfigurationModel>
}