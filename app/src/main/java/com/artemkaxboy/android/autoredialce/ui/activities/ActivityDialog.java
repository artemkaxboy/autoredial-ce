package com.artemkaxboy.android.autoredialce.ui.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.artemkaxboy.android.autoredialce.R;
import com.artemkaxboy.android.autoredialce.Redialing;
import com.artemkaxboy.android.autoredialce.ServiceWait;
import com.artemkaxboy.android.autoredialce.dialogs.TimeoutDialog;
import java.util.Locale;

public class ActivityDialog extends AppCompatActivity {

  public static final String ACTION_DIALOG_CLOSE = "ACTION_DIALOG_CLOSE";

  public static final String TYPE = "dialogType";
  public static final int TYPE_VOID = 0;
  public static final int TYPE_QUERY = 1;
  public static final int TYPE_STATUS = 2;

  private Context context;
  private int type;
  private TextView timeView;
  private TimeoutDialog timeoutDialog;
  private AlertDialog alertDialog;

  Context getContext() {
    return context;
  }

  void setContext(Context context) {
    this.context = context;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setTheme(R.style.AppTheme_Transparent);
    setContentView(R.layout.dialog);
    context = this;
    Intent intent = getIntent();
    if (intent == null || (type = intent.getIntExtra(TYPE, TYPE_VOID)) == TYPE_VOID) {
      finish();
    }

    if (type == TYPE_QUERY) {
      assert intent != null;
      query(intent.getExtras());
    } else {
      finish();
    }
  }

  View view;

  void query(Bundle params) {

    view = View.inflate(this, R.layout.misseddialog, null);

    timeoutDialog = new TimeoutDialog(context, 10);

    String title = "Stop calling";
    timeoutDialog.setTitle(title);
    timeoutDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.yes),
        (dialog, which) -> {
          Redialing.INSTANCE.stop(context);
          timeoutDialog.cncl();
        });
    timeoutDialog.setView(view);
    timeoutDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.no),
        (dialog, arg1) -> timeoutDialog.cncl());
    timeoutDialog.setCancelable(true);
    timeoutDialog.setOnCancelListener(dialog -> finish());
    timeoutDialog.show();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (alertDialog != null && alertDialog.isShowing()) {
      alertDialog.cancel();
    }
    if (timeoutDialog != null && timeoutDialog.isShowing()) {
      timeoutDialog.cncl();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    IntentFilter filter = new IntentFilter();
    filter.addAction(ServiceWait.ACTION_TIME_REMAIN);
    filter.addAction(ACTION_DIALOG_CLOSE);
    registerReceiver(broadcastReceiver, filter);
    final Window window = getWindow();
    window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
    window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
  }

  @Override
  protected void onPause() {
    super.onPause();
    try {
      unregisterReceiver(broadcastReceiver);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      String action;//intent.getAction()
      if (intent == null || (action = intent.getAction()) == null) {
        return;
      }
      if (action.equals(ServiceWait.ACTION_TIME_REMAIN)) {
        int got = intent.getIntExtra(ServiceWait.REMAIN_SECONDS,
            Redialing.INSTANCE.getPause(getContext()));
        if (timeView != null) {
          if (got == 0) {
            finish();
          } else {
            String remain = String.format(Locale.getDefault(), "%02d:%02d", got / 60, got % 60);
            timeView.setText(remain);
          }
        }
      } else if (action.equals(ACTION_DIALOG_CLOSE)) {
        finish();
      }
    }
  };
}
