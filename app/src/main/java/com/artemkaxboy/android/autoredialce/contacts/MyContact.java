package com.artemkaxboy.android.autoredialce.contacts;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.PhoneLookup;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;

public class MyContact {
	long id = 0;
	String displayName = null;
	ArrayList<MyPhone> phones = null;
	
	public MyContact() {
		phones = new ArrayList<>();
	}
	public void setId( long id ) { this.id = id; }
	public long getId() { return id; }
    @SuppressWarnings("unused")
	public void setDisplayName( String displayName ) { this.displayName = displayName; }
	public String getDisplayName() { return displayName != null ? displayName : ""; }
    @SuppressWarnings("unused")
    public void setPhones( ArrayList<MyPhone> phones ) { this.phones = phones; }
	public ArrayList<MyPhone> getPhones() { return phones; }
	
	public boolean loadByNumber( Context context, String number ) {
		Uri uri = Uri.withAppendedPath( PhoneLookup.CONTENT_FILTER_URI, Uri.encode( number ));
		Cursor cId = context.getContentResolver().query( uri, null, null, null, null );
		if( cId != null ) {
			if( cId.moveToFirst()) {
				id = cId.getLong( cId.getColumnIndex( PhoneLookup._ID ));
				Cursor c = context.getContentResolver().query( Phone.CONTENT_URI, null, Phone.CONTACT_ID + " = " + id, null, null );
				if( c != null ) {
					if( c.moveToFirst()) {
						displayName = c.getString( c.getColumnIndex( Phone.DISPLAY_NAME ));
						do {
							number = c.getString( c.getColumnIndex( Phone.NUMBER ));
							if( contains( number )) continue;
							MyPhone phone = new MyPhone();
							phone.setId( c.getLong( c.getColumnIndex( Phone._ID )));
							phone.setNumber( number );
							phone.setType( c.getInt( c.getColumnIndex( Phone.TYPE )));
							phone.setLabel( Phone.getTypeLabel( context.getResources(), phone.getType(), c.getString( c.getColumnIndex( Phone.LABEL ))).toString());
							phones.add( phone );
						} while( c.moveToNext());
					}
					c.close();
				}
			} else return false;
			cId.close();
		}
		return true;
	}
	public boolean contains( String number ) {
		for( MyPhone phone : phones )
			if( MyPhone.compare( phone.getNumber(), number ))
				return true;
		return false;
	}
	
	
	
	
	public static String getNameByNumber( Context context, String number ) {
		String ret = context.getString( android.R.string.unknownName );
        if( !isGranted( context )) return ret;
		Uri uri = Uri.withAppendedPath( PhoneLookup.CONTENT_FILTER_URI, Uri.encode( number ));
		Cursor cursor = context.getContentResolver().query( uri, new String[]{ PhoneLookup.DISPLAY_NAME }, null, null, null );
		try {
            assert cursor != null;
			cursor.moveToFirst();
			ret = cursor.getString( cursor.getColumnIndex( PhoneLookup.DISPLAY_NAME ));
			cursor.close();
		} catch( Exception ignored ) {}
		return ret;
	}
	public static String getNameByNumber( Context context, String number, String defValue ) {
		String name = getNameByNumber( context, number );
		return (( name.equals( context.getString( android.R.string.unknownName ))) ? defValue : name );
	}
	
	public static String getTypeByNumber( Context context, String number ) {
		String ret = "";
        if( !isGranted( context )) return ret;
		Uri uri = Uri.withAppendedPath( PhoneLookup.CONTENT_FILTER_URI, Uri.encode( number ));
		Cursor cursor = context.getContentResolver().query( uri, new String[]{ PhoneLookup.TYPE, PhoneLookup.LABEL }, null, null, null );
		try {
            assert cursor != null;
			cursor.moveToFirst();
			ret = Phone.getTypeLabel( context.getResources(),
					cursor.getInt( cursor.getColumnIndex( PhoneLookup.TYPE )),
							cursor.getString( cursor.getColumnIndex( PhoneLookup.LABEL ))).toString();
			cursor.close();
		} catch( Exception ignored ) {}
		return ret;
	}
    @SuppressWarnings("unused")
	public static MyContact getNumbersById( Context context, long id ) {
		MyContact contact = new MyContact();
		contact.setId( id );
		if( !isGranted( context )) return contact;
		Cursor c = context.getContentResolver().query( Phone.CONTENT_URI, null, Phone.CONTACT_ID + " = " + id, null, null );
		if( c != null ) {
			if( c.moveToFirst()) {
				do {
					MyPhone p = new MyPhone();
					p.setId(c.getLong( c.getColumnIndex( Phone._ID )));
					p.type = c.getInt( c.getColumnIndex( Phone.TYPE ));
					p.setLabel(Phone.getTypeLabel( context.getResources(), p.type, c.getString( c.getColumnIndex( Phone.LABEL ))).toString());
					p.setNumber(c.getString( c.getColumnIndex( Phone.NUMBER )));
					contact.phones.add( p );
				} while( c.moveToNext());
			}
			c.close();
		}
		return contact;
	}

    private static boolean isGranted( Context context ) {
        return ActivityCompat.checkSelfPermission( context, Manifest.permission.READ_CONTACTS )
                == PackageManager.PERMISSION_GRANTED;
    }
}
