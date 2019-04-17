package com.artemkaxboy.android.autoredialce;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.artemkaxboy.android.autoredialce.contacts.MyContact;
import com.artemkaxboy.android.autoredialce.dialogs.TimeoutDialog;

import java.util.Locale;

public class ActivityDialog extends AppCompatActivity {

  public static final String ACTION_DIALOG_CLOSE = "ACTION_DIALOG_CLOSE";

  public static final String TYPE = "dialogType";
  public static final int TYPE_VOID = 0;
  public static final int TYPE_QUERY = 1;
  public static final int TYPE_STATUS = 2;


  Context context;
  int type;
  int simId;
  String number;
  TextView timeView;
  TimeoutDialog timeoutDialog;
  AlertDialog alertDialog;


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
    simId = params.getInt(ReceiverCommand.EXTRA_SIM_ID);
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
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                Redialing.start(context, number, simId);
                Redialing.nextCall(context);
                timeoutDialog.cncl();
              }
            });
        break;
      case CallLog.Calls.MISSED_TYPE:
      default:
        title = context.getString(R.string.redialRejected);
        timeoutDialog.setTitle(title);
        //timeoutDialog.setTitle( R.string.redialRejected );
        timeoutDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(android.R.string.yes),
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                Recall.call(context, number);
                timeoutDialog.cncl();
              }
            });
        break;
    }
    timeoutDialog.setView(view);
    timeoutDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.no),
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int arg1) {
            timeoutDialog.cncl();
          }
        });
    timeoutDialog.setCancelable(true);
    timeoutDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
      @Override
      public void onCancel(DialogInterface dialog) {
        finish();
      }
    });
    timeoutDialog.show();
  }

  void status() {
    number = P.number(context);
    String name = MyContact.getNameByNumber(this, number, number);

    view = View.inflate(this, R.layout.statusdialog, null);

    String attempts = context.getString(R.string.attemptString,
        P.currentAttempt(context) + 1,
        P.lastAttempt(context));
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
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            Intent i = new Intent();
            i.setAction(ReceiverCommand.ACTION_REDIALING_CALL_NOW);
            sendBroadcast(i);
            alertDialog.cancel();
          }
        });
    alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.hide),
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int arg1) {
            alertDialog.cancel();
          }
        });

    alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel),
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            Intent i = new Intent();
            i.setAction(ReceiverCommand.ACTION_REDIALING_STOP);
            sendBroadcast(i);
            dialog.cancel();
          }
        });
    alertDialog.setCancelable(true);
    alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
      @Override
      public void onCancel(DialogInterface dialog) {
        finish();
      }
    });
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
        int got = intent.getIntExtra(ServiceWait.REMAIN_SECONDS, P.pause(
            ActivityDialog.this.context));
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
