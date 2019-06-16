package com.artemkaxboy.android.autoredialce;

import static com.artemkaxboy.android.autoredialce.utils.ConsKt.TAG;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.provider.CallLog;
import android.util.Log;
import android.widget.Toast;

import com.artemkaxboy.android.autoredialce.calls.CallInfo;
import com.artemkaxboy.android.autoredialce.calls.TaskGetCallInfo;
import com.artemkaxboy.android.autoredialce.contacts.MyContact;
import com.artemkaxboy.android.autoredialce.contacts.MyPhone;
import com.artemkaxboy.android.autoredialce.utils.SettingsHelper;

public class ReceiverCalls extends com.artemkaxboy.android.autoredialce.calls.ReceiverCalls {

  @Override
  public void offhook() {
    if (Redialing.INSTANCE.isEnabled(getContext())) {
      return;
    }
    if (CALL_DIRECTION_OUTBOUND
        .equals(P.getP(getContext(), CALL_DIRECTION, CALL_DIRECTION_INBOUND))) {
      if ((P.speakerAlways(getContext()) || (P.speaker(getContext())
          && Redialing.INSTANCE.isMasterCall(getContext())))
          && !headsetOn()) {
        try {
          Thread.sleep(P.speakerTime(getContext()));
        } catch (Exception e) {
          if (BuildConfig.DEBUG) {
            Log.w(TAG, "Couldn't sleep before turn speaker on", e);
          }
        }
        ((AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE))
            .setSpeakerphoneOn(true);
      } else {
        ((AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE))
            .setSpeakerphoneOn(false);
      }
    }
  }

  @Override
  public void idleMissedOrDropped() {
    if (Redialing.INSTANCE.isEnabled(getContext())) {
      return;
    }
    if (P.missedEnabled(getContext()) || P.missedList(getContext())) {
      new TaskGetCallInfo(getContext()) {
        @Override
        protected void onPostExecute(CallInfo callInfo) {
          if (callInfo.getDuration() < 0) {
            return;
          }
          if (P.missedList(getContext())
              && callInfo.getType() != CallLog.Calls.MISSED_TYPE
              && DbHelper.isInRejected(getContext(), callInfo.getNumber()) > 0) {
            Recall.call(getContext(), callInfo.getNumber());
            return;
          }
          if (P.missedEnabled(getContext())) {
            Recall.query(getContext(), callInfo.getNumber());
          }
        }
      }.execute(P.getP(getContext(), LAST_NUMBER, null),
          P.getP(getContext(), LAST_TIME, "0"));
    }
  }

  @Override
  public void idleDroppedOrAnswered() {
    if (Redialing.INSTANCE.isEnabled(getContext())) {
      return;
    }
    if (P.missedEnabled(getContext()) || P.missedList(getContext())) {
      new TaskGetCallInfo(getContext()) {
        @Override
        protected void onPostExecute(CallInfo callInfo) {
          if (callInfo.getDuration() != 0) {
            return;
          }
          if (P.missedList(getContext())
              && DbHelper.isInRejected(getContext(), callInfo.getNumber()) > 0) {
            Recall.call(getContext(), callInfo.getNumber());
            return;
          }
          if (P.missedEnabled(getContext())) {
            Recall.query(getContext(), callInfo.getNumber());
          }
        }
      }.execute(P.getP(getContext(), LAST_NUMBER, null),
          P.getP(getContext(), LAST_TIME, "0"));
    }
  }

