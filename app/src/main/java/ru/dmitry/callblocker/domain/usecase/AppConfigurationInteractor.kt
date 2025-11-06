package ru.dmitry.callblocker.domain.usecase

import kotlinx.coroutines.flow.Flow
import ru.dmitry.callblocker.data.AppConfigurationRepository
import ru.dmitry.callblocker.domain.model.ConfigurationModel

class AppConfigurationInteractor(
    private val appConfigurationRepository: AppConfigurationRepository
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