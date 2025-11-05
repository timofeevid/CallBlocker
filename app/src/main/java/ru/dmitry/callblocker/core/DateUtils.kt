package ru.dmitry.callblocker.core

import java.text.SimpleDateFormat
import java.util.Locale

object DateUtils {

    const val DD_MM_YYYY_HH_MM = "dd.MM.yyyy HH:mm"

    fun String.toFormatter(): SimpleDateFormat {
        return SimpleDateFormat(this, Locale.getDefault())
    }
}