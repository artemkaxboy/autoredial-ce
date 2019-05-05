package com.artemkaxboy.android.autoredialce.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.preference.Preference
import com.artemkaxboy.android.autoredialce.BuildConfig
import com.artemkaxboy.android.autoredialce.R
import com.artemkaxboy.android.autoredialce.ReceiverCommand
import com.artemkaxboy.android.autoredialce.utils.SettingsHelper

class SettingsFragment : TitledFragment() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefx_root, rootKey)

        findPreference<Preference>(getString(R.string.version_key))?.let {
            it.title = getString(
                    R.string.version,
                    BuildConfig.VERSION_NAME
            )
            it.summary = null
        }

        findPreference<Preference>(getString(R.string.rate_key))?.isVisible = false

        val resetPreference = findPreference<Preference>(getString(R.string.reset_key))

        resetPreference?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            context!!.sendBroadcast(Intent(ReceiverCommand.ACTION_REDIALING_STOP))
            resetPreference?.isVisible = false
            true
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