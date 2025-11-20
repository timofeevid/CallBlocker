package ru.dmitry.callblocker.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.dmitry.callblocker.data.api.AppConfigurationRepositoryApi
import ru.dmitry.callblocker.domain.model.ConfigurationModel

class AppConfigurationInteractor(
    private val appConfigurationRepository: AppConfigurationRepositoryApi
) {

    fun getConfiguration(): ConfigurationModel {
        return appConfigurationRepository.configuration
    }

    fun updateConfig(configurationModel: ConfigurationModel) {
        appConfigurationRepository.configuration = configurationModel
    }

    fun observeConfiguration(): Flow<ConfigurationModel> {
        return appConfigurationRepository.observe()
    }
}