package ru.dmitry.callblocker.core

import android.content.Context
import ru.dmitry.callblocker.data.AppConfigurationRepository
import ru.dmitry.callblocker.domain.model.AppLanguage
import java.util.Locale

object LanguageUtils {

    private var appContext: Context? = null

    fun init(appContext: Context) {
        this.appContext = appContext
    }

    fun getCurrentLanguage(): AppLanguage {
        val settings = appContext
            ?.let { AppConfigurationRepository(it) }
            ?: return DEFAULT_LANGUAGE
        val langCode = settings.configuration.language
        return AppLanguage.entries.first { it.code == langCode }
    }

    fun getLocale(): Locale {
        val language = getCurrentLanguage().code
        val locale = Locale.forLanguageTag(language)
        return locale
    }

    /**
     * Update the locale in the configuration
     */
    fun updateConfiguration(context: Context, language: AppLanguage): Context {
        val locale = Locale.forLanguageTag(language.code)
        Locale.setDefault(locale)

        val configuration = context.resources.configuration
        configuration.setLocale(locale)

        @Suppress("DEPRECATION")
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
        return context.createConfigurationContext(configuration)
    }

    private val DEFAULT_LANGUAGE = AppLanguage.RU
}
