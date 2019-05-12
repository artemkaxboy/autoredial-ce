package com.artemkaxboy.android.autoredialce.ui.fragments

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.SwitchPreferenceCompat
import com.artemkaxboy.android.autoredialce.BuildConfig
import com.artemkaxboy.android.autoredialce.R
import com.artemkaxboy.android.autoredialce.ReceiverCommand
import com.artemkaxboy.android.autoredialce.utils.SettingsHelper

class SettingsFragment : TitledFragment() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefx_root, rootKey)

        findPreference<SwitchPreferenceCompat>(getString(R.string.services_enabled_key))?.let {
            it.setOnPreferenceChangeListener { _, _ ->
                context!!.sendBroadcast(
                        ReceiverCommand.getIntent(context, ReceiverCommand.ACTION_UPDATE_STATUS))
                true
            }
        }

        findPreference<Preference>(getString(R.string.version_key))?.let {
            it.title = getString(
                    R.string.version,
                    BuildConfig.VERSION_NAME
            )
            it.summary = null
        }

        findPreference<Preference>(getString(R.string.rate_key))?.isVisible = false

        findPreference<Preference>(getString(R.string.reset_key))?.let { it ->
            it.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                context!!.sendBroadcast(
                        ReceiverCommand.getIntent(context, ReceiverCommand.ACTION_REDIALING_STOP))
                it.isVisible = false
                true
            }
        }

        context?.let {
            findPreference<Preference>(getString(R.string.reset_key))?.isVisible =
                    SettingsHelper.getBoolean(it, SettingsHelper.REDIALING)
        }
    }

    // to call setDisplayHomeAsUpEnabled when user returns from sub-fragments
    override fun onStart() {
        super.onStart()
        setDisplayHomeAsUpEnabled(false)
    }
}