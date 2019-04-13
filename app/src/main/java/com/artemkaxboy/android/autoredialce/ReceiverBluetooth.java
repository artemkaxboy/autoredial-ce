package com.artemkaxboy.android.autoredialce;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReceiverBluetooth extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action;
		if( intent == null || ( action = intent.getAction()) == null ) return;
		if( action.equals( BluetoothDevice.ACTION_ACL_CONNECTED )) {
			P.bluetoothConnected( context, true );
		} else if( action.equals( BluetoothDevice.ACTION_ACL_DISCONNECTED )) {
			P.bluetoothConnected( context, false );
		}
	}

}
