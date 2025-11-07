package ru.dmitry.callblocker.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.dmitry.callblocker.domain.model.AppThemeColor
import ru.dmitry.callblocker.domain.model.ConfigurationModel
import javax.inject.Inject

class ObserveThemeUseCase @Inject constructor(
    private val appConfigurationInteractor: AppConfigurationInteractor
) {
    operator fun invoke(): Flow<AppThemeColor> {
        return appConfigurationInteractor.observeConfiguration()
            .map { config -> config.toTheme() }
    }

    private fun ConfigurationModel.toTheme(): AppThemeColor {
        return AppThemeColor.entries.first { it.themeName == theme }
    }
}