package com.artemkaxboy.android.autoredialce;

import static com.artemkaxboy.android.autoredialce.WidgetLast.FROM_WIDGET;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.artemkaxboy.android.autoredialce.calls.CallInfo;
import com.artemkaxboy.android.autoredialce.calls.TaskGetCallInfo;
import com.artemkaxboy.android.autoredialce.utils.Logger;
import com.artemkaxboy.android.autoredialce.utils.SettingsHelper;

public class ReceiverCommand extends BroadcastReceiver {

  public static final String ACTION_REDIALING_START = "ACTION_REDIALING_START";
  public static final String ACTION_REDIALING_STOP = "ACTION_REDIALING_STOP";
  public static final String ACTION_REDIALING_CALL_NOW = "ACTION_REDIALING_CALL_NOW";
  public static final String ACTION_SERVICE_SWITCH = "ACTION_SERVICE_SWITCH";
  public static final String ACTION_UPDATE_STATUS = "ACTION_UPDATE_STATUS";


  @Override
  public void onReceive(final Context context, Intent intent) {
    if (intent == null) {
      return;
    }
    String action = intent.getAction();
    switch (action) {
      case ACTION_REDIALING_START:
        if (intent.getBooleanExtra(FROM_WIDGET, false)) {
          Redialing.INSTANCE.setWidgetRedialing(context, true);
          SettingsHelper.INSTANCE.setBoolean(context, SettingsHelper.SERVICES_ENABLED_BACKUP,
              SettingsHelper.INSTANCE.getBoolean(context, SettingsHelper.SERVICES_ENABLED));
          SettingsHelper.INSTANCE.setBoolean(context, SettingsHelper.SERVICES_ENABLED, true);
          Redialing.INSTANCE.backupEnabled(context);
          Redialing.INSTANCE.setEnabled(context, true);
        }
        new TaskGetCallInfo(context) {
          @Override
          protected void onPostExecute(CallInfo callInfo) {
            try {
              Redialing.INSTANCE.start(context, callInfo.getNumber());
              Redialing.INSTANCE.nextCall(context);
            } catch (IllegalArgumentException e) {
              Logger.INSTANCE.info(() -> "Wrong number: " + e.getMessage());
            }
          }
        }.execute();
        break;
      case ACTION_REDIALING_STOP:
        context.sendBroadcast(new Intent(ActivityDialog.ACTION_DIALOG_CLOSE));
        context.stopService(new Intent(context, ServiceWait.class));
        Redialing.INSTANCE.setIgnoreLast(context, true);
        Redialing.INSTANCE.stop(context);
        break;
      case ACTION_REDIALING_CALL_NOW:
        context.sendBroadcast(new Intent(ActivityDialog.ACTION_DIALOG_CLOSE));
        context.stopService(new Intent(context, ServiceWait.class));
        Redialing.INSTANCE.nextCall(context);
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
