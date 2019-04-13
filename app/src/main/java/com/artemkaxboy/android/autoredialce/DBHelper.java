package com.artemkaxboy.android.autoredialce;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.artemkaxboy.android.autoredialce.contacts.MyContact;
import com.artemkaxboy.android.autoredialce.contacts.MyPhone;

import java.util.ArrayList;

class DBHelper {
	private static final long SLEEP_TIME = 500;
	private static final int RETRIES = 3;
	
	
	private static final String DB_NAME = "db.sq3";
	private static final String TABLE_REJECTED = "TABLE_REJECTED";
	private static final String FIELD_ID = "_ID";
	private static final String FIELD_NUMBER = "NUMBER";
	private static final String FIELD_NUMBER_CLEAR = "NUMBER_CLEAR";
	
	private static final String CREATE_REJECT = "CREATE TABLE IF NOT EXISTS " + TABLE_REJECTED +
			" ( " + FIELD_ID + " INTEGER NOT NULL PRIMARY KEY, " + FIELD_NUMBER + " TEXT NOT NULL, " + FIELD_NUMBER_CLEAR + " TEXT NOT NULL );";
	
	private static SQLiteDatabase getDB( Context context ) {
		SQLiteDatabase db;
		for( int i = 0; i < RETRIES; i ++ ) {
			db = context.openOrCreateDatabase( DB_NAME, Context.MODE_PRIVATE, null );
			if( db == null ) {
				try {
					Thread.sleep( SLEEP_TIME );
				} catch( Exception ignored ){}
			} else {
				db.execSQL( CREATE_REJECT );
				return db;
			}
		}
		return null;
	}
	
	public static void addRejected( Context context, ArrayList<String> items ) {
		SQLiteDatabase db = getDB( context );
		if( db == null ) {
			Toast.makeText( context, "Error", Toast.LENGTH_LONG ).show();
			return;
		}
		for( String number : items ) {
			ContentValues value = new ContentValues();
			value.put( FIELD_NUMBER, number );
			value.put( FIELD_NUMBER_CLEAR, MyPhone.clean(number));
			db.insert( TABLE_REJECTED, null, value );
		}
		db.close();
	}
	public static void updateRejected( Context context, ArrayList<String> items, boolean[] checked ) {
		SQLiteDatabase db = getDB( context );
		if( db == null ) {
			Toast.makeText( context, "Error", Toast.LENGTH_LONG ).show();
			return;
		}
		for( int i = 0; i < items.size(); i ++ ) {
			if( checked[i] ) {
				if( isInRejected( context, items.get( i )) > 0 ) continue;
				ContentValues value = new ContentValues();
				value.put( FIELD_NUMBER, items.get( i ));
				value.put( FIELD_NUMBER_CLEAR, MyPhone.clean( items.get( i )));
				db.insert( TABLE_REJECTED, null, value );
			} else {
				long id = isInRejected( context, items.get( i ));
				if( id > 0 ) {
					db.delete( TABLE_REJECTED, FIELD_ID + " = " + id, null );
				}
			}
		}
		db.close();
	}
	public static ArrayList<MyContact> getListRejected( Context context ) {
		ArrayList<MyContact> list = new ArrayList<>();
		SQLiteDatabase db = getDB( context );
		if( db == null ) {
			Toast.makeText( context, "Error", Toast.LENGTH_LONG ).show();
			return list;
		}
		Cursor c = db.query( TABLE_REJECTED, null, null, null, null, null, null );
		if( c != null ) {
			if( c.moveToFirst()) {
				do {
					String number = c.getString( c.getColumnIndex( FIELD_NUMBER ));
					boolean cont = false;
					for( MyContact mc : list ) 
						if( mc.contains( number )) { 
							cont = true;
							break;
						}
					if( cont ) continue;
					MyContact contact = new MyContact();
					if( contact.loadByNumber( context, number ))
						list.add( contact );
					else {
						db.delete( TABLE_REJECTED, FIELD_ID + " = " + c.getLong( c.getColumnIndex( FIELD_ID )), null );
					}
				} while( c.moveToNext());
			}
			c.close();
		}
		db.close();
		return list;
	}
	public static long isInRejected( Context context, String number ) {
		long ret = -1;
		number = MyPhone.clean( number );
		SQLiteDatabase db = getDB( context );
		if( db == null ) {
			Toast.makeText( context, "Error", Toast.LENGTH_LONG ).show();
			return ret;
		}
		Cursor c = db.query( TABLE_REJECTED, null, FIELD_NUMBER_CLEAR + " LIKE '" + number + "'", null, null, null, null );
		if( c != null ) {
			if( c.moveToFirst())
				ret = c.getLong( c.getColumnIndex( FIELD_ID ));
			c.close();
		}
		db.close();
		return ret;
	}
}
