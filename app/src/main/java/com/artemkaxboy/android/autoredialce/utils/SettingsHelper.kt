package com.artemkaxboy.android.autoredialce.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

object SettingsHelper {

    private const val TAG = "SettingsHelper"

    const val FIRST_RUN = "first_run"
    private const val FIRST_RUN_DEF = true

    const val REDIALING = "redialing"
    private const val REDIALING_DEF = false

    const val SERVICES_ENABLED = "services_enabled"
    private const val SERVICES_ENABLED_DEF = true

    private val defaults = mapOf<String, Any>(FIRST_RUN to FIRST_RUN_DEF,
            REDIALING to REDIALING_DEF, SERVICES_ENABLED to SERVICES_ENABLED_DEF)

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
}
