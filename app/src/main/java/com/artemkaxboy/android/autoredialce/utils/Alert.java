package com.artemkaxboy.android.autoredialce.utils;

import android.content.Context;
import androidx.appcompat.app.AlertDialog;

import com.artemkaxboy.android.autoredialce.R;

/**
 * Created by artem.kolin on 2016/07/10.
 * Класс для выдачи сообщения пользователю.
 */
public class Alert {
    public static void complain( Context context, int stringId, Object... args ) {
        complain( context, context.getString( stringId, args ));
    }

    public static void complain( Context context, String message ) {
        alert( context, null, context.getString(R.string.error, message ));
    }

    public static void alert( Context context, int messageId ) {
        alert( context, null, context.getString( messageId ));
    }

    public static void alert(Context context, int titleId, int messageId ) {
        alert( context, context.getString( titleId ), context.getString( messageId ));
    }

    public static void alert( Context context, String message ) {
        alert( context, null, message );
    }

    public static void alert( Context context, String title, String message ) {
        new AlertDialog.Builder( context )
                .setTitle( title )
                .setMessage( message )
                .setPositiveButton( android.R.string.ok, null )
                .create().show();
    }
}
