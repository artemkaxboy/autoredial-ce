package com.artemkaxboy.android.autoredialce;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class WidgetLast extends AppWidgetProvider {
//	public static final String WIDGET_IDS_KEY = "autoredialWidgetStatus";
	public static final String FROM_WIDGET = "from_widget";

	@Override
	public void onUpdate( Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds ) {
		super.onUpdate( context, appWidgetManager, appWidgetIds );
		RemoteViews remoteViews = new RemoteViews( context.getPackageName(), R.layout.widget_last );
		Intent intent = new Intent( ReceiverCommand.ACTION_REDIALING_START ).putExtra( FROM_WIDGET, true );
		PendingIntent pendingIntent = PendingIntent.getBroadcast( context, 42, intent, 0 );
		remoteViews.setOnClickPendingIntent( R.id.widgetImage, pendingIntent );
		appWidgetManager.updateAppWidget( appWidgetIds, remoteViews );
	}
}
