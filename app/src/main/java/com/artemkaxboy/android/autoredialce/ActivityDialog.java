package com.artemkaxboy.android.autoredialce;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.CallLog;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
	
	
	Context mContext;
	int mType, mSimId;
	String mNumber;
	TextView mTimeView;
	TimeoutDialog tDialog;
	AlertDialog aDialog;
	
	
	@Override
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate(savedInstanceState);
        setTheme( R.style.AppTheme_Transparent );
		setContentView( R.layout.dialog );
		mContext = this;
		Intent intent = getIntent();
		if( intent == null || ( mType = intent.getIntExtra( TYPE, TYPE_VOID )) == TYPE_VOID ) finish();
		
		switch( mType ) {
		case TYPE_QUERY:
            assert intent != null;
            query( intent.getExtras());
			break;
		case TYPE_STATUS:
			status();
			break;
		default:
			finish();
		}
	}
	
	View mView;
	void query( Bundle params ) {
		mNumber = params.getString( "number" );
		int callType = params.getInt( CallLog.Calls.TYPE );
        mSimId = params.getInt( ReceiverCommand.EXTRA_SIM_ID );
		String name = MyContact.getNameByNumber(this, mNumber);

        mView = View.inflate( this, R.layout.misseddialog, null );
		String sNumber = mNumber;
		((TextView)(mView.findViewById( R.id.MDnumber ))).setText( sNumber );
		((TextView)(mView.findViewById( R.id.MDname ))).setText( name );
		
		tDialog = new TimeoutDialog( mContext, 10 );

		Log.v( "A##", sNumber );
		
		switch( callType ) {
		case CallLog.Calls.OUTGOING_TYPE:
			String title = mContext.getString( R.string.redialLast );
			tDialog.setTitle( title );
			tDialog.setButton( DialogInterface.BUTTON_POSITIVE, getString(android.R.string.yes),
                    new DialogInterface.OnClickListener() {
				@Override
				public void onClick( DialogInterface dialog, int which ) {
					Redialing.start( mContext, mNumber, mSimId );
					Redialing.nextCall( mContext );
					tDialog.cncl();
				}
			});
			break;
		case CallLog.Calls.MISSED_TYPE:
		default:
			title = mContext.getString( R.string.redialRejected );
			tDialog.setTitle( title );
			//tDialog.setTitle( R.string.redialRejected );
			tDialog.setButton( DialogInterface.BUTTON_POSITIVE, getString( android.R.string.yes ),
                    new DialogInterface.OnClickListener() {
				@Override
				public void onClick( DialogInterface dialog, int which ) {
					Recall.call( mContext, mNumber );
					tDialog.cncl();
				}
			});
			break;
		}
		tDialog.setView( mView );
		tDialog.setButton( DialogInterface.BUTTON_NEGATIVE, getString( android.R.string.no ),
                new DialogInterface.OnClickListener() {
			@Override
			public void onClick( DialogInterface dialog, int arg1 ) {
				tDialog.cncl();
			}
		});
		tDialog.setCancelable( true );
		tDialog.setOnCancelListener( new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel( DialogInterface dialog ) {
				finish();
			}
		});
		tDialog.show();
	}
	void status() {
		mNumber = P.number( mContext );
		String name = MyContact.getNameByNumber( this, mNumber, mNumber );
		
		mView = View.inflate( this, R.layout.statusdialog, null );

        String attempts = mContext.getString( R.string.attemptString,
                P.currentAttempt( mContext ) + 1,
                P.lastAttempt( mContext ));
//		String attempts = String.format( Locale.getDefault(), getString( R.string.attemptString ),
//						P.currentAttempt( mContext ) + 1, P.lastAttempt( mContext ));

		((TextView)(mView.findViewById( R.id.number ))).setText( name );
		((TextView)(mView.findViewById( R.id.attempts ))).setText( attempts );
		( mTimeView = ((TextView)(mView.findViewById( R.id.time )))).setText( "" );
		
		//TimeoutDialog dialog = new TimeoutDialog( mContext, 10 );
		aDialog = new AlertDialog.Builder( mContext ).create();
		
		aDialog.setTitle(R.string.redialing);
		aDialog.setView(mView);
        aDialog.setButton( DialogInterface.BUTTON_POSITIVE, getString( R.string.now ), new DialogInterface.OnClickListener() {
			@Override
			public void onClick( DialogInterface dialog, int which ) {
				Intent i = new Intent();
				i.setAction( ReceiverCommand.ACTION_REDIALING_CALL_NOW );
				sendBroadcast( i );
				aDialog.cancel();
			}
		});
		aDialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.hide), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int arg1) {
                aDialog.cancel();
            }
        });
		
		aDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent();
                i.setAction(ReceiverCommand.ACTION_REDIALING_STOP);
                sendBroadcast(i);
                dialog.cancel();
            }
        });
		aDialog.setCancelable( true );
		aDialog.setOnCancelListener( new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel( DialogInterface dialog ) {
				finish();
			}
		});
		aDialog.show();
		
	}
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if( aDialog != null && aDialog.isShowing()) aDialog.cancel();
		if( tDialog != null && tDialog.isShowing()) tDialog.cncl();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter filter = new IntentFilter();
		filter.addAction( ServiceWait.ACTION_TIME_REMAIN );
		filter.addAction( ACTION_DIALOG_CLOSE );
		registerReceiver( mBReceiver, filter );
		final Window window = getWindow();
		window.addFlags( WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD );
		window.addFlags( WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED );
		window.addFlags( WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON );
		window.addFlags( WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON );
	}
	@Override
	protected void onPause() {
		super.onPause();
		try {
			unregisterReceiver( mBReceiver );
		} catch( Exception e ) {
            e.printStackTrace();
        }
	}
	
	private BroadcastReceiver mBReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive( Context context, Intent intent ) {
			String action;//intent.getAction()
			if( intent == null || ( action = intent.getAction()) == null ) return;
			if( action.equals( ServiceWait.ACTION_TIME_REMAIN )) {
				int got = intent.getIntExtra( ServiceWait.REMAIN_SECONDS, P.pause( mContext ));
				if( mTimeView != null ) {
					if( got == 0 )
						finish();
					else {
						String remain = String.format( Locale.getDefault(), "%02d:%02d", got / 60, got % 60 );
						mTimeView.setText( remain );
					}
				}
			} else if( action.equals( ACTION_DIALOG_CLOSE )) {
				finish();
			}
		}
	};
}
