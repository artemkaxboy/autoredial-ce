package com.artemkaxboy.android.autoredialce.calls;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.CallLog.Calls;
import androidx.core.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;
import com.artemkaxboy.android.autoredialce.P;

public class TaskGetCallInfo extends AsyncTask<String, Void, CallInfo> {

  static final String TAG = "TaskGetCallInfo";
  final int RETRIES = 20;
  final int SLEEP = 250;
  final String[] SIM_ID = new String[]{"sim_id", "simid", "sub_id", "subscription_id"};

  Context mContext;

  public TaskGetCallInfo(Context context) {
    mContext = context;
  }

  @Override
  protected CallInfo doInBackground(String... params) {
    try {
      Thread.sleep(500);
    } catch (Exception ignore) {
    }
    CallInfo ci = new CallInfo();
    if (params.length > 0) {
      ci.setNumber(params[0]);
      ci.setDate(Long.valueOf(params[1]));
      for (int i = 0; i < RETRIES; i++) {
        ci = getLastCall(ci);
        if (ci.getDuration() >= 0) {
          break;
        }
        try {
          Thread.sleep(SLEEP);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    } else {
      ci = getLastCall();
    }
    return ci;
  }

  Cursor query(String where) {
    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALL_LOG)
        != PackageManager.PERMISSION_GRANTED) {
      Log.e(TAG, "cant read call log!!!");
      Toast.makeText(mContext, "Can't read log!", Toast.LENGTH_LONG).show(); //TODO string
      return null;
    }
    return mContext.getContentResolver().query(
        Calls.CONTENT_URI,
        null,
        where,
        null,
        Calls.DATE + " DESC LIMIT 1");
  }

  private CallInfo getLastCall() {
    CallInfo callInfo = new CallInfo();
    Cursor callCursor = query(null);
    if (callCursor != null) {
      if (callCursor.moveToFirst()) {
        callInfo.setDate(callCursor.getLong(callCursor.getColumnIndex(Calls.DATE)));
        callInfo.setNumber(
            callCursor.getString(callCursor.getColumnIndex(Calls.NUMBER)));
        callInfo.setDuration(
            callCursor.getLong(callCursor.getColumnIndex(Calls.DURATION)));
        callInfo.setSimId(getSimId(callCursor));
      }
      callCursor.close();
    }
    return callInfo;
  }

  private CallInfo getLastCall(CallInfo ci) {
    String where = Calls.NUMBER + " LIKE '" + ci.getNumber() + "'";
    if (ci.getNumber() == null || ci.getNumber().length() == 0) {
      where = Calls.NUMBER + " IS NULL";
    }
    Cursor mCallCursor = query(where);
    if (mCallCursor != null) {
      Log.i(TAG, "Cursor size: " + mCallCursor.getCount());
      if (mCallCursor.moveToFirst()) {
        long date = mCallCursor.getLong(mCallCursor.getColumnIndex(Calls.DATE));
        if (ci.getDate() <= (date + 5000)) {
          ci.setDate(date);
          ci.setType(mCallCursor.getInt(mCallCursor.getColumnIndex(Calls.TYPE)));
          ci.setDuration(mCallCursor.getLong(mCallCursor.getColumnIndex(Calls.DURATION)));
          ci.setSimId(getSimId(mCallCursor));
        }
      }
      mCallCursor.close();
    }
    return ci;
  }

  private int getSimId(Cursor c) {
    int simId = -1;
    boolean brek = false;
    for (int i = 0; i < c.getColumnCount(); i++) {
      Log.w(TAG, c.getColumnName(i) + ": " + c.getString(i));
      for (String aSIM_ID : SIM_ID) {
        if (c.getColumnName(i).toLowerCase().equals(aSIM_ID)) {
          try {
            simId = c.getInt(i);
            brek = true;
            break;
          } catch (Exception ignore) {
          }
        }
      }
      if (brek) {
        break;
      }
    }
    if (simId < 0) {
      simId = P.getP(mContext, "subs", -1);
    }
    return simId;
  }
}
