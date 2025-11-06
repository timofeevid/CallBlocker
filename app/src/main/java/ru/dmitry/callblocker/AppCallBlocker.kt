package ru.dmitry.callblocker

import android.app.Application
import android.content.res.Configuration
import dagger.hilt.android.HiltAndroidApp
import ru.dmitry.callblocker.core.LanguageUtils
import ru.dmitry.callblocker.domain.model.AppLanguage
import ru.dmitry.callblocker.domain.usecase.AppConfigurationInteractor
import javax.inject.Inject

@HiltAndroidApp
class AppCallBlocker : Application() {

    @Inject
    lateinit var appConfigurationInteractor: AppConfigurationInteractor

    override fun onCreate() {
        super.onCreate()
        applySavedLanguage()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        applySavedLanguage()
    }

    private fun applySavedLanguage() {
        LanguageUtils.init(this)
        val langCode = appConfigurationInteractor.getConfiguration().language
        val lang = AppLanguage.entries.first { it.code == langCode }
        LanguageUtils.updateConfiguration(this, lang)
    }
}