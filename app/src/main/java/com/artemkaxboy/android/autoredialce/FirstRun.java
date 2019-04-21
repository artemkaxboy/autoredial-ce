package com.artemkaxboy.android.autoredialce;

import android.content.Context;

import com.artemkaxboy.android.autoredialce.utils.Alert;

public class FirstRun {
	public static final String FIRST_RUN = "firstRun";
	
	public static void check( Context context ) {
		if( P.getP( context, FIRST_RUN, true )) {
            Alert.INSTANCE.alert( context, R.string.remember, R.string.mainHelp );
			P.putP( context, FIRST_RUN, false );
		}
	}
}
