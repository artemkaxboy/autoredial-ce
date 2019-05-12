package com.artemkaxboy.android.autoredialce.calls;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.CallLog.Calls;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import com.artemkaxboy.android.autoredialce.utils.Logger;

public class TaskGetCallInfo extends AsyncTask<String, Void, CallInfo> {

  private static final String TAG = "TaskGetCallInfo";
  private static final int retries = 20;
  private static final int sleep = 250;

  private Context context;

  public TaskGetCallInfo(Context context) {
    this.context = context;
  }

  @Override
  protected CallInfo doInBackground(String... params) {
    try {
      Thread.sleep(500);
    } catch (Exception e) {
      Logger.INSTANCE.warning(() -> "Couldn't sleep.", e);
    }
    CallInfo ci = new CallInfo();
    if (params.length > 0) {
      ci.setNumber(params[0]);
      ci.setDate(Long.valueOf(params[1]));
      for (int i = 0; i < retries; i++) {
        ci = getLastCall(ci);
        if (ci.getDuration() >= 0) {
          break;
        }
        try {
          Thread.sleep(sleep);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    } else {
      ci = getLastCall();
    }
    return ci;
  }

  private Cursor query(String where) {
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALL_LOG)
        != PackageManager.PERMISSION_GRANTED) {
      Log.e(TAG, "cant read call log!!!");
      Toast.makeText(context, "Can't read log!", Toast.LENGTH_LONG).show(); //TODO string
      return null;
    }
    return context.getContentResolver().query(
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
    Cursor callCursor = query(where);
    if (callCursor != null) {
      Log.i(TAG, "Cursor size: " + callCursor.getCount());
      if (callCursor.moveToFirst()) {
        long date = callCursor.getLong(callCursor.getColumnIndex(Calls.DATE));
        if (ci.getDate() <= (date + 5000)) {
          ci.setDate(date);
          ci.setType(callCursor.getInt(callCursor.getColumnIndex(Calls.TYPE)));
          ci.setDuration(callCursor.getLong(callCursor.getColumnIndex(Calls.DURATION)));
        }
      }
      callCursor.close();
    }
    return ci;
  }
}
