package ru.dmitry.callblocker.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import dagger.hilt.android.AndroidEntryPoint
import ru.dmitry.callblocker.R
import ru.dmitry.callblocker.core.DateUtils
import ru.dmitry.callblocker.core.DateUtils.toFormatter
import ru.dmitry.callblocker.data.AppConfigurationRepository
import ru.dmitry.callblocker.data.CallHistoryRepository
import ru.dmitry.callblocker.data.ContactsRepository
import ru.dmitry.callblocker.ui.main.MainActivity
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class CallScreenerWidgetProvider : AppWidgetProvider() {

    @Inject
    lateinit var appConfigurationRepository: AppConfigurationRepository

    @Inject
    lateinit var callHistoryRepository: CallHistoryRepository

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val lastCallTime = appConfigurationRepository.getLastCallScreenedTime()
        val calls = callHistoryRepository.getScreenedCalls()
        val lastBlockedCall = calls.firstOrNull { it.wasBlocked }
        val blockEnabled = appConfigurationRepository.shouldBlockUnknownNumbers()

        val views = RemoteViews(context.packageName, R.layout.widget_call_screener)

        views.setTextViewText(
            R.id.widget_blocking_status,
            if (blockEnabled) context.getString(R.string.widget_blocking_on) else context.getString(
                R.string.widget_blocking_off
            )
        )

        val timeText = if (lastCallTime > 0) {
            val timeAgo =
                DateUtils.getTimeAgoString(System.currentTimeMillis() - lastCallTime, context)
            context.getString(R.string.widget_last_screened, timeAgo)
        } else {
            context.getString(R.string.widget_no_calls)
        }
        views.setTextViewText(R.id.widget_last_call_time, timeText)

        if (lastBlockedCall != null) {
            val contactName =
                ContactsRepository(context).getContactName(lastBlockedCall.phoneNumber)
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

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    companion object {
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