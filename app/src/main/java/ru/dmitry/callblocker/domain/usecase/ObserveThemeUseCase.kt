package ru.dmitry.callblocker.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.dmitry.callblocker.domain.model.ConfigurationModel
import ru.dmitry.callblocker.domain.model.ThemeColor
import javax.inject.Inject

class ObserveThemeUseCase @Inject constructor(
    private val appConfigurationInteractor: AppConfigurationInteractor
) {
    operator fun invoke(): Flow<ThemeColor> {
        return appConfigurationInteractor.observeConfiguration()
            .map { config -> config.toTheme() }
    }

    private fun ConfigurationModel.toTheme(): ThemeColor {
        return ThemeColor.entries.first { it.themeName == theme }
    }
}