package ru.dmitry.callblocker.core

import java.text.SimpleDateFormat

object DateUtils {

    const val DD_MM_YYYY_HH_MM = "dd.MM.yyyy HH:mm"
    const val HH_MM = "HH:mm"

    fun String.toFormatter(): SimpleDateFormat {
        return SimpleDateFormat(this, LanguageUtils.getLocale())
    }
}