package com.artemkaxboy.android.autoredialce;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.CallLog;
import androidx.core.app.ActivityCompat;
import com.artemkaxboy.android.autoredialce.utils.PermissionHelper;

public class Recall {

  public static void query(Context context, String number) {
    Intent intent = new Intent(context, ActivityDialog.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.putExtra(ActivityDialog.TYPE, ActivityDialog.TYPE_QUERY);
    intent.putExtra("number", number);
    intent.putExtra(CallLog.Calls.TYPE, CallLog.Calls.MISSED_TYPE);
    context.startActivity(intent);
  }

  public static void call(Context context, String number) {
    P.confirmIsGot(context, true);

    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
        != PackageManager.PERMISSION_GRANTED) {
      PermissionHelper.INSTANCE.complain(context);
      return;
    }

    Intent call = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
    call.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(call);
  }
}
