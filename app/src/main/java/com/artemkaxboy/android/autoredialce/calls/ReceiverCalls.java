package com.artemkaxboy.android.autoredialce.calls;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.artemkaxboy.android.autoredialce.BuildConfig;
import com.artemkaxboy.android.autoredialce.P;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Set;

public class ReceiverCalls extends BroadcastReceiver {
    private static final String TAG = "ReceiverCalls";

	public static String CALL_DIRECTION = "__callDirection";
	public static String CALL_DIRECTION_INBOUND = "__callDirectionIn";
	public static String CALL_DIRECTION_OUTBOUND = "__callDirectionOut";

    protected static String LAST_NUMBER = "__lastNumber";
	protected static String LAST_TIME = "__lastTime";
	
	protected Context mContext;
	protected Intent mIntent;
	protected String mNumber, mTime;
	@Override
	public void onReceive( Context context, Intent intent ) {
        if(BuildConfig.DEBUG) {
            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            Log.i(TAG, "********Received**********" + df.format( System.currentTimeMillis()));
            Log.i(TAG, "Action: " + intent.getAction());
            Log.i(TAG, "Data String: " + intent.getData());
            Log.i(TAG, "*****Extras*****");
            Bundle b = intent.getExtras();
            Set<String> ks = b.keySet();
            for (String k : ks) {
                Object kk = b.get(k);
                if( kk != null ) Log.i(TAG, k + ": " + kk.toString());
            }
        }

        String PREV_CALL_STATE = "__prevCallState";

        mContext = context;
		mIntent = intent;
		if( intent == null || intent.getAction() == null )
			return;
		
		if( intent.getAction().equals( TelephonyManager.ACTION_PHONE_STATE_CHANGED )) {
			String state = intent.getStringExtra( TelephonyManager.EXTRA_STATE );

            if( state.equals( TelephonyManager.EXTRA_STATE_IDLE )) {
				String callDirection = P.getP(mContext, CALL_DIRECTION, CALL_DIRECTION_INBOUND);
				String prevCallState = P.getP( mContext, PREV_CALL_STATE, TelephonyManager.EXTRA_STATE_OFFHOOK );
				
				//-----Missed/Dropped calls
				if( CALL_DIRECTION_INBOUND.equals(callDirection) &&
                        TelephonyManager.EXTRA_STATE_RINGING.equals(prevCallState)) {
					_idleMissedOrDropped();
				}
				//----Dropped or answered
				else if ( CALL_DIRECTION_INBOUND.equals( callDirection ) &&
                        TelephonyManager.EXTRA_STATE_OFFHOOK.equals( prevCallState )) {
					_idleDroppedOrAnswered();
				}
				//-----Outbound
				else if( CALL_DIRECTION_OUTBOUND.equals( callDirection )) {
					mNumber = P.getP( mContext, LAST_NUMBER, null );
					mTime = P.getP( mContext, LAST_TIME, "0" );
					_idleOutbound();
				}
			} else if( state.equals( TelephonyManager.EXTRA_STATE_RINGING )) {
				P.putP( mContext, PREV_CALL_STATE, state );
				P.putP( mContext, CALL_DIRECTION, CALL_DIRECTION_INBOUND );
				P.putP( mContext, LAST_NUMBER, intent.getStringExtra( TelephonyManager.EXTRA_INCOMING_NUMBER ));
				P.putP( mContext, LAST_TIME, System.currentTimeMillis());
				_ringing();
			} else if( state.equals( TelephonyManager.EXTRA_STATE_OFFHOOK )) {
				P.putP( mContext, PREV_CALL_STATE, state );
				_offhook();
				//P.putP( mContext, LAST_TIME, System.currentTimeMillis());
			}
		} else if( intent.getAction().equals( Intent.ACTION_NEW_OUTGOING_CALL )) {
			String number = getResultData();
			if( number == null )
				number = intent.getStringExtra( Intent.EXTRA_PHONE_NUMBER );
			P.putP( mContext, LAST_NUMBER, number );
			P.putP( mContext, CALL_DIRECTION, CALL_DIRECTION_OUTBOUND );
			P.putP( mContext, LAST_TIME, System.currentTimeMillis());
            P.putP( mContext, "subs", intent.getIntExtra( "subscription", -1 ));
			mNumber = P.getP( mContext, LAST_NUMBER, null );
			_outbound();
		}
	}
	
	public void _idleMissedOrDropped() {
		
	}
	public void _idleDroppedOrAnswered() {
		
	}
	public void _idleOutbound() {
		
	}
	public void _ringing() {
		
	}
	public void _offhook() {
		
	}
	public void _outbound() {
		
	}
}
