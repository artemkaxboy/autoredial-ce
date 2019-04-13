package com.artemkaxboy.android.autoredialce;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.artemkaxboy.android.autoredialce.calls.CallInfo;
import com.artemkaxboy.android.autoredialce.calls.TaskGetCallInfo;

public class ReceiverCommand extends BroadcastReceiver {
    public static final String ACTION_REDIALING_START = "ACTION_REDIALING_START",
            ACTION_REDIALING_STOP = "ACTION_REDIALING_STOP",
            ACTION_REDIALING_CALL_NOW = "ACTION_REDIALING_CALL_NOW",
            ACTION_SERVICE_SWITCH = "ACTION_SERVICE_SWITCH";
    public static final String EXTRA_SIM_ID = "SIM_ID";


    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent == null) return;
        String action = intent.getAction();
        switch (action) {
            case ACTION_REDIALING_START:
                if (intent.getBooleanExtra(WidgetLast.FROM_WIDGET, false)) {
                    P.widgetRedialing(context, true);
                    P.enabledBackup(context, P.enabled(context));
                    P.autoredialBackup(context, P.autoRedialOn(context));
                    P.enabled(context, true);
                    P.autoRedialOn(context, true);
//                Toast.makeText(context, "from_widget", Toast.LENGTH_LONG).show();
                }
                new TaskGetCallInfo(context) {
                    @Override
                    protected void onPostExecute(CallInfo callInfo) {
                        Redialing.start(context, callInfo.getNumber(), callInfo.getSimId());
                        Redialing.nextCall(context);
                    }
                }.execute();
                break;
            case ACTION_REDIALING_STOP:
                context.sendBroadcast(new Intent(ActivityDialog.ACTION_DIALOG_CLOSE));
                context.stopService(new Intent(context, ServiceWait.class));
                P.ignoreLast(context, true);
                Redialing.stop(context);
                break;
            case ACTION_REDIALING_CALL_NOW:
                context.sendBroadcast(new Intent(ActivityDialog.ACTION_DIALOG_CLOSE));
                context.stopService(new Intent(context, ServiceWait.class));
                Redialing.nextCall(context);
                break;
            case ACTION_SERVICE_SWITCH:
                boolean on = intent.getBooleanExtra("ex_data", true);
                P.enabled(context, on);
                sendMessageToWidget(context);
                break;
        }
	}
	
	private void sendMessageToWidget( Context context ) {
		AppWidgetManager mAppWidgetManager = AppWidgetManager.getInstance( context );
		int[] ids = mAppWidgetManager.getAppWidgetIds( new ComponentName( context, WidgetStatus.class ));
		Intent msg = new Intent( WidgetStatus.ACTION_APPWIDGET_UPDATE );
		msg.putExtra( WidgetStatus.WIDGET_IDS_KEY, ids );
		context.sendBroadcast( msg );
	}

}
