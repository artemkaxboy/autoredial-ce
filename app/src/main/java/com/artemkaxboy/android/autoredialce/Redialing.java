package com.artemkaxboy.android.autoredialce;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.widget.RemoteViews;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import com.artemkaxboy.android.autoredialce.contacts.MyPhone;
import com.artemkaxboy.android.autoredialce.dsim.DSimCommand;
import com.artemkaxboy.android.autoredialce.utils.SettingsHelper;

public class Redialing {

  public static final int NOTIFICATION_MAIN = 1;

  public static void query(Context context, String number, int simId) {
    if (P.redialWoutPrompt(context)) {
      Redialing.start(context, number, simId);
      Redialing.nextCall(context);
      return;
    }
    Intent intent = new Intent(context, ActivityDialog.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.putExtra(ActivityDialog.TYPE, ActivityDialog.TYPE_QUERY);
    intent.putExtra("number", number);
    intent.putExtra(ReceiverCommand.EXTRA_SIM_ID, simId);
    intent.putExtra(CallLog.Calls.TYPE, CallLog.Calls.OUTGOING_TYPE);
    context.startActivity(intent);
  }

  public static void start(Context context, String number, int simId) {
    SettingsHelper.INSTANCE.setBoolean(context, SettingsHelper.REDIALING, true);
    P.currentAttempt(context, 0);
    P.number(context, number);
    Settings.putSimId(context, simId);
    notificationCreate(context);
  }

  public static void notificationCreate(Context context) {
    Intent status = new Intent(context, ActivityDialog.class)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        .putExtra(ActivityDialog.TYPE, ActivityDialog.TYPE_STATUS);
    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, status, 0);
    PendingIntent stop = PendingIntent.getBroadcast(context, 0,
        new Intent(ReceiverCommand.ACTION_REDIALING_STOP), 0);
    PendingIntent next = PendingIntent.getBroadcast(context, 0,
        new Intent(ReceiverCommand.ACTION_REDIALING_CALL_NOW), 0);

    NotificationCompat.Builder builder = (NotificationCompat.Builder) new NotificationCompat
        .Builder(context)
        .setSmallIcon(R.drawable.notify)
        .setTicker(context.getString(R.string.redialing))
        .setContentIntent(contentIntent)
        .setWhen(System.currentTimeMillis())
        .setOngoing(true);

    if (Build.VERSION.SDK_INT < 16) {
      RemoteViews contentView = new RemoteViews(context.getPackageName(),
          R.layout.notification_layout);
      contentView.setOnClickPendingIntent(R.id.btn_stop, stop);
      contentView.setOnClickPendingIntent(R.id.btn_next, next);
      contentView.setTextViewText(R.id.try_count, context.getString(R.string.attemptString,
          P.currentAttempt(context) + 1,
          P.lastAttempt(context)));
      builder.setContent(contentView);
    } else {
      builder
          .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
              R.mipmap.ic_launcher))
          .setContentTitle(context.getString(R.string.redialing))
          .setContentText(context.getString(R.string.attemptString,
              P.currentAttempt(context) + 1,
              P.lastAttempt(context)))
          .addAction(R.drawable.ic_stop,
              context.getString(R.string.finish_button_text), stop)
          .addAction(R.drawable.ic_next,
              context.getString(R.string.next_button_text), next);
    }

    NotificationManager nm = (NotificationManager) context
        .getSystemService(Context.NOTIFICATION_SERVICE);
    nm.notify(NOTIFICATION_MAIN, builder.build());
  }

  public static void stop(Context context) {
    P.masterCall(context, false);
    SettingsHelper.INSTANCE.setBoolean(context, SettingsHelper.REDIALING, false);
    P.ignoreLast(context, false);
    if (P.widgetRedialing(context)) {
      P.autoRedialOn(context, P.autoredialBackup(context));
      P.enabled(context, P.enabledBackup(context));
      P.widgetRedialing(context, false);
    }
    NotificationManager nm = (NotificationManager) context
        .getSystemService(Context.NOTIFICATION_SERVICE);
    nm.cancel(NOTIFICATION_MAIN);
  }

  public static void waitNext(Context context) {
    int pause = P.pause(context);
    if (pause == 0) {
      nextCall(context);
    } else {
      Intent service = new Intent(context, ServiceWait.class);
      context.startService(service);
    }
  }

  public static void nextCall(Context context) {
    P.currentAttempt(context, P.currentAttempt(context) + 1);
    call(context);
  }

  public static void call(Context context) {
    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    if (tm.getCallState() == TelephonyManager.CALL_STATE_IDLE) {
      P.masterCall(context, true);
      P.confirmIsGot(context, true);
      String num = P.number(context).replaceAll("#", Uri.encode("#"));
      Intent call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + num));
      DSimCommand.powerIntent(call, Settings.getSimId(context));
      call.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
          == PackageManager.PERMISSION_GRANTED) {
        context.startActivity(call);
      } else {
        Toast.makeText(context, "Cant call 2! Denied!", Toast.LENGTH_LONG).show();
      }
    } else {
      P.putP(context, "__waiting", true);
    }
  }

  public static void endCall(Context context) {
    P.masterCall(context, false);
  }

  public static boolean keepOn(Context context) {
    if (P.currentAttempt(context) == P.lastAttempt(context)) {
      return false;
    }
    notificationCreate(context);
    return true;
  }


  @SuppressWarnings("unused")
  public static boolean checkDeferred(Context context) {
    if (P.getP(context, "__waiting", false)) {
      return false;
    } else {
      P.putP(context, "__waiting", false);
      call(context);
    }
    return true;
  }

  @SuppressWarnings("unused")
  public static void clearDeferred(Context context, String number) {
    if (MyPhone.compare(P.getP(context, "__waiting", null), number)) {
      P.putP(context, "__waiting", null);
    }
  }
}
