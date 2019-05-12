package com.artemkaxboy.android.autoredialce;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

@SuppressWarnings("WeakerAccess")
public class P {

  private static final String SPEAKER = "speaker";
  private static final String SPEAKER_ALWAYS = "speakerAlways";
  private static final String MIN_DURATION = "minDuration";
  private static final String MISSED_ENABLED = "missedCall";
  private static final String MISSED_LIST = "missedList";
  private static final String CONFIRM_OUTBOUND = "confirmation_outbound";
  private static final String CONFIRM_EXCLUDE_USSD = "confirmation_exclude_ussd";
  private static final String CONFIRM_EXCLUDE_UNKNOWN = "confirmation_exclude_unknown";
  //private static final String CONFIRM_INBOUND = "confirmation_inbound";
  private static final String CONFIRM_SENSOR = "confirmation_sensor";
  private static final String CONFIRM_HEADSET = "confirmation_headset";
  private static final String STATUS_SHOW = "showCancel";
  private static final String BLUETOOTH_CONNECTED = "bluetooth_connected";
  private static final String OUT_TIME = "out_time";
  private static final String SPEAKER_TIME = "speaker_time";
  private static final String LAST_IDLE = "last_idle";

  //private static final String
  //TODO change defaults
  public static boolean confirmOut(Context context) {
    return getP(context, CONFIRM_OUTBOUND, false);
  }

  public static boolean confirmExceptUssd(Context context) {
    return getP(context, CONFIRM_EXCLUDE_USSD, true);
  }

  public static boolean confirmExceptUnknown(Context context) {
    return getP(context, CONFIRM_EXCLUDE_UNKNOWN, false);
  }

  public static boolean confirmSensor(Context context) {
    return getP(context, CONFIRM_SENSOR, true);
  }

  public static boolean confirmHeadset(Context context) {
    return getP(context, CONFIRM_HEADSET, true);
  }

  public static boolean speaker(Context context) {
    return getP(context, SPEAKER, false);
  }

  //TODO speaker always
  public static boolean speakerAlways(Context context) {
    return getP(context, SPEAKER_ALWAYS, false);
  }

  public static void speakerAlways(Context context, boolean speakerAlways) {
    putP(context, SPEAKER_ALWAYS, speakerAlways);
  }

  public static boolean missedList(Context context) {
    return getP(context, MISSED_LIST, true);
  }

  public static boolean missedEnabled(Context context) {
    return getP(context, MISSED_ENABLED, true);
  }

  public static boolean status(Context context) {
    return getP(context, STATUS_SHOW, true);
  }

  public static boolean bluetoothConnected(Context context) {
    return getP(context, BLUETOOTH_CONNECTED, false);
  }

  public static void bluetoothConnected(Context context, boolean connected) {
    putP(context, BLUETOOTH_CONNECTED, connected);
  }

  public static int minDuration(Context context) {
    return getP(context, MIN_DURATION, 5);
  }

  public static void outTime(Context context, long time) {
    putP(context, OUT_TIME, time);
  }

  public static long outTime(Context context) {
    return getP(context, OUT_TIME, 0L);
  }

  public static int speakerTime(Context context) {
    return getP(context, SPEAKER_TIME, 800);
  }
  //public static void speakerTime( Context context, int time ) {
  // putP( context, SPEAKER_TIME, time ); }

  public static long lastIdle(Context context) {
    return getP(context, LAST_IDLE, 0L);
  }

  public static void lastIdle(Context context, long time) {
    putP(context, LAST_IDLE, time);
  }


