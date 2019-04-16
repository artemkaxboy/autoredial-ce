package com.artemkaxboy.android.autoredialce.utils;

import android.util.Log;

@SuppressWarnings("unused")
public class Logger {

  public static void wtf(String msg) {
    Log.wtf(Cons.TAG, msg);
  }

  public static void wtf(Throwable e) {
    Log.wtf(Cons.TAG, e);
  }

  public static void wtf(String msg, Throwable e) {
    Log.wtf(Cons.TAG, msg, e);
  }

  public static void error(String msg) {
    Log.e(Cons.TAG, msg);
  }

  public static void error(String msg, Throwable e) {
    Log.e(Cons.TAG, msg, e);
  }

  public static void warning(String msg) {
    Log.w(Cons.TAG, msg);
  }

  public static void warning(Throwable e) {
    Log.w(Cons.TAG, e);
  }

  public static void warning(String msg, Throwable e) {
    Log.w(Cons.TAG, msg, e);
  }

  public static void info(String msg) {
    Log.i(Cons.TAG, msg);
  }

  public static void info(String msg, Throwable e) {
    Log.i(Cons.TAG, msg, e);
  }

  public static void verbose(String msg) {
    Log.v(Cons.TAG, msg);
  }

  public static void verbose(String msg, Throwable e) {
    Log.v(Cons.TAG, msg, e);
  }

  public static void debug(String msg) {
    Log.d(Cons.TAG, msg);
  }

  public static void debug(String msg, Throwable e) {
    Log.d(Cons.TAG, msg, e);
  }
}
