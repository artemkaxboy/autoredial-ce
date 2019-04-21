package com.artemkaxboy.android.autoredialce.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog

/**
 * Created by artem.kolin on 2016/07/10. Class to show messages to user.
 */
object Alert {

    fun alert(context: Context, messageId: Int) {
        alert(context, null, context.getString(messageId))
    }

    fun alert(context: Context, titleId: Int, messageId: Int) {
        alert(context, context.getString(titleId), context.getString(messageId))
    }

    fun alert(context: Context, message: String) {
        alert(context, null, message)
    }

    fun alert(context: Context, title: String?, message: String) {
        AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .create().show()
    }
}
