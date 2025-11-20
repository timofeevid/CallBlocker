package ru.dmitry.callblocker.mock

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import ru.dmitry.callblocker.data.api.AppConfigurationRepositoryApi
import ru.dmitry.callblocker.domain.model.AppLanguage
import ru.dmitry.callblocker.domain.model.AppThemeColor
import ru.dmitry.callblocker.domain.model.ConfigurationModel

/**
 * Mock implementation of AppConfigurationRepository for testing purposes
 */
class MockAppConfigurationRepository : AppConfigurationRepositoryApi {
    
    override var configuration: ConfigurationModel = ConfigurationModel(
        isScreenRoleGrand = true,
        isBlockUnknownNumberEnable = false,
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