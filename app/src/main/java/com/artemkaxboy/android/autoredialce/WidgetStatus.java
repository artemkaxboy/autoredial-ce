package com.artemkaxboy.android.autoredialce;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class WidgetStatus extends AppWidgetProvider {
	public static final String WIDGET_IDS_KEY = "autoredialWidgetStatus";
	public static final String ACTION_APPWIDGET_UPDATE = "com.artemkaxboy.android.APPWIDGET_UPDATE";
	@Override
	public void onReceive( Context context, Intent intent ) {
		super.onReceive( context, intent );
		if( intent.hasExtra( WIDGET_IDS_KEY )) {
			int[] ids = intent.getExtras().getIntArray( WIDGET_IDS_KEY );
			onUpdate( context, AppWidgetManager.getInstance( context ), ids );
		}
	}
	
	@Override
	public void onUpdate( Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds ) {
		super.onUpdate( context, appWidgetManager, appWidgetIds );
		RemoteViews remoteViews;// = new RemoteViews( context.getPackageName(), R.layout.widget_on );
		Intent intent = new Intent( ReceiverCommand.ACTION_SERVICE_SWITCH );
		int requestCode;
		
		if( P.enabled( context )) {
			remoteViews = new RemoteViews( context.getPackageName(), R.layout.widget_on );
			intent.putExtra( "ex_data", false );
			requestCode = 11;
		} else {
			remoteViews = new RemoteViews( context.getPackageName(), R.layout.widget_off );
			intent.putExtra( "ex_data", true );
			requestCode = 12;
		}
		
		PendingIntent pendingIntent = PendingIntent.getBroadcast( context, requestCode, intent, 0 );
		remoteViews.setOnClickPendingIntent( R.id.widgetText, pendingIntent );
		appWidgetManager.updateAppWidget( appWidgetIds, remoteViews );
	}
}
