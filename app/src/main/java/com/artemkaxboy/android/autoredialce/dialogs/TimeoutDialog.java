package com.artemkaxboy.android.autoredialce.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import androidx.appcompat.app.AlertDialog;
import android.view.View;


public class TimeoutDialog {
	AlertDialog mAlertDialog;
	public TimeoutDialog( Context context, int seconds ) {
		mAlertDialog = new AlertDialog.Builder( context )
                .create();
		mTimeout = seconds * 1000;
	}
    @SuppressWarnings("unused")
	public TimeoutDialog( Context context ) {
		this( context, 10 );
	}
	
	
	AsyncTask<Long, Void, Boolean> asyncTask;
	long mTimeout;
	public void show( int timeout ) {
		mTimeout = timeout;
		show();
	}
	public void show() {
		asyncTask = new AsyncTask<Long,Void,Boolean>() {
			@Override
			protected Boolean doInBackground( Long... params ) {
				try {
					Thread.sleep( params[0] );
				} catch ( InterruptedException e ) {
					return false;
				}
				return true;
			}
			@Override
			protected void onPostExecute( Boolean result ) {
				if( result ) cncl();
			}
		}.execute( mTimeout );
		mAlertDialog.show();
	}
	
	public void cncl() {
		asyncTask.cancel( true );
		mAlertDialog.cancel();
	}
	
	
	public void setTitle( CharSequence text ) {
		mAlertDialog.setTitle( text );
	}
	public void setTitle( int titleId ) {
		mAlertDialog.setTitle( titleId );
	}
	public void setMessage( CharSequence text ) {
		mAlertDialog.setMessage( text );
	}
	public void setButton( int whichButton, CharSequence text, DialogInterface.OnClickListener listener ) {
        mAlertDialog.setButton( whichButton, text, listener );
	}
	public void setCancelable( boolean flag ) {
		mAlertDialog.setCancelable( flag );
	}
	public void setOnCancelListener( DialogInterface.OnCancelListener listener ) {
		mAlertDialog.setOnCancelListener( listener );
	}
	public void setView( View view ) {
		int space = 20;
		mAlertDialog.setView( view, space, space, space, space );
	}
    @SuppressWarnings("unused")
	public void setView( View view, int viewSpacingLeft, int viewSpacingTop,
			int viewSpacingRight, int viewSpacingBottom ) {
		mAlertDialog.setView( view, viewSpacingLeft, viewSpacingTop, viewSpacingRight, viewSpacingBottom );
	}
	public boolean isShowing() {
		return mAlertDialog.isShowing();
	}
}
