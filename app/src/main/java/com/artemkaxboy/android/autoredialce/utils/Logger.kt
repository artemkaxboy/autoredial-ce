package com.artemkaxboy.android.autoredialce.utils

import android.util.Log
import com.artemkaxboy.android.autoredialce.BuildConfig

object Logger {

    fun wtf(msg: () -> String) {
        Log.wtf(Cons.TAG, msg.invoke())
    }

    fun wtf(e: Throwable) {
        Log.wtf(Cons.TAG, e)
    }

    fun wtf(msg: () -> String, e: Throwable) {
        Log.wtf(Cons.TAG, msg.invoke(), e)
    }

    fun error(msg: () -> String) {
        Log.e(Cons.TAG, msg.invoke())
    }

    fun error(msg: () -> String, e: Throwable) {
        Log.e(Cons.TAG, msg.invoke(), e)
    }

    fun warning(msg: () -> String) {
        Log.w(Cons.TAG, msg.invoke())
    }

    fun warning(e: Throwable) {
        Log.w(Cons.TAG, e)
    }

    fun warning(msg: () -> String, e: Throwable) {
        Log.w(Cons.TAG, msg.invoke(), e)
    }

    fun info(msg: () -> String) {
        Log.i(Cons.TAG, msg.invoke())
    }

    fun info(msg: () -> String, e: Throwable) {
        Log.i(Cons.TAG, msg.invoke(), e)
    }

    fun debug(tag: () -> String, msg: () -> String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag.invoke(), msg.invoke())
        }
    }

    fun debug(msg: () -> String) {
        if (BuildConfig.DEBUG) {
            Log.d(Cons.TAG, msg.invoke())
        }
    }

    fun debug(msg: () -> String, e: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.d(Cons.TAG, msg.invoke(), e)
        }
    }

    fun verbose(msg: () -> String) {
        Log.v(Cons.TAG, msg.invoke())
    }

    fun verbose(msg: () -> String, e: Throwable) {
        Log.v(Cons.TAG, msg.invoke(), e)
    }
}
