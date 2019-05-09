package com.artemkaxboy.android.autoredialce

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews

class WidgetLast : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val intent = ReceiverCommand.getIntent(context,
                ReceiverCommand.ACTION_REDIALING_START).putExtra(FROM_WIDGET, true)
        val pendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE, intent, 0)
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_last)
        remoteViews.setOnClickPendingIntent(R.id.widgetImage, pendingIntent)
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews)
    }

    companion object {
        const val FROM_WIDGET = "from_widget"
        const val REQUEST_CODE = 42
    }
}
