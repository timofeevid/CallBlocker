package ru.dmitry.callblocker.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import ru.dmitry.callblocker.data.ContactsRepository
import ru.dmitry.callblocker.data.AppConfigurationRepository
import ru.dmitry.callblocker.R
import ru.dmitry.callblocker.core.DateUtils
import ru.dmitry.callblocker.core.DateUtils.toFormatter
import ru.dmitry.callblocker.data.CallHistoryRepository
import ru.dmitry.callblocker.ui.main.MainActivity
import java.util.Date

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
            val isServiceActive = AppConfigurationRepository.isServiceActive(context)
            val lastCallTime = AppConfigurationRepository.getLastCallScreenedTime(context)
            val calls = CallHistoryRepository.getScreenedCalls(context)
            val lastBlockedCall = calls.firstOrNull { it.wasBlocked }
            val blockEnabled = AppConfigurationRepository.shouldBlockUnknownNumbers(context)

            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.widget_call_screener)

            // Set service status
            views.setTextViewText(
                R.id.widget_service_status,
                if (isServiceActive) context.getString(R.string.widget_service_active) else context.getString(R.string.widget_service_inactive)
            )
            views.setInt(
                R.id.widget_service_status,
                "setTextColor",
                if (isServiceActive) 0xFF4CAF50.toInt() else 0xFFE53935.toInt()
            )

            // Set blocking status
            views.setTextViewText(
                R.id.widget_blocking_status,
                if (blockEnabled) context.getString(R.string.widget_blocking_on) else context.getString(R.string.widget_blocking_off)
            )

            // Set last call screened time
            val timeText = if (lastCallTime > 0) {
                val timeAgo = getTimeAgoString(System.currentTimeMillis() - lastCallTime, context)
                context.getString(R.string.widget_last_screened, timeAgo)
            } else {
                context.getString(R.string.widget_no_calls)
            }
            views.setTextViewText(R.id.widget_last_call_time, timeText)

            // Set last blocked call
            if (lastBlockedCall != null) {
                val contactName = ContactsRepository.getContactName(context, lastBlockedCall.phoneNumber)
                val displayName = contactName ?: lastBlockedCall.phoneNumber
                val dateFormat = DateUtils.DD_MM_YYYY_HH_MM.toFormatter()
                val date = dateFormat.format(Date(lastBlockedCall.timestamp))
                views.setTextViewText(
                    R.id.widget_last_blocked,
                    context.getString(R.string.widget_last_blocked, displayName, date)
                )
                views.setViewVisibility(R.id.widget_last_blocked, View.VISIBLE)
            } else {
                views.setViewVisibility(R.id.widget_last_blocked, View.GONE)
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

        private fun getTimeAgoString(millisAgo: Long, context: Context): String {
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

        fun updateAllWidgets(context: Context) {
            val intent = Intent(context, CallScreenerWidgetProvider::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, CallScreenerWidgetProvider::class.java))
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            context.sendBroadcast(intent)
        }
    }
}