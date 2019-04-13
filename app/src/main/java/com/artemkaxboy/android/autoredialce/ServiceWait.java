package com.artemkaxboy.android.autoredialce;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;

public class ServiceWait extends Service {
	public static final String ACTION_TIME_REMAIN = "ACTION_TIME_REMAIN";
	public static final String REMAIN_SECONDS = "remain_seconds";
	
	@Override public IBinder onBind( Intent arg0 ) { return null; }
	
	Context mContext;
	CountDownTimer mCountDownTimer;
	
	@Override
	public int onStartCommand( Intent intent, int flags, int startId ) {
		mContext = this;
		
		mCountDownTimer = new CountDownTimer( P.pause( mContext ) * 1000, 100L ) {
			@Override
			public void onFinish() {
				mContext.sendBroadcast( new Intent( ACTION_TIME_REMAIN ).putExtra( REMAIN_SECONDS, 0 ));
				call();
			}
			@Override
			public void onTick( long millisUntilFinished ) {
				mContext.sendBroadcast( new Intent( ACTION_TIME_REMAIN ).putExtra( REMAIN_SECONDS, (int)(millisUntilFinished / 1000 )));
			}
		}.start();
		if( P.status( mContext )) {
			Intent status = new Intent( mContext, ActivityDialog.class );
			status.putExtra( ActivityDialog.TYPE, ActivityDialog.TYPE_STATUS );
			status.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
			startActivity( status );
		}
		
		return START_STICKY;
	}
	public void call() {
		if( mCountDownTimer != null ) {
			mCountDownTimer.cancel();
			mCountDownTimer = null;
		}
		Redialing.nextCall( mContext );
		stopSelf();
	}
	@Override
	public void onDestroy() {
		
		if( mCountDownTimer != null ) {
			mCountDownTimer.cancel();
			mCountDownTimer = null;
		}
		stopSelf();
	}
	
}
