package ru.dmitry.callblocker.domain.usecase

import ru.dmitry.callblocker.data.AppConfigurationRepository

class AppConfigInteractor(
    private val appConfigurationRepository: AppConfigurationRepository
) {

    fun shouldBlockUnknownNumbers(): Boolean {
        return appConfigurationRepository.shouldBlockUnknownNumbers()
    }

    fun setBlockUnknownNumbers(shouldBlock: Boolean) {
        appConfigurationRepository.setBlockUnknownNumbers(shouldBlock)
    }

    fun markServiceActive() {
        appConfigurationRepository.markServiceActive()
    }

    fun isServiceActive(): Boolean {
        return appConfigurationRepository.isServiceActive()
    }

    fun getLastCallScreenedTime(): Long {
        return appConfigurationRepository.getLastCallScreenedTime()
    }
}