  static boolean getP(Context context, String key, boolean defValue) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    try {
      defValue = sp.getBoolean(key, defValue);
      //if( BuildConfig.DEBUG ) Log.v( context.getPackageName(), key + " = " + defValue );
      return defValue;
    } catch (Exception e1) {
      try {
        String dv = Boolean.toString(defValue);
        return Boolean.valueOf(sp.getString(key, dv));
      } catch (Exception e2) {
        try {
          float dv = 0;
          if (defValue) {
            dv = 1;
          }
          dv = sp.getFloat(key, dv);
          return dv != 0;
        } catch (Exception e3) {
          try {
            int dv = 0;
            if (defValue) {
              dv = 1;
            }
            dv = sp.getInt(key, dv);
            return dv != 0;
          } catch (Exception e4) {
            try {
              long dv = 0;
              if (defValue) {
                dv = 1;
              }
              dv = sp.getLong(key, dv);
              return dv != 0;
            } catch (Exception ignored) {
              return defValue;
            }
          }
        }
      }
    }
  }

  /**
   * Gets preference value or returns default one.
   *
   * @param context app context
   * @param key preference key
   * @param defValue default value
   * @return preference value if it was found and correct, defValue otherwise
   */
  public static int getP(Context context, String key, int defValue) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    try {
      defValue = sp.getInt(key, defValue);
      return defValue;
    } catch (Exception e1) {
      try {
        String dv = Integer.toString(defValue);
        return Math.round(Float.valueOf(sp.getString(key, dv)));
      } catch (Exception e2) {
        try {
          boolean dv = true;
          if (defValue == 0) {
            dv = false;
          }
          dv = sp.getBoolean(key, dv);
          if (dv) {
            return 1;
          }
          return 0;
        } catch (Exception e3) {
          try {
            return Math.round(sp.getFloat(key, defValue));
          } catch (Exception e4) {
            try {
              return (int) sp.getLong(key, defValue);
            } catch (Exception ignored) {
              return 0;
            }
          }
        }
      }
    }
  }

  static long getP(Context context, String key, long defValue) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    try {
      defValue = sp.getLong(key, defValue);
      return defValue;
    } catch (Exception e1) {
      try {
        String dv = Long.toString(defValue);
        return Math.round(Float.valueOf(sp.getString(key, dv)));
      } catch (Exception e2) {
        try {
          boolean dv = true;
          if (defValue == 0) {
            dv = false;
          }
          dv = sp.getBoolean(key, dv);
          if (dv) {
            return 1;
          }
          return 0;
        } catch (Exception e3) {
          try {
            return Math.round(sp.getFloat(key, defValue));
          } catch (Exception e4) {
            try {
              return sp.getInt(key, (int) defValue);
            } catch (Exception ignored) {
              return defValue;
            }
          }
        }
      }
    }
  }

  public static double getP(Context context, String key, double defValue) {
    return Double.longBitsToDouble(getP(context, key, Double.doubleToLongBits(defValue)));
  }

  /**
   * Gets preference value or returns default one.
   *
   * @param context app context
   * @param key preference key
   * @param defValue default value
   * @return preference value if it was found and correct, defValue otherwise
   */
  public static String getP(Context context, String key, String defValue) {
    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
    try {
      defValue = sp.getString(key, defValue);
      return defValue;
    } catch (Exception e1) {
      try {
        boolean dv = Boolean.valueOf(defValue);
        return Boolean.toString(sp.getBoolean(key, dv));
      } catch (Exception e2) {
        try {
          float dv = Float.valueOf(defValue);
          return Float.toString(sp.getFloat(key, dv));
        } catch (Exception e3) {
          try {
            int dv = Integer.valueOf(defValue);
            return Integer.toString(sp.getInt(key, dv));
          } catch (Exception e4) {
            try {
              long dv = Long.valueOf(defValue);
              return Long.toString(sp.getLong(key, dv));
            } catch (Exception ignored) {
              return defValue;
            }
          }
        }
      }
    }
  }

  static void putP(Context context, String key, boolean value) {
    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
    editor.putBoolean(key, value);
    editor.commit();
  }

  /**
   * Saves preference value.
   *
   * @param context app context
   * @param key preference key
   * @param value value to save
   */
  public static void putP(Context context, String key, int value) {
    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
    editor.putInt(key, value);
    editor.commit();
  }

  /**
   * Saves preference value.
   *
   * @param context app context
   * @param key preference key
   * @param value value to save
   */
  public static void putP(Context context, String key, long value) {
    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
    editor.putLong(key, value);
    editor.commit();
  }

  /**
   * Saves preference value.
   *
   * @param context app context
   * @param key preference key
   * @param value value to save
   */
  public static void putP(Context context, String key, String value) {
    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
    editor.putString(key, value);
    editor.commit();
  }
}
