package ru.dmitry.callblocker.domain.model

import ru.dmitry.callblocker.core.DateUtils
import ru.dmitry.callblocker.core.DateUtils.toFormatter
import java.util.Date

data class ScreenedCall(
    val phoneNumber: String,
    val timestamp: Long,
    val wasBlocked: Boolean
) {
    val formattedDate: String
        get() {
            val dateFormat = DateUtils.DD_MM_YYYY_HH_MM.toFormatter()
            return dateFormat.format(Date(timestamp))
        }
}