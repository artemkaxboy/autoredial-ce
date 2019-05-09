package com.artemkaxboy.android.autoredialce;

import static com.artemkaxboy.android.autoredialce.WidgetLast.FROM_WIDGET;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.artemkaxboy.android.autoredialce.calls.CallInfo;
import com.artemkaxboy.android.autoredialce.calls.TaskGetCallInfo;
import com.artemkaxboy.android.autoredialce.utils.SettingsHelper;

public class ReceiverCommand extends BroadcastReceiver {

  public static final String ACTION_REDIALING_START = "ACTION_REDIALING_START";
  public static final String ACTION_REDIALING_STOP = "ACTION_REDIALING_STOP";
  public static final String ACTION_REDIALING_CALL_NOW = "ACTION_REDIALING_CALL_NOW";
  public static final String ACTION_SERVICE_SWITCH = "ACTION_SERVICE_SWITCH";
  public static final String ACTION_UPDATE_STATUS = "ACTION_UPDATE_STATUS";
  public static final String EXTRA_SIM_ID = "SIM_ID";


  @Override
  public void onReceive(final Context context, Intent intent) {
    if (intent == null) {
      return;
    }
    String action = intent.getAction();
    switch (action) {
      case ACTION_REDIALING_START:
        if (intent.getBooleanExtra(FROM_WIDGET, false)) {
          P.widgetRedialing(context, true);
          P.enabledBackup(context, P.enabled(context));
          P.autoredialBackup(context, P.autoRedialOn(context));
          P.enabled(context, true);
          P.autoRedialOn(context, true);
          //Toast.makeText(context, "from_widget", Toast.LENGTH_LONG).show();
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
        boolean enabled = intent.getBooleanExtra(SettingsHelper.SERVICES_ENABLED, true);
        SettingsHelper.INSTANCE.setBoolean(context, SettingsHelper.SERVICES_ENABLED, enabled);
        sendMessageToWidget(context);
        break;
      case ACTION_UPDATE_STATUS:
        sendMessageToWidget(context);
        break;
      default:
    }
  }

  private void sendMessageToWidget(Context context) {
    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
    int[] widgetIds = appWidgetManager
        .getAppWidgetIds(new ComponentName(context, WidgetStatus.class));

    context.sendBroadcast(
        WidgetStatus.Companion.getIntent(context, WidgetStatus.ACTION_APPWIDGET_UPDATE)
            .putExtra(WidgetStatus.WIDGET_IDS_KEY, widgetIds));
  }

  public static Intent getIntent(Context context, String action) {
    return new Intent(action).setClass(context, ReceiverCommand.class);
  }
}
