package com.artemkaxboy.android.autoredialce.utils

import android.util.Log
import com.artemkaxboy.android.autoredialce.BuildConfig

object Logger {

    @Suppress("unused") /* for public usage */
    fun wtf(msg: () -> String) {
        Log.wtf(TAG, msg.invoke())
    }

    @Suppress("unused") /* for public usage */
    fun wtf(e: Throwable) {
        Log.wtf(TAG, e)
    }

    @Suppress("unused") /* for public usage */
    fun wtf(msg: () -> String, e: Throwable) {
        Log.wtf(TAG, msg.invoke(), e)
    }

    @Suppress("unused") /* for public usage */
    fun error(msg: () -> String) {
        Log.e(TAG, msg.invoke())
    }

    @Suppress("unused") /* for public usage */
    fun error(msg: () -> String, e: Throwable) {
        Log.e(TAG, msg.invoke(), e)
    }

    @Suppress("unused") /* for public usage */
    fun warning(msg: () -> String) {
        Log.w(TAG, msg.invoke())
    }

    @Suppress("unused") /* for public usage */
    fun warning(e: Throwable) {
        Log.w(TAG, e)
    }

    @Suppress("unused") /* for public usage */
    fun warning(msg: () -> String, e: Throwable) {
        Log.w(TAG, msg.invoke(), e)
    }

    @Suppress("unused") /* for public usage */
    fun info(msg: () -> String) {
        Log.i(TAG, msg.invoke())
    }

    @Suppress("unused") /* for public usage */
    fun info(msg: () -> String, e: Throwable) {
        Log.i(TAG, msg.invoke(), e)
    }

    @Suppress("unused") /* for public usage */
    fun debug(tag: () -> String, msg: () -> String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag.invoke(), msg.invoke())
        }
    }

    @Suppress("unused") /* for public usage */
    fun debug(msg: () -> String) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, msg.invoke())
        }
    }

    @Suppress("unused") /* for public usage */
    fun debug(msg: () -> String, e: Throwable) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, msg.invoke(), e)
        }
    }

    @Suppress("unused") /* for public usage */
    fun verbose(msg: () -> String) {
        Log.v(TAG, msg.invoke())
    }

    @Suppress("unused") /* for public usage */
    fun verbose(msg: () -> String, e: Throwable) {
        Log.v(TAG, msg.invoke(), e)
    }
}
