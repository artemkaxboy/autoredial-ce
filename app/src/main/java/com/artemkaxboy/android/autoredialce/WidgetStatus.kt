package com.artemkaxboy.android.autoredialce

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.artemkaxboy.android.autoredialce.utils.SettingsHelper

class WidgetStatus : AppWidgetProvider() {

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        intent.extras?.getIntArray(WIDGET_IDS_KEY)?.let {
            onUpdate(context, AppWidgetManager.getInstance(context), it)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val remoteViews: RemoteViews
        val intent = ReceiverCommand.getIntent(context, ReceiverCommand.ACTION_SERVICE_SWITCH)
        val requestCode: Int

        val enabled = SettingsHelper.readBoolean(context, SettingsHelper.AUTOREDIAL_ENABLED)
        intent.putExtra(SettingsHelper.AUTOREDIAL_ENABLED, !enabled)

        if (enabled) {
            remoteViews = RemoteViews(context.packageName, R.layout.widget_on)
            requestCode = REQUEST_CODE_ON
        } else {
            remoteViews = RemoteViews(context.packageName, R.layout.widget_off)
            requestCode = REQUEST_CODE_OFF
        }

        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        remoteViews.setOnClickPendingIntent(R.id.widget_text, pendingIntent)
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews)
    }

    companion object {
        const val WIDGET_IDS_KEY = "autoredialWidgetStatus"
        const val ACTION_APPWIDGET_UPDATE = "com.artemkaxboy.android.APPWIDGET_UPDATE"

        private const val REQUEST_CODE_ON = 11
        private const val REQUEST_CODE_OFF = 12

        fun getIntent(context: Context, action: String): Intent {
            return Intent(action).setClass(context, WidgetStatus::class.java)
        }
    }
}
