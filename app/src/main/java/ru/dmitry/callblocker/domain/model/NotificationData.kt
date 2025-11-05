package ru.dmitry.callblocker.domain.model

data class NotificationData(
    val phoneNumber: String,
    val contentTitle: String = "Call Blocked",
    val contentText: String = "",
    val channelId: String = "call_screener_channel",
    val channelName: String = "Call Screening"
) {
    fun getDisplayContentText(): String {
        return contentText.ifBlank { "Blocked call from: $phoneNumber" }
    }
}