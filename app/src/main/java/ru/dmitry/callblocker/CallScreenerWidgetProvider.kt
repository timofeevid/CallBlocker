package ru.dmitry.callblocker


import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import java.text.SimpleDateFormat
import java.util.*

class CallScreenerWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            // Get current data
            val isServiceActive = PreferencesHelper.isServiceActive(context)
            val lastCallTime = PreferencesHelper.getLastCallScreenedTime(context)
            val calls = CallLogHelper.getScreenedCalls(context)
            val lastBlockedCall = calls.firstOrNull { it.wasBlocked }
            val blockEnabled = PreferencesHelper.shouldBlockUnknownNumbers(context)

            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.widget_call_screener)

            // Set service status
            views.setTextViewText(
                R.id.widget_service_status,
                if (isServiceActive) "Service: Active ✓" else "Service: Inactive ✗"
            )
            views.setInt(
                R.id.widget_service_status,
                "setTextColor",
                if (isServiceActive) 0xFF4CAF50.toInt() else 0xFFE53935.toInt()
            )

            // Set blocking status
            views.setTextViewText(
                R.id.widget_blocking_status,
                if (blockEnabled) "Blocking: ON" else "Blocking: OFF"
            )

            // Set last call screened time
            val timeText = if (lastCallTime > 0) {
                val timeAgo = getTimeAgoString(System.currentTimeMillis() - lastCallTime)
                "Last screened: $timeAgo"
            } else {
                "No calls screened yet"
            }
            views.setTextViewText(R.id.widget_last_call_time, timeText)

            // Set last blocked call
            if (lastBlockedCall != null) {
                val contactName = ContactsHelper.getContactName(context, lastBlockedCall.phoneNumber)
                val displayName = contactName ?: lastBlockedCall.phoneNumber
                val dateFormat = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
                val date = dateFormat.format(Date(lastBlockedCall.timestamp))
                views.setTextViewText(
                    R.id.widget_last_blocked,
                    "Last blocked:\n$displayName\n$date"
                )
                views.setViewVisibility(R.id.widget_last_blocked, android.view.View.VISIBLE)
            } else {
                views.setViewVisibility(R.id.widget_last_blocked, android.view.View.GONE)
            }

            // Create an Intent to launch MainActivity
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun getTimeAgoString(millisAgo: Long): String {
            val seconds = millisAgo / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24

            return when {
                days > 0 -> "${days}d ago"
                hours > 0 -> "${hours}h ago"
                minutes > 0 -> "${minutes}m ago"
                seconds > 0 -> "${seconds}s ago"
                else -> "now"
            }
        }

        fun updateAllWidgets(context: Context) {
            val intent = Intent(context, CallScreenerWidgetProvider::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(android.content.ComponentName(context, CallScreenerWidgetProvider::class.java))
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            context.sendBroadcast(intent)
        }
    }
}