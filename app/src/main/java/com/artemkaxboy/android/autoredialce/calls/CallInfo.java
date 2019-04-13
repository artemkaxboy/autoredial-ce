package com.artemkaxboy.android.autoredialce.calls;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CallInfo {
	String mNumber;
	long mDate, mDuration = -1, mId;
	int mType;
    int mSimId = -1;
	
	public void setNumber( String number ) {
		mNumber = number;
	}
	public String getNumber() {
		return mNumber;
	}
	public void setDate( long date ) {
		mDate = date;
	}
	public long getDate() {
		return mDate;
	}
	public String getDateString() {
		return new SimpleDateFormat( "y/m/d H:m:s", Locale.getDefault()).format(new Date( mDate ));
	}
	public void setType( int type ) {
		mType = type;
	}
	public int getType() {
		return mType;
	}
	public void setDuration( long duration ) {
		mDuration = duration;
	}
	public long getDuration() {
		return mDuration;
	}
	public void setId( long id ) {
		mId = id;
	}
	public long getId() {
		return mId;
	}
    public void setSimId( int simId ) { mSimId = simId; }
    public int getSimId() { return mSimId; }
}