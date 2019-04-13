package com.artemkaxboy.android.autoredialce;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.artemkaxboy.android.autoredialce.contacts.MyContact;
import com.artemkaxboy.android.autoredialce.contacts.MyPhone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ActivityRejectList extends AppCompatActivity implements AdapterView.OnItemClickListener {
	public static final int PICK_CONTACT = 1;

    class MyArrayAdapter extends BaseAdapter {
		ArrayList<MyContact> contacts;
		MyArrayAdapter( ArrayList<MyContact> contacts ) {
			Collections.sort( contacts, new Comparator<MyContact>(){
				@Override
				public int compare( MyContact c1, MyContact c2 ) {
					return c1.getDisplayName().compareToIgnoreCase( c2.getDisplayName());
				}
			});
			this.contacts = contacts;
		}
		@Override
		public int getCount() {
			return contacts.size();
		}
		@Override
		public MyContact getItem( int position ) {
			return contacts.get( position );
		}
		@Override
		public long getItemId( int position ) {
			return contacts.get( position ).getId();
		}
		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			if( convertView == null )
				convertView = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE )).inflate( android.R.layout.simple_list_item_1, null );
			((TextView)convertView.findViewById( android.R.id.text1 )).setText( contacts.get( position ).getDisplayName());
			return convertView;
		}
	}
	
	
	Context mContext;
	MyArrayAdapter mAdapter;
    ListView mListView;
	
	@Override
	public void onCreate( Bundle savedInstanceState ) {
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setHomeButtonEnabled( true );
        ab.setDisplayHomeAsUpEnabled( true );
        super.onCreate(savedInstanceState);
        if(ActivityCompat.checkSelfPermission( this, Manifest.permission.READ_CONTACTS )
                != PackageManager.PERMISSION_GRANTED ) {
            return;
        }
		setContentView( R.layout.diallist );
		mContext = this;
		firstRun();
		TextView v = new TextView( this );
		v.setText( R.string.rejectedListHelp);
        v.setEnabled(false);
        mListView = (ListView)findViewById( android.R.id.list );
        assert mListView !=  null;
        mListView.setOnItemClickListener( this );
        mListView.addHeaderView( v, null, false );
		fill();
	}
	void fill() {
		//mList = DBHelper.getListRejected( mContext );
		mAdapter = new MyArrayAdapter( DBHelper.getListRejected( mContext ));
		mListView.setAdapter(mAdapter);
	}
	@Override
	public boolean onCreateOptionsMenu( Menu mMenu ) {
		super.onCreateOptionsMenu( mMenu );
		
		mMenu.add( Menu.NONE, 1, Menu.NONE, getString( R.string.add ) )
				.setIcon( android.R.drawable.ic_menu_add );
		mMenu.add( Menu.NONE, 2, Menu.NONE, getString( R.string.caution ) )
				.setIcon( android.R.drawable.ic_menu_info_details );
		
		mMenu.add( Menu.NONE, 1, Menu.NONE, getString( R.string.add ))
				.setIcon( android.R.drawable.ic_menu_add )
				.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );
		mMenu.add( Menu.NONE, 2, Menu.NONE, getString( R.string.caution ))
				.setIcon( android.R.drawable.ic_menu_info_details )
				.setShowAsAction( MenuItem.SHOW_AS_ACTION_IF_ROOM );
		
		return super.onCreateOptionsMenu( mMenu );
	}
	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		switch( item.getItemId()) {
		case 1:
			Intent intent = new Intent( Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI );
			intent.setType( ContactsContract.Contacts.CONTENT_TYPE );
			//intent = new Intent( Intent.ACTION_PICK, ContactsContract.Groups.CONTENT_URI );
			//intent.setType( ContactsContract.Contacts.CONTENT_TYPE );
			startActivityForResult( intent, PICK_CONTACT );
			
			//onSearchRequested();
			return false;
		case 2:
			showHelp();
			return false;
		case android.R.id.home:
			finish();
			return false;
		default:
			return super.onOptionsItemSelected( item );
	    }
	}
	public void onActivityResult( int reqCode, int resultCode, Intent data ) {
		super.onActivityResult(reqCode, resultCode, data);
		if( resultCode != Activity.RESULT_OK ) return;
		switch( reqCode ) {
		case PICK_CONTACT:
			String id = data.getData().getLastPathSegment();
            Cursor c =  getContentResolver().query( Phone.CONTENT_URI, null,
                    Phone.CONTACT_ID + " = " + id, null, null );
            final ArrayList<String> strings = new ArrayList<>();
            final ArrayList<String> numbers = new ArrayList<>();
            final ArrayList<Boolean> checked = new ArrayList<>();
            String name = "";
            if( c != null ) {
                if( c.moveToFirst()) {
                    name = c.getString( c.getColumnIndex( Phone.DISPLAY_NAME ));
                    do {
                        String number = c.getString( c.getColumnIndex( Phone.NUMBER ));
                        if( numbers.contains( number )) continue;
                        numbers.add( number );
                        strings.add( Phone.getTypeLabel( getResources(),
                                c.getInt(c.getColumnIndex( Phone.TYPE )),
                                c.getString( c.getColumnIndex( Phone.LABEL ))) + ": " + number );
                        checked.add( false );
                    } while( c.moveToNext());
                }
                c.close();
            }

            if( numbers.size() == 0 ) {
                Toast.makeText( mContext, "no numbers", Toast.LENGTH_LONG ).show();
                return;
            }


            new AlertDialog.Builder( this )
                    .setTitle( name )
                    .setMultiChoiceItems( strings.toArray( new String[strings.size()] ), null,
                            new OnMultiChoiceClickListener(){
                        @Override
                        public void onClick( DialogInterface arg0, int which, boolean ch ) {
                            checked.set( which, ch );
                        }
                    })
                    .setPositiveButton( android.R.string.ok, new OnClickListener(){
                        @Override
                        public void onClick( DialogInterface dialog, int which ) {
                            for( int i = checked.size() - 1; i >= 0; i -- )
                                if( !checked.get( i )) numbers.remove( i );
                            if( numbers.size() > 0 ) DBHelper.addRejected( mContext, numbers );
                            fill();
                        }
                    })
                    .setNegativeButton( android.R.string.cancel, new OnClickListener(){
                        @Override
                        public void onClick( DialogInterface dialog, int which ) {}
                    })
                    .show();
		}
	}

	@Override
	public void onItemClick( AdapterView<?> parent, View v, int position, long id ) {
//		super.onListItemClick( l, v, position, id );
		
		MyContact contact = mAdapter.getItem( position - 1 );
		
		final ArrayList<String> numbers = new ArrayList<>();
		final CharSequence[] strings = new CharSequence[contact.getPhones().size()];
		final boolean[] checked = new boolean[strings.length];
		
		for( int i = 0; i < strings.length; i ++ ) {
			MyPhone p = contact.getPhones().get( i );
			numbers.add( p.getNumber());
			strings[i] = p.getLabel() + ": " + p.getNumber();
			checked[i] = DBHelper.isInRejected(mContext, p.getNumber()) > 0;
		}
		
		new AlertDialog.Builder( this )
			.setTitle( contact.getDisplayName())
			.setMultiChoiceItems( strings, checked, new OnMultiChoiceClickListener(){
			@Override
			public void onClick( DialogInterface dialog, int which, boolean ch ) {
				checked[which] = ch;
			}
			
		})
		.setPositiveButton( android.R.string.ok, new OnClickListener(){
			@Override
			public void onClick( DialogInterface dialog, int which ) {
				DBHelper.updateRejected( mContext, numbers, checked );
				fill();
			}
		})
		.setNegativeButton( android.R.string.cancel, new OnClickListener(){
			@Override
			public void onClick( DialogInterface dialog, int which ) {}
		})
		.show();
	}
	
	private void firstRun() {
		if( PreferenceManager.getDefaultSharedPreferences( this ).getBoolean( "firstRun.RejectedList", true )) {
			showHelp();
			Editor editor = PreferenceManager.getDefaultSharedPreferences( this ).edit();
			editor.putBoolean( "firstRun.RejectedList", false );
			editor.apply();
		}
	}
	private void showHelp() {
		AlertDialog help = new AlertDialog.Builder( this )
				.setTitle( R.string.caution )
				.setMessage( R.string.rejectedListCaution )
				.setPositiveButton( "OK", null )
				.setCancelable( true )
				.create();
		help.show();
	}
}
