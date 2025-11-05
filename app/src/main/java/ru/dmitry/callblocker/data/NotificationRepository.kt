package ru.dmitry.callblocker.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import ru.dmitry.callblocker.domain.model.NotificationData
import ru.dmitry.callblocker.ui.MainActivity

class NotificationRepository(
    private val context: Context
) {

    fun showBlockedCallNotification(params: NotificationData) {
        createNotificationChannel(params.channelId, params.channelName)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, params.channelId)
            .setSmallIcon(android.R.drawable.ic_menu_call)
            .setContentTitle(params.contentTitle)
            .setContentText(params.getDisplayContentText())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel(
        channelId: String = DEFAULT_CHANNEL_ID,
        channelName: String = DEFAULT_CHANNEL_NAME
    ) {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = "Notifications for blocked calls"
        }

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private companion object {
        private const val DEFAULT_CHANNEL_ID = "call_screener_channel"
        private const val DEFAULT_CHANNEL_NAME = "Call Screening"
        private const val NOTIFICATION_ID = 1001
    }
}