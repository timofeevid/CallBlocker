package ru.dmitry.callblocker.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.dmitry.callblocker.domain.model.AppLanguage
import ru.dmitry.callblocker.domain.model.ConfigurationModel
import javax.inject.Inject

class ObserveLanguageUseCase @Inject constructor(
    private val appConfigurationInteractor: AppConfigurationInteractor
) {
    operator fun invoke(): Flow<AppLanguage> {
        return appConfigurationInteractor.observeConfiguration()
            .map { config -> config.toLanguage() }
    }

    private fun ConfigurationModel.toLanguage(): AppLanguage {
        return AppLanguage.entries.first { it.code == language }
    }
}