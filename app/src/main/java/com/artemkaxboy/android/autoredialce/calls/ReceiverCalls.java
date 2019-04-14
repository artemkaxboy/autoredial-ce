package com.artemkaxboy.android.autoredialce.calls;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.artemkaxboy.android.autoredialce.BuildConfig;
import com.artemkaxboy.android.autoredialce.P;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Set;

public abstract class ReceiverCalls extends BroadcastReceiver {

  private static final String TAG = "ReceiverCalls";

  public static final String CALL_DIRECTION = "__callDirection";
  public static final String CALL_DIRECTION_INBOUND = "__callDirectionIn";
  public static final String CALL_DIRECTION_OUTBOUND = "__callDirectionOut";
  private static final String PREV_CALL_STATE = "__prevCallState";

  protected static final String LAST_NUMBER = "__lastNumber";
  protected static final String LAST_TIME = "__lastTime";

  protected Context context;
  protected Intent intent;
  protected String number;
  protected String time;

  public Context getContext() {
    return context;
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    if (BuildConfig.DEBUG) {
      SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
      Log.i(TAG, "********Received**********" + df.format(System.currentTimeMillis()));
      Log.i(TAG, "Action: " + intent.getAction());
      Log.i(TAG, "Data String: " + intent.getData());
      Log.i(TAG, "*****Extras*****");
      Bundle b = intent.getExtras();
      if (b != null) {
        Set<String> ks = b.keySet();
        for (String k : ks) {
          Object kk = b.get(k);
          if (kk != null) {
            Log.i(TAG, k + ": " + kk.toString());
          }
        }
      }
    }

    this.context = context;
    this.intent = intent;
    if (intent == null || intent.getAction() == null) {
      return;
    }

    if (intent.getAction().equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
      String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

      if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
        String callDirection = P.getP(this.context, CALL_DIRECTION, CALL_DIRECTION_INBOUND);
        String prevCallState = P
            .getP(this.context, PREV_CALL_STATE, TelephonyManager.EXTRA_STATE_OFFHOOK);

        if (CALL_DIRECTION_INBOUND.equals(callDirection)
            && TelephonyManager.EXTRA_STATE_RINGING.equals(prevCallState)) {
          //-----Missed/Dropped calls
          idleMissedOrDropped();
        } else if (CALL_DIRECTION_INBOUND.equals(callDirection)
            && TelephonyManager.EXTRA_STATE_OFFHOOK.equals(prevCallState)) {
          //----Dropped or answered
          idleDroppedOrAnswered();
        } else if (CALL_DIRECTION_OUTBOUND.equals(callDirection)) {
          //-----Outbound
          number = P.getP(this.context, LAST_NUMBER, null);
          time = P.getP(this.context, LAST_TIME, "0");
          idleOutbound();
        }
      } else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
        P.putP(this.context, PREV_CALL_STATE, state);
        P.putP(this.context, CALL_DIRECTION, CALL_DIRECTION_INBOUND);
        P.putP(this.context, LAST_NUMBER,
            intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER));
        P.putP(this.context, LAST_TIME, System.currentTimeMillis());
        ringing();
      } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
        P.putP(this.context, PREV_CALL_STATE, state);
        offhook();
        //P.putP( context, LAST_TIME, System.currentTimeMillis());
      }
    } else if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
      String number = getResultData();
      if (number == null) {
        number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
      }
      P.putP(this.context, LAST_NUMBER, number);
      P.putP(this.context, CALL_DIRECTION, CALL_DIRECTION_OUTBOUND);
      P.putP(this.context, LAST_TIME, System.currentTimeMillis());
      P.putP(this.context, "subs", intent.getIntExtra("subscription", -1));
      this.number = P.getP(this.context, LAST_NUMBER, null);
      outbound();
    }
  }

  /**
   * The last call was missed or rejected and current state is IDLE.
   */
  public abstract void idleMissedOrDropped();

  /**
   * The last call was rejected or answered and current state is IDLE.
   */
  public abstract void idleDroppedOrAnswered();

  /**
   * The last call was outgoing and current state is IDLE.
   */
  public abstract void idleOutbound();

  /**
   * Current state is RINGING.
   */
  public abstract void ringing();

  /**
   * Current state is OFFHOOK.
   */
  public abstract void offhook();

  /**
   * New outgoing call is in progress.
   */
  public abstract void outbound();
}
