package com.artemkaxboy.android.autoredialce;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.provider.CallLog;
import android.util.Log;
import android.widget.Toast;

import com.artemkaxboy.android.autoredialce.calls.CallInfo;
import com.artemkaxboy.android.autoredialce.calls.TaskGetCallInfo;
import com.artemkaxboy.android.autoredialce.contacts.MyContact;
import com.artemkaxboy.android.autoredialce.contacts.MyPhone;


public class ReceiverCalls extends com.artemkaxboy.android.autoredialce.calls.ReceiverCalls {
	@Override
	public void _offhook() {
        if( !P.enabled( mContext )) return;
        if( CALL_DIRECTION_OUTBOUND.equals( P.getP( mContext, CALL_DIRECTION, CALL_DIRECTION_INBOUND ))) {
			if(( P.speakerAlways( mContext ) || ( P.speaker( mContext ) && P.masterCall( mContext ))) && !headsetOn()) {
				try {
					Thread.sleep( P.speakerTime( mContext ));
				} catch( Exception ignored ) {}
				((AudioManager)mContext.getSystemService( Context.AUDIO_SERVICE )).setSpeakerphoneOn( true );
			} else {
                ((AudioManager)mContext.getSystemService( Context.AUDIO_SERVICE )).setSpeakerphoneOn( false );
            }
		}
	}
	@Override
	public void _idleMissedOrDropped() {
		if( !P.enabled( mContext )) return;
		if( P.missedEnabled( mContext ) || P.missedList( mContext )) {
			new TaskGetCallInfo( mContext ){
				@Override
				protected void onPostExecute( CallInfo callInfo ) {
					if( callInfo.getDuration() < 0 ) return;
					if( P.missedList( mContext ) 
							&& callInfo.getType() != CallLog.Calls.MISSED_TYPE
							&& DBHelper.isInRejected( mContext, callInfo.getNumber()) > 0 ) {
						Recall.call( mContext, callInfo.getNumber());
						return;
					}
					if( P.missedEnabled( mContext )) {
						Recall.query( mContext, callInfo.getNumber());
					}
				}
			}.execute( P.getP( mContext, LAST_NUMBER, null ),
				P.getP( mContext, LAST_TIME, "0" ));
		}
	}
	@Override
	public void _idleDroppedOrAnswered() {
		if( !P.enabled( mContext )) return;
		if( P.missedEnabled( mContext ) || P.missedList( mContext )) {
			new TaskGetCallInfo( mContext ) {
				@Override
				protected void onPostExecute( CallInfo callInfo ) {
					if( callInfo.getDuration() != 0 ) return;
					if( P.missedList( mContext ) && DBHelper.isInRejected( mContext, callInfo.getNumber()) > 0 ) {
						Recall.call( mContext, callInfo.getNumber());
						return;
					}
					if( P.missedEnabled( mContext ))
						Recall.query( mContext, callInfo.getNumber());
				}
			}.execute( P.getP( mContext, LAST_NUMBER, null ),
					P.getP( mContext, LAST_TIME, "0" ));
		}
	}
	@Override
	public void _idleOutbound() {
        Log.w( "A##", "pause:" + ( System.currentTimeMillis() - P.lastIdle( mContext )));
        if( System.currentTimeMillis() - 1000 < P.lastIdle( mContext )) {
            if( BuildConfig.DEBUG ) Log.w( "A##", "skip idle intent");
            return;
        }
        P.lastIdle( mContext, System.currentTimeMillis());

		if( !P.enabled( mContext ) || !P.autoRedialOn( mContext )) {
            P.enabled(mContext);
            return;
        }
		if( P.ignoreLast( mContext )) {
			P.ignoreLast( mContext, false );
			return;
		}
		if( P.masterCall( mContext )) {
			if( !Redialing.keepOn( mContext )) {
				Redialing.stop( mContext );
				return;
			}
		} else {
			if( P.redialing( mContext )) {
				if( !MyPhone.compare(P.getP(mContext, LAST_NUMBER, null), P.number(mContext)))
					return;
			}
		}
		new TaskGetCallInfo( mContext ) {
            @Override
            protected void onPostExecute(CallInfo callInfo) {
                long duration = callInfo.getDuration();
                if (duration > P.minDuration(mContext)) {
                    if (P.masterCall(mContext) && P.redialing(mContext)) {
                        Redialing.stop(mContext);
                    }
                } else if (duration < 0) {
                    Toast.makeText(mContext, "AutoRedial: Error 1!", Toast.LENGTH_LONG).show();
                } else {
                    long s = System.currentTimeMillis() - P.outTime(mContext) - (duration * 1000);
                    Log.v("A##", "" + s);

                    if (P.redialing(mContext)) {
                        if (P.masterCall(mContext)) {
                            Redialing.endCall(mContext);
                            Redialing.waitNext(mContext);
                        }/* else {
                            if (Redialing.checkDeferred(mContext)) return;
                        }*/
                    } else {
                        Redialing.query(mContext, callInfo.getNumber(), callInfo.getSimId());
                    }
                }
            }
		}.execute( P.getP( mContext, LAST_NUMBER, null ),
				P.getP( mContext, LAST_TIME, "0" ));
		
	}
	@Override
	public void _outbound() {
		if( !P.enabled( mContext )) return;

        String number = getResultData();
        if( number == null ) return;

        switch( number ) {
            case "*2433811":
                P.speakerAlways( mContext, true );
                Toast.makeText( mContext, "Speaker ENABLED", Toast.LENGTH_SHORT ).show();
                P.ignoreLast( mContext, true );
                setResultData( null );
                return;
            case "*2433810":
                P.speakerAlways( mContext, false );
                Toast.makeText( mContext, "Speaker DISABLED", Toast.LENGTH_SHORT ).show();
                P.ignoreLast( mContext, true );
                setResultData( null );
                return;
        }

        P.outTime( mContext, System.currentTimeMillis());
		if( !P.confirmOut( mContext ) || P.confirmIsGot( mContext ) || ( P.confirmHeadset( mContext ) && headsetOn())) {
			P.confirmIsGot( mContext, false );
			return;
		}


		
		char firstNum = number.charAt( 0 );
		char lastNum = number.charAt( number.length() - 1 );
		if( P.confirmExceptUssd( mContext ) && (( firstNum == '*' || firstNum == '#' ) && lastNum == '#' ))
			return;
			
		String name = MyContact.getNameByNumber(mContext, number);
		if( P.confirmExceptUnknown( mContext )
				&& mContext.getString( android.R.string.unknownName ).equals( name ))
			return;
		setResultData( null );
		Intent i = new Intent( mContext, ActivityConfirm.class )
				.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK 
						| Intent.FLAG_ACTIVITY_NO_ANIMATION )
				.putExtra( "number", number )
				.putExtra( "name", name )
				.putExtra( CALL_DIRECTION, CALL_DIRECTION_OUTBOUND );
		mContext.startActivity( i );
	}


	private boolean headsetOn() {
		AudioManager am = (AudioManager)mContext.getSystemService( Context.AUDIO_SERVICE );
        return am.isWiredHeadsetOn() | P.bluetoothConnected( mContext );
	}
}
