package com.artemkaxboy.android.autoredialce;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.CallLog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.artemkaxboy.android.autoredialce.contacts.MyContact;
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
  private String number;
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

    switch (type) {
      case TYPE_QUERY:
        assert intent != null;
        query(intent.getExtras());
        break;
      case TYPE_STATUS:
        status();
        break;
      default:
        finish();
    }
  }

  View view;

  void query(Bundle params) {
    number = params.getString("number");
    String name = MyContact.getNameByNumber(this, number);

    view = View.inflate(this, R.layout.misseddialog, null);
    ((TextView) (view.findViewById(R.id.MDnumber))).setText(number);
    ((TextView) (view.findViewById(R.id.MDname))).setText(name);

    timeoutDialog = new TimeoutDialog(context, 10);

    Log.v("A##", number);

    int callType = params.getInt(CallLog.Calls.TYPE);
    switch (callType) {
      case CallLog.Calls.OUTGOING_TYPE:
        String title = context.getString(R.string.redialLast);
        timeoutDialog.setTitle(title);
        timeoutDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.yes),
            (dialog, which) -> {
              Redialing.INSTANCE.start(context, number);
              Redialing.INSTANCE.nextCall(context);
              timeoutDialog.cncl();
            });
        break;
      case CallLog.Calls.MISSED_TYPE:
      default:
        title = context.getString(R.string.redialRejected);
        timeoutDialog.setTitle(title);
        //timeoutDialog.setTitle( R.string.redialRejected );
        timeoutDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.yes),
            (dialog, which) -> {
              Recall.call(context, number);
              timeoutDialog.cncl();
            });
        break;
    }
    timeoutDialog.setView(view);
    timeoutDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.no),
        (dialog, arg1) -> timeoutDialog.cncl());
    timeoutDialog.setCancelable(true);
    timeoutDialog.setOnCancelListener(dialog -> finish());
    timeoutDialog.show();
  }

  void status() {
    number = Redialing.INSTANCE.getRedialingNumber(context);
    String name = MyContact.getNameByNumber(this, number, number);

    view = View.inflate(this, R.layout.statusdialog, null);

    String attempts = context.getString(R.string.attemptString,
        Redialing.INSTANCE.getCurrentAttempt(context) + 1,
        Redialing.INSTANCE.getAttemptsCount(context));
    //String attempts = String.format( Locale.getDefault(), getString( R.string.attemptString ),
    //    P.currentAttempt( context ) + 1, P.lastAttempt( context ));

    ((TextView) (view.findViewById(R.id.number))).setText(name);
    ((TextView) (view.findViewById(R.id.attempts))).setText(attempts);
    (timeView = ((TextView) (view.findViewById(R.id.time)))).setText("");

    //TimeoutDialog dialog = new TimeoutDialog( context, 10 );
    alertDialog = new AlertDialog.Builder(context).create();

    alertDialog.setTitle(R.string.redialing);
    alertDialog.setView(view);
    alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.now),
        (dialog, which) -> {
          sendBroadcast(
              ReceiverCommand.getIntent(context, ReceiverCommand.ACTION_REDIALING_CALL_NOW));
          dialog.cancel();
        });
    alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.hide),
        (dialog, arg1) -> dialog.cancel());

    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel),
        (dialog, which) -> {
          sendBroadcast(ReceiverCommand.getIntent(context, ReceiverCommand.ACTION_REDIALING_STOP));
          dialog.cancel();
        });
    alertDialog.setCancelable(true);
    alertDialog.setOnCancelListener(dialog -> finish());
    alertDialog.show();

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
