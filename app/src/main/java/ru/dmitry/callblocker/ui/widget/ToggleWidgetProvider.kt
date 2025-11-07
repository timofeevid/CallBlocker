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
import ru.dmitry.callblocker.domain.usecase.AppConfigurationInteractor
import javax.inject.Inject

@AndroidEntryPoint
class ToggleWidgetProvider : AppWidgetProvider() {

    @Inject
    lateinit var appConfigurationInteractor: AppConfigurationInteractor

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
                updateWidget(context)
            }
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_toggle)

        views.setTextViewText(
            R.id.widget_toggle,
            if (shouldBlockUnknownNumbers) context.getString(R.string.widget_blocking_on) else context.getString(R.string.widget_blocking_off)
        )
        
        if (shouldBlockUnknownNumbers) {
            views.setInt(R.id.widget_toggle, "setBackgroundResource", R.drawable.widget_button_background_enabled)
        } else {
            views.setInt(R.id.widget_toggle, "setBackgroundResource", R.drawable.widget_button_background_disabled)
        }

        val toggleIntent = Intent(context, ToggleWidgetProvider::class.java).apply {
            action = ACTION_TOGGLE_BLOCK
            putExtra(EXTRA_BLOCK_STATUS, shouldBlockUnknownNumbers)
        }
        val togglePendingIntent = PendingIntent.getBroadcast(
            context,
            appWidgetId,
            toggleIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_toggle, togglePendingIntent)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    companion object {
        private const val ACTION_TOGGLE_BLOCK = "ru.dmitry.callblocker.SIMPLE_TOGGLE_BLOCK"
        private const val EXTRA_BLOCK_STATUS = "extra_block_status"

        fun updateWidget(context: Context) {
            val intent = Intent(context, ToggleWidgetProvider::class.java)
            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val ids = AppWidgetManager.getInstance(context)
                .getAppWidgetIds(ComponentName(context, ToggleWidgetProvider::class.java))
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            context.sendBroadcast(intent)
        }
    }
}