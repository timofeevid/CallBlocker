package ru.dmitry.callblocker.ui.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import dagger.hilt.android.AndroidEntryPoint
import ru.dmitry.callblocker.R
import ru.dmitry.callblocker.core.DateUtils.HH_MM
import ru.dmitry.callblocker.core.DateUtils.toFormatter
import ru.dmitry.callblocker.core.formatters.PhoneNumberFormatter
import ru.dmitry.callblocker.data.ContactsRepository
import ru.dmitry.callblocker.domain.repository.CallHistoryRepositoryApi
import ru.dmitry.callblocker.domain.usecase.AppConfigurationInteractor
import ru.dmitry.callblocker.ui.MainActivity
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class CallScreenerWidgetProvider : AppWidgetProvider() {

    @Inject
    lateinit var appConfigurationInteractor: AppConfigurationInteractor

    @Inject
    lateinit var callHistoryRepository: CallHistoryRepositoryApi

    private var shouldBlockByPatternNumbers: Boolean
        get() = appConfigurationInteractor.getConfiguration().isBlockByPatternEnable
        set(value) {
            val currentConfig = appConfigurationInteractor.getConfiguration()
            val newConfig = currentConfig.copy(isBlockByPatternEnable = value)
            appConfigurationInteractor.updateConfig(newConfig)
        }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            ACTION_TOGGLE_BLOCK -> {
                val blockStatus = intent.getBooleanExtra(EXTRA_BLOCK_STATUS, false)
                shouldBlockByPatternNumbers = !blockStatus
                updateWidget(context)
            }
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val calls = callHistoryRepository.getScreenedCalls()
        val zoneId = ZoneId.systemDefault()
        val blockedCalls = calls
            .filter {
                val date = Instant.ofEpochMilli(it.timestamp)
                    .atZone(zoneId)
                    .toLocalDate()
                it.wasBlocked && date == LocalDate.now(zoneId)
            }
            .take(5)

        val views = RemoteViews(context.packageName, R.layout.widget_call_screener)

        val config = appConfigurationInteractor.getConfiguration()
        val textColor = if (config.isBlockByPatternEnable) {
            0xFF00FF00.toInt() // Green color
        } else {
            0xFFCCCCCC.toInt() // Gray color
        }
        views.setTextColor(R.id.widget_title, textColor)

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            appWidgetId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

        views.removeAllViews(R.id.widget_blocked_calls_container)

        if (blockedCalls.isNotEmpty()) {
            for (call in blockedCalls) {
                val itemView = RemoteViews(context.packageName, R.layout.widget_blocked_call_item)

                val contactName = ContactsRepository(context).getContactName(call.phoneNumber)
                val displayName = contactName ?: PhoneNumberFormatter.format(call.phoneNumber)
                val timeFormat = HH_MM.toFormatter()
                val timeText = timeFormat.format(Date(call.timestamp))

                itemView.setTextViewText(R.id.widget_blocked_number, displayName)
                itemView.setTextViewText(R.id.widget_blocked_time, timeText)

                views.addView(R.id.widget_blocked_calls_container, itemView)
            }
        }

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    companion object {
        private const val ACTION_TOGGLE_BLOCK = "ru.dmitry.callblocker.TOGGLE_BLOCK"
        private const val EXTRA_BLOCK_STATUS = "extra_block_status"

        fun updateWidget(context: Context) {
            val intent = Intent(context, CallScreenerWidgetProvider::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, CallScreenerWidgetProvider::class.java))
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            context.sendBroadcast(intent)
        }
    }
}