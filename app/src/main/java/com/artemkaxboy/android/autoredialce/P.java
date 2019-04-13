package com.artemkaxboy.android.autoredialce;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class P {
	private static final String SPEAKER = "speaker";
	private static final String SPEAKER_ALWAYS = "speakerAlways";
	private static final String MASTER_CALL = "masterCall";
	private static final String ENABLED = "service_on";
	private static final String CURRENT_ATTEMPT = "curCount";
	private static final String LAST_ATTEMPT = "lastCount";
	private static final String MIN_DURATION = "minDuration";
	private static final String REDIALING = "redialing";
	private static final String NUMBER = "redialing_number";
	private static final String PAUSE = "pause";
	private static final String MISSED_ENABLED = "missedCall";
	private static final String MISSED_LIST = "missedList";
	private static final String CONFIRM_OUTBOUND = "confirmation_outbound";
	private static final String CONFIRM_EXCLUDE_USSD = "confirmation_exclude_ussd";
	private static final String CONFIRM_EXCLUDE_UNKNOWN = "confirmation_exclude_unknown";
	//private static final String CONFIRM_INBOUND = "confirmation_inbound";
	private static final String CONFIRM_IS_GOT = "confirmation_is_got";
	private static final String CONFIRM_SENSOR = "confirmation_sensor";
	private static final String CONFIRM_HEADSET = "confirmation_headset";
	private static final String STATUS_SHOW = "showCancel";
	private static final String BLUETOOTH_CONNECTED = "bluetooth_connected";
	private static final String AUTOREDIAL_ON = "autoredial_on";
	private static final String ENABLED_BACKUP = "enabled_backup";
	private static final String AUTOREDIAL_BACKUP = "autoredial_backup";
	private static final String WIDGET_REDIALING = "widget_redialing";
	private static final String REDIAL_WOUT_PROMPT = "woutPrompt";
	private static final String OUT_TIME = "out_time";
    private static final String IGNORE_LAST = "ignore_last";
    private static final String SPEAKER_TIME = "speaker_time";
    private static final String LAST_IDLE = "last_idle";

    //private static final String
	//TODO change defaults
	public static boolean confirmOut( Context context ) { return getP( context, CONFIRM_OUTBOUND, false ); }
	public static boolean confirmExceptUssd( Context context ) { return getP( context, CONFIRM_EXCLUDE_USSD, true ); }
	public static boolean confirmExceptUnknown( Context context ) { return getP( context, CONFIRM_EXCLUDE_UNKNOWN, false ); }
	//public static boolean confirmInbound( Context context ) { return getP( context, CONFIRM_INBOUND, false ); }
	public static boolean confirmIsGot( Context context ) { return getP( context, CONFIRM_IS_GOT, false ); }
	public static void confirmIsGot( Context context, boolean confirmIsGot ) { putP( context, CONFIRM_IS_GOT, confirmIsGot ); }
	public static boolean confirmSensor( Context context ) { return getP( context, CONFIRM_SENSOR, true ); }
	public static boolean confirmHeadset( Context context ) { return getP( context, CONFIRM_HEADSET, true ); }
	public static boolean autoRedialOn( Context context ) { return getP( context, AUTOREDIAL_ON, true ); }
	public static void autoRedialOn( Context context, boolean enabled ) { putP( context, AUTOREDIAL_ON, enabled ); }
	public static boolean enabledBackup( Context context ) { return getP( context, ENABLED_BACKUP, true ); }
	public static void enabledBackup( Context context, boolean enabled ) { putP( context, ENABLED_BACKUP, enabled ); }
	public static boolean autoredialBackup( Context context ) { return getP( context, AUTOREDIAL_BACKUP, true ); }
	public static void autoredialBackup( Context context, boolean enabled ) { putP( context, AUTOREDIAL_BACKUP, enabled ); }
	public static boolean widgetRedialing( Context context ) {
		return getP( context, WIDGET_REDIALING, false ) & !getP( context, "test", false );
	}
	public static void widgetRedialing( Context context, boolean enabled ) { putP( context, WIDGET_REDIALING, enabled ); }
	public static boolean ignoreLast( Context context ) { return getP( context, IGNORE_LAST, false ); }
	public static void ignoreLast( Context context, boolean enabled ) { putP( context, IGNORE_LAST, enabled ); }
	
	
	
	public static boolean speaker( Context context ) { return getP( context, SPEAKER, false ); }
	public static boolean redialWoutPrompt( Context context ) { return getP( context, REDIAL_WOUT_PROMPT, false ); }
	//TODO speaker always
	public static boolean speakerAlways( Context context ) { return getP( context, SPEAKER_ALWAYS, false ); }
	public static void speakerAlways( Context context, boolean speakerAlways ) { putP( context, SPEAKER_ALWAYS, speakerAlways ); }
	public static boolean masterCall( Context context ) { return getP( context, MASTER_CALL, false ); }
	public static void masterCall( Context context, boolean masterCall ) { putP( context, MASTER_CALL, masterCall ); }
	public static boolean enabled( Context context ) {
		return getP( context, ENABLED, true ) & !getP( context, "test", false );
	}
	public static void enabled( Context context, boolean enabled ) { putP( context, ENABLED, enabled ); }
	public static boolean redialing( Context context ) { return getP( context, REDIALING, false ); }
	public static void redialing( Context context, boolean redialing ) { putP( context, REDIALING, redialing ); }
	public static boolean missedList( Context context ) { return getP( context, MISSED_LIST, true ); }
	public static boolean missedEnabled( Context context ) { return getP( context, MISSED_ENABLED, true ); }
	public static boolean status( Context context ) { return getP( context, STATUS_SHOW, true ); }
	public static boolean bluetoothConnected( Context context ) { return getP( context, BLUETOOTH_CONNECTED, false ); }
	public static void bluetoothConnected( Context context, boolean connected ) { putP( context, BLUETOOTH_CONNECTED, connected ); }
	
	
	public static int currentAttempt( Context context ) { return getP( context, CURRENT_ATTEMPT, 0 ); }
	public static void currentAttempt( Context context, int attempt ) { putP( context, CURRENT_ATTEMPT, attempt ); }
	public static int lastAttempt( Context context ) { return getP( context, LAST_ATTEMPT, 5 ); }
	//public static void lastAttempt( Context context, int attempt ) { putP( context, LAST_ATTEMPT, attempt ); }
	public static int minDuration( Context context ) { return getP( context, MIN_DURATION, 5 ); }
	public static int pause( Context context ) { return getP( context, PAUSE, 3 ); }

	public static String number( Context context ) { return getP( context, NUMBER, null ); }
	public static void number( Context context, String number ) { putP( context, NUMBER, number ); }

    public static void outTime( Context context, long time ) { putP( context, OUT_TIME, time ); }
    public static long outTime( Context context ) { return getP( context, OUT_TIME, 0L ); }

    public static int speakerTime( Context context ) { return getP( context, SPEAKER_TIME, 800 ); }
//    public static void speakerTime( Context context, int time ) { putP( context, SPEAKER_TIME, time ); }

    public static long lastIdle( Context context ) { return getP( context, LAST_IDLE, 0L ); }
    public static void lastIdle( Context context, long time ) { putP( context, LAST_IDLE, time ); }


    public static boolean getP( Context context, String key, boolean defValue ) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( context );
        try {
            defValue = sp.getBoolean( key, defValue );
//			if( BuildConfig.DEBUG ) Log.v( context.getPackageName(), key + " = " + defValue );
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
                    return dv != 0;
                } catch( Exception e3 ) {
                    try {
                        int dv = 0;
                        if( defValue ) dv = 1;
                        dv = sp.getInt( key, dv );
                        return dv != 0;
                    } catch( Exception e4 ) {
                        try {
                            long dv = 0;
                            if( defValue ) dv = 1;
                            dv = sp.getLong( key, dv );
                            return dv != 0;
                        } catch( Exception ignored ) {}
                    }
                }
            }
        }
        return false;
    }

    public static float getP( Context context, String key, float defValue ) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( context );
        try {
            defValue = sp.getFloat( key, defValue );
            return defValue;
        } catch( Exception e1 ) {
            try {
                String dv = Float.toString( defValue );
                return Float.valueOf( sp.getString( key, dv ));
            } catch( Exception e2 ) {
                try {
                    boolean dv = true;
                    if( defValue == 0 ) dv = false;
                    dv = sp.getBoolean( key, dv );
                    if( dv ) return 1;
                    return 0;
                } catch( Exception e3 ) {
                    try {
                        int dv = Math.round( defValue );
                        return sp.getInt( key, dv );
                    } catch( Exception e4 ) {
                        try {
                            long dv = Math.round( defValue );
                            return sp.getLong( key, dv );
                        } catch( Exception ignored ) {}
                    }
                }
            }
        }
        return 0;
    }

    public static int getP( Context context, String key, int defValue ) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( context );
        try {
            defValue = sp.getInt( key, defValue );
            return defValue;
        } catch( Exception e1 ) {
            try {
                String dv = Integer.toString( defValue );
                return Math.round( Float.valueOf( sp.getString( key, dv )));
            } catch( Exception e2 ) {
                try {
                    boolean dv = true;
                    if( defValue == 0 ) dv = false;
                    dv = sp.getBoolean( key, dv );
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
            defValue = sp.getLong( key, defValue );
            return defValue;
        } catch( Exception e1 ) {
            try {
                String dv = Long.toString( defValue );
                return Math.round( Float.valueOf( sp.getString( key, dv )));
            } catch( Exception e2 ) {
                try {
                    boolean dv = true;
                    if( defValue == 0 ) dv = false;
                    dv = sp.getBoolean( key, dv );
                    if( dv ) return 1;
                    return 0;
                } catch( Exception e3 ) {
                    try {
                        return Math.round( sp.getFloat( key, defValue ));
                    } catch( Exception e4 ) {
                        try {
                            return sp.getInt( key, (int)defValue );
                        } catch( Exception ignored ) {}
                    }
                }
            }
        }
        return 0;
    }
    public static double getP( Context context, String key, double defValue ) {
        return Double.longBitsToDouble( getP( context, key, Double.doubleToLongBits( defValue )));
    }

    public static String getP( Context context, String key, String defValue ) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences( context );
        try {
            defValue = sp.getString( key, defValue );
            return defValue;
        } catch( Exception e1 ) {
            try {
                boolean dv = Boolean.valueOf( defValue );
                return Boolean.toString( sp.getBoolean( key, dv ));
            } catch( Exception e2 ) {
                try {
                    float dv = Float.valueOf( defValue );
                    return Float.toString( sp.getFloat( key, dv ));
                } catch( Exception e3 ) {
                    try {
                        int dv = Integer.valueOf( defValue );
                        return Integer.toString( sp.getInt( key, dv ));
                    } catch( Exception e4 ) {
                        try {
                            long dv = Long.valueOf( defValue );
                            return Long.toString( sp.getLong( key, dv ));
                        } catch( Exception ignored ) {}
                    }
                }
            }
        }
        return null;
    }

    public static void putP( Context context, String key, boolean value ) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences( context ).edit();
        editor.putBoolean( key, value );
        editor.commit();
    }

    public static void putP( Context context, String key, float value ) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences( context ).edit();
        editor.putFloat( key, value );
        editor.commit();
    }

    public static void putP( Context context, String key, int value ) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences( context ).edit();
        editor.putInt( key, value );
        editor.commit();
    }

    public static void putP( Context context, String key, long value ) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences( context ).edit();
        editor.putLong( key, value );
        editor.commit();
    }
    public static void putP( Context context, String key, double value ) {
        putP( context, key, Double.doubleToLongBits( value ));
    }

    public static void putP( Context context, String key, String value ) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString( key, value );
        editor.commit();
    }
}
