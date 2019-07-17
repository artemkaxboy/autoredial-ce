package com.artemkaxboy.android.autoredialce.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import java.lang.ClassCastException

object SettingsHelper {

    private const val TAG = "SettingsHelper"

    const val FIRST_RUN = "first_run"
    private const val FIRST_RUN_DEF = true

    const val REDIALING = "redialing"
    private const val REDIALING_DEF = false

    const val AUTOREDIAL_ENABLED = "autoredial_enabled"
    private const val AUTOREDIAL_ENABLED_DEF = true

    const val AUTOREDIAL_ENABLED_BACKUP = "autoredial_enabled_backup"
    private const val AUTOREDIAL_ENABLED_BACKUP_DEF = true

    const val NO_PROMPT_REDIAL = "no_prompt_redial"
    private const val NO_PROMPT_REDIAL_DEF = false

    const val MASTER_CALL = "master_call"
    private const val MASTER_CALL_DEF = false

    const val IGNORE_LAST = "ignore_last"
    private const val IGNORE_LAST_DEF = false

    const val WIDGET_REDIALING = "widget_redialing"
    private const val WIDGET_REDIALING_DEF = false

    const val CONFIRMATION_GOT = "confirmation_got"
    private const val CONFIRMATION_GOT_DEF = false

    const val REDIALING_CURRENT_ATTEMPT = "redialing_current_attempt"
    private const val REDIALING_CURRENT_ATTEMPT_DEF = 0

    const val REDIALING_ATTEMPTS_COUNT = "redialing_attempts_count"
    private const val REDIALING_ATTEMPTS_COUNT_DEF = 5

    const val REDIALING_PAUSE = "redialing_pause"
    private const val REDIALING_PAUSE_DEF = 3

    const val REDIALING_NUMBER = "redialing_number"
    private const val REDIALING_NUMBER_DEF = ""

    private val defaults = mapOf(
            FIRST_RUN to FIRST_RUN_DEF,
            REDIALING to REDIALING_DEF,
            AUTOREDIAL_ENABLED to AUTOREDIAL_ENABLED_DEF,
            AUTOREDIAL_ENABLED_BACKUP to AUTOREDIAL_ENABLED_BACKUP_DEF,
            NO_PROMPT_REDIAL to NO_PROMPT_REDIAL_DEF,
            MASTER_CALL to MASTER_CALL_DEF,
            IGNORE_LAST to IGNORE_LAST_DEF,
            WIDGET_REDIALING to WIDGET_REDIALING_DEF,
            CONFIRMATION_GOT to CONFIRMATION_GOT_DEF,

            REDIALING_CURRENT_ATTEMPT to REDIALING_CURRENT_ATTEMPT_DEF,
            REDIALING_ATTEMPTS_COUNT to REDIALING_ATTEMPTS_COUNT_DEF,
            REDIALING_PAUSE to REDIALING_PAUSE_DEF,

            REDIALING_NUMBER to REDIALING_NUMBER_DEF
            )

    private val values = HashMap<String, Any>()

    private lateinit var sp: SharedPreferences

    private fun getSP(context: Context): SharedPreferences {
        if (!::sp.isInitialized) {
            sp = PreferenceManager.getDefaultSharedPreferences(context)
        }
        return sp
    }

    fun getBoolean(context: Context, key: String): Boolean {
        if (!values.containsKey(key)) {
            values[key] = readBoolean(context, key)
        }
        return values[key] as Boolean
    }

    fun setBoolean(context: Context, key: String, value: Boolean) {
        Logger.debug({ TAG }, { "$key: $value" })
        values[key] = value
        getSP(context).edit().putBoolean(key, value).apply()
    }

    fun readBoolean(context: Context, key: String): Boolean {
        return try {
            getSP(context).getBoolean(key, defaults.getValue(key) as Boolean)
        } catch (e: Exception) {
            defaults.getValue(key) as Boolean
        }
    }

    fun getInt(context: Context, key: String): Int {
        if (!values.containsKey(key)) {
            values[key] = readInt(context, key)
        }
        return values[key] as Int
    }

    fun setInt(context: Context, key: String, value: Int) {
        Logger.debug({ TAG }, { "$key: $value" })
        values[key] = value
        getSP(context).edit().putInt(key, value).apply()
    }

    fun readInt(context: Context, key: String): Int {
        return try {
            try {
                getSP(context).getInt(key, defaults.getValue(key) as Int)
            } catch (e: ClassCastException) {
                getSP(context).getString(key, null)?.toInt() ?: defaults.getValue(key) as Int
            }
        } catch (e: Exception) {
            defaults.getValue(key) as Int
        }
    }

    fun getString(context: Context, key: String): String {
        if (!values.containsKey(key)) {
            values[key] = readString(context, key)
        }
        return values[key] as String
    }

    fun setString(context: Context, key: String, value: String) {
        Logger.debug({ TAG }, { "$key: $value" })
        values[key] = value
        getSP(context).edit().putString(key, value).apply()
    }

    private fun readString(context: Context, key: String): String {
        return try {
            getSP(context).getString(key, defaults.getValue(key) as String)
                    ?: defaults.getValue(key) as String
        } catch (e: Exception) {
            defaults.getValue(key) as String
        }
    }
}
