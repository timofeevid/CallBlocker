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
import ru.dmitry.callblocker.data.CallHistoryRepository
import ru.dmitry.callblocker.data.ContactsRepository
import ru.dmitry.callblocker.domain.usecase.AppConfigurationInteractor
import ru.dmitry.callblocker.ui.MainActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class CallScreenerWidgetProvider : AppWidgetProvider() {

    @Inject
    lateinit var appConfigurationInteractor: AppConfigurationInteractor

    @Inject
    lateinit var callHistoryRepository: CallHistoryRepository

    private var shouldBlockUnknownNumbers: Boolean
        get() = appConfigurationInteractor.getConfiguration().isBlockUnknownNumberEnable
        set(value) {
            val currentConfig = appConfigurationInteractor.getConfiguration()
            val newConfig = currentConfig.copy(isBlockUnknownNumberEnable = value)
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
                shouldBlockUnknownNumbers = !blockStatus
                updateAllWidgets(context)
            }
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val calls = callHistoryRepository.getScreenedCalls()
        val blockedCalls = calls.filter { it.wasBlocked }.take(3)

        val views = RemoteViews(context.packageName, R.layout.widget_call_screener)

        // Set blocking status text and color
        views.setTextViewText(
            R.id.widget_block_toggle,
            if (shouldBlockUnknownNumbers) context.getString(R.string.widget_blocking_on) else context.getString(
                R.string.widget_blocking_off
            )
        )
        views.setTextColor(
            R.id.widget_block_toggle,
            if (shouldBlockUnknownNumbers) 0xFF4CAF50.toInt() else 0xFFFF5252.toInt() // Green when enabled, red when disabled
        )

        // Set toggle click listener
        val toggleIntent = Intent(context, CallScreenerWidgetProvider::class.java).apply {
            action = ACTION_TOGGLE_BLOCK
            putExtra(EXTRA_BLOCK_STATUS, shouldBlockUnknownNumbers)
        }
        val togglePendingIntent = PendingIntent.getBroadcast(
            context,
            appWidgetId,
            toggleIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_block_toggle, togglePendingIntent)

        // Set click listener for the whole widget to open the app
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            appWidgetId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

        // Clear previous views
        views.removeAllViews(R.id.widget_blocked_calls_container)

        // Add blocked calls
        if (blockedCalls.isNotEmpty()) {
            for (call in blockedCalls) {
                val itemView = RemoteViews(context.packageName, R.layout.widget_blocked_call_item)

                // Get contact name or show phone number
                val contactName = ContactsRepository(context).getContactName(call.phoneNumber)
                val displayName = contactName ?: call.phoneNumber

                // Format time as HH:mm
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
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