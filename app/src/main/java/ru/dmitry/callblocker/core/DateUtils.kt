package ru.dmitry.callblocker.core

import ru.dmitry.callblocker.R
import java.text.SimpleDateFormat
import java.util.Locale

object DateUtils {

    const val DD_MM_YYYY_HH_MM = "dd.MM.yyyy HH:mm"

    fun String.toFormatter(): SimpleDateFormat {
        return SimpleDateFormat(this, Locale.getDefault())
    }
    
    fun getTimeAgoString(millisAgo: Long, context: android.content.Context): String {
        val seconds = millisAgo / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            days > 0 -> context.getString(R.string.days_ago, days, if (days > 1) "s" else "")
            hours > 0 -> context.getString(R.string.hours_ago, hours, if (hours > 1) "s" else "")
            minutes > 0 -> context.getString(R.string.minutes_ago, minutes, if (minutes > 1) "s" else "")
            seconds > 0 -> context.getString(R.string.seconds_ago, seconds, if (seconds > 1) "s" else "")
            else -> context.getString(R.string.just_now)
        }
    }
}