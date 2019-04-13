package com.artemkaxboy.android.autoredialce;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {
    final Context mContext;

    public Settings(Context context) {
        mContext = context;
    }

    private static final String SIM_ID = "sim_id";
    static final int SIM_ID_DEF = -1;
    static void putSimId( Context context, int value ) { putP( context, SIM_ID, value ); }
    static int getSimId( Context context ) { return getP( context, SIM_ID, SIM_ID_DEF ); }

    @SuppressWarnings("unused")
    public static void putP( Context context, String key, boolean value ) {
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        editor.putBoolean( key, value );
        editor.commit();
    }

    public static void putP( Context context, String key, int value ) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void putP( Context context, String key, long value ) {
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static void putP( Context context, String key, String value ) {
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        editor.putString(key, value);
        editor.commit();
    }

    @SuppressWarnings("unused")
    public static boolean getP( Context context, String key, boolean defValue ) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            defValue = sp.getBoolean( key, defValue );
            return defValue;
        } catch( Exception e1 ) {
            try {
                String dv = Boolean.toString( defValue );
                return Boolean.valueOf( sp.getString( key, dv ));
            } catch( Exception e2 ) {
                try {
                    float dv = 0;
                    if( defValue ) dv = 1;
                    dv = sp.getFloat( key, dv );
                    return !(dv == 0);
                } catch( Exception e3 ) {
                    try {
                        int dv = 0;
                        if( defValue ) dv = 1;
                        dv = sp.getInt( key, dv );
                        return !( dv == 0 );
                    } catch( Exception e4 ) {
                        try {
                            long dv = 0;
                            if( defValue ) dv = 1;
                            dv = sp.getLong( key, dv );
                            return !( dv==0 );
                        } catch( Exception ignored ) {}
                    }
                }
            }
        }
        return defValue;
    }

    public static int getP( Context context, String key, int defValue ) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( context );
        try {
            defValue = sp.getInt(key, defValue);
            return defValue;
        } catch( Exception e1 ) {
            try {
                String dv = Integer.toString( defValue );
                return Math.round(Float.valueOf(sp.getString(key, dv)));
            } catch( Exception e2 ) {
                try {
                    boolean dv = true;
                    if( defValue == 0 ) dv = false;
                    dv = sp.getBoolean(key, dv);
                    if( dv ) return 1;
                    return 0;
                } catch( Exception e3 ) {
                    try {
                        return Math.round( sp.getFloat( key, defValue ));
                    } catch( Exception e4 ) {
                        try {
                            return (int)sp.getLong( key, defValue );
                        } catch( Exception ignored ) {}
                    }
                }
            }
        }
        return 0;
    }

    public static long getP( Context context, String key, long defValue ) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( context );
        try {
            defValue = sp.getLong(key, defValue);
            return defValue;
        } catch( Exception e1 ) {
            try {
                String dv = Long.toString( defValue );
                return Math.round(Float.valueOf(sp.getString(key, dv)));
            } catch( Exception e2 ) {
                try {
                    boolean dv = true;
                    if( defValue == 0 ) dv = false;
                    dv = sp.getBoolean(key, dv);
                    if( dv ) return 1;
                    return 0;
                } catch( Exception e3 ) {
                    try {
                        return Math.round( sp.getFloat( key, defValue ));
                    } catch( Exception e4 ) {
                        try {
                            return sp.getInt( key, (int)defValue );
                        } catch( Exception ignore ) {}
                    }
                }
            }
        }
        return defValue;
    }

    public static String getP( Context context, String key, String defValue ) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( context );
        try {
            defValue = sp.getString(key, defValue);
            return defValue;
        } catch( Exception e1 ) {
            try {
                boolean dv = Boolean.valueOf(defValue);
                return Boolean.toString(sp.getBoolean(key, dv));
            } catch( Exception e2 ) {
                try {
                    float dv = Float.valueOf( defValue );
                    return Float.toString( sp.getFloat( key, dv ));
                } catch( Exception e3 ) {
                    try {
                        int dv = Integer.valueOf( defValue );
                        return Integer.toString(sp.getInt(key, dv));
                    } catch( Exception e4 ) {
                        try {
                            long dv = Long.valueOf( defValue );
                            return Long.toString(sp.getLong(key, dv));
                        } catch( Exception ignore ) {}
                    }
                }
            }
        }
        return defValue;
    }
}
