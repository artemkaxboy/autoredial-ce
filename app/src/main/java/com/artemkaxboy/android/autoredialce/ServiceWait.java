package com.artemkaxboy.android.autoredialce;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;

public class ServiceWait extends Service {

  public static final String ACTION_TIME_REMAIN = "ACTION_TIME_REMAIN";
  public static final String REMAIN_SECONDS = "remain_seconds";

  @Override
  public IBinder onBind(Intent arg0) {
    return null;
  }

  Context context;
  CountDownTimer countDownTimer;

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    context = this;
    countDownTimer = new CountDownTimer(Redialing.INSTANCE.getPause(context) * 1000, 100L) {
      @Override
      public void onFinish() {
        context.sendBroadcast(new Intent(ACTION_TIME_REMAIN).putExtra(REMAIN_SECONDS, 0));
        call();
      }

      @Override
      public void onTick(long millisUntilFinished) {
        context.sendBroadcast(new Intent(ACTION_TIME_REMAIN)
            .putExtra(REMAIN_SECONDS, (int) (millisUntilFinished / 1000)));
      }
    }.start();
    if (P.status(context)) {
      Intent status = new Intent(context, ActivityDialog.class);
      status.putExtra(ActivityDialog.TYPE, ActivityDialog.TYPE_STATUS);
      status.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(status);
    }

    return START_STICKY;
  }

  /**
   * Starts new call through {@link Redialing}.
   */
  public void call() {
    if (countDownTimer != null) {
      countDownTimer.cancel();
      countDownTimer = null;
    }
    Redialing.INSTANCE.nextCall(context);
    stopSelf();
  }

  @Override
  public void onDestroy() {

    if (countDownTimer != null) {
      countDownTimer.cancel();
      countDownTimer = null;
    }
    stopSelf();
  }

}
