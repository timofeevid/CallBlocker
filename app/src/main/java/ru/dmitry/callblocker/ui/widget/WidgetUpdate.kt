package ru.dmitry.callblocker.ui.widget

import android.content.Context

object WidgetUpdate {

    fun updateWidgets(context: Context) {
        CallScreenerWidgetProvider.updateWidget(context)
        ToggleWidgetProvider.updateWidget(context)
    }
}