  @Override
  public void idleOutbound() {
    Log.w("A##", "pause:" + (System.currentTimeMillis() - P.lastIdle(getContext())));
    if (System.currentTimeMillis() - 1000 < P.lastIdle(getContext())) {
      if (BuildConfig.DEBUG) {
        Log.w("A##", "skip idle intent");
      }
      return;
    }
    P.lastIdle(getContext(), System.currentTimeMillis());

    if (!Redialing.INSTANCE.isEnabled(getContext())) {
      //P.enabled(getContext()); // todo check if needed. has no effect, a bug?
      return;
    }
    if (Redialing.INSTANCE.isIgnoreLast(getContext())) {
      Redialing.INSTANCE.setIgnoreLast(getContext(), false);
      return;
    }
    if (Redialing.INSTANCE.isMasterCall(getContext())) {
      if (!Redialing.INSTANCE.keepOn(getContext())) {
        Redialing.INSTANCE.stop(getContext());
        return;
      }
    } else {
      if (Redialing.INSTANCE.isRedialing(getContext())) {
        if (!MyPhone.compare(P.getP(getContext(), LAST_NUMBER, null),
            Redialing.INSTANCE.getRedialingNumber(getContext()))) {
          return;
        }
      }
    }
    new TaskGetCallInfo(getContext()) {
      @Override
      protected void onPostExecute(CallInfo callInfo) {
        long duration = callInfo.getDuration();
        if (duration > P.minDuration(getContext())) {
          if (Redialing.INSTANCE.isMasterCall(getContext()) && SettingsHelper.INSTANCE
              .getBoolean(getContext(), SettingsHelper.REDIALING)) {
            Redialing.INSTANCE.stop(getContext());
          }
        } else if (duration < 0) {
          Toast.makeText(getContext(), "AutoRedial: Error 1!", Toast.LENGTH_LONG).show();
        } else {
          long s = System.currentTimeMillis() - P.outTime(getContext()) - (duration * 1000);
          Log.v("A##", "" + s);

          if (SettingsHelper.INSTANCE.getBoolean(getContext(), SettingsHelper.REDIALING)) {
            if (Redialing.INSTANCE.isMasterCall(getContext())) {
              Redialing.INSTANCE.endCall(getContext());
              Redialing.INSTANCE.waitNext(getContext());
            } /* else {
                            if (Redialing.checkDeferred(getContext())) return;
                        }*/
          } else {
            Redialing.INSTANCE.query(getContext(), callInfo.getNumber());
          }
        }
      }
    }.execute(P.getP(getContext(), LAST_NUMBER, null),
        P.getP(getContext(), LAST_TIME, "0"));

  }

  @Override
  public void ringing() {

  }

  @Override
  public void outbound() {
    if (Redialing.INSTANCE.isEnabled(getContext())) {
      return;
    }

    String number = getResultData();
    if (number == null) {
      return;
    }

    switch (number) {
      case "*2433811":
        P.speakerAlways(getContext(), true);
        Toast.makeText(getContext(), "Speaker ENABLED", Toast.LENGTH_SHORT).show();
        Redialing.INSTANCE.setIgnoreLast(getContext(), true);
        setResultData(null);
        return;
      case "*2433810":
        P.speakerAlways(getContext(), false);
        Toast.makeText(getContext(), "Speaker DISABLED", Toast.LENGTH_SHORT).show();
        Redialing.INSTANCE.setIgnoreLast(getContext(), true);
        setResultData(null);
        return;
      default:
    }

    P.outTime(getContext(), System.currentTimeMillis());
    if (!P.confirmOut(getContext())
        || SettingsHelper.INSTANCE.getBoolean(getContext(), SettingsHelper.CONFIRMATION_GOT)
        || (P.confirmHeadset(getContext()) && headsetOn())) {
      SettingsHelper.INSTANCE.setBoolean(getContext(), SettingsHelper.CONFIRMATION_GOT, false);
      return;
    }

    char firstNum = number.charAt(0);
    char lastNum = number.charAt(number.length() - 1);
    if (P.confirmExceptUssd(getContext()) && ((firstNum == '*' || firstNum == '#')
        && lastNum == '#')) {
      return;
    }

    String name = MyContact.getNameByNumber(getContext(), number);
    if (P.confirmExceptUnknown(getContext())
        && getContext().getString(android.R.string.unknownName).equals(name)) {
      return;
    }
    setResultData(null);
    Intent i = new Intent(getContext(), ActivityConfirm.class)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
            | Intent.FLAG_ACTIVITY_NO_ANIMATION)
        .putExtra("number", number)
        .putExtra("name", name)
        .putExtra(CALL_DIRECTION, CALL_DIRECTION_OUTBOUND);
    getContext().startActivity(i);
  }


  private boolean headsetOn() {
    AudioManager am = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
    return am.isWiredHeadsetOn() | P.bluetoothConnected(getContext());
  }
}
