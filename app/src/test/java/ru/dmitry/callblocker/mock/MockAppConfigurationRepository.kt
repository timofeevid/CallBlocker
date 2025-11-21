package ru.dmitry.callblocker.mock

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import ru.dmitry.callblocker.domain.model.AppLanguage
import ru.dmitry.callblocker.domain.model.AppThemeColor
import ru.dmitry.callblocker.domain.model.ConfigurationModel
import ru.dmitry.callblocker.domain.repository.AppConfigurationRepositoryApi

/**
 * Mock implementation of AppConfigurationRepository for testing purposes
 */
class MockAppConfigurationRepository : AppConfigurationRepositoryApi {
    
    override var configuration: ConfigurationModel = ConfigurationModel(
        isScreenRoleGrand = true,
        isBlockByPatternEnable = true,
        isPushEnable = true,
        numberOfBlockCallToStore = 100,
        language = AppLanguage.RU.code,
        theme = AppThemeColor.DARK.themeName
    )
    
    override fun observe(): Flow<ConfigurationModel> {
        return MutableStateFlow(configuration)
    }
}