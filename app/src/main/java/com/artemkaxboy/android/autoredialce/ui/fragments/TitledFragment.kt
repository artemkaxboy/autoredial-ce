package com.artemkaxboy.android.autoredialce.ui.fragments

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import androidx.transition.AutoTransition
import com.artemkaxboy.android.autoredialce.BuildConfig
import com.artemkaxboy.android.autoredialce.R
import com.artemkaxboy.android.autoredialce.ReceiverCommand
import com.artemkaxboy.android.autoredialce.ui.activities.MainActivity
import com.artemkaxboy.android.autoredialce.ui.preferences.EditNumberDialog
import com.artemkaxboy.android.autoredialce.ui.preferences.EditIntegerPreference
import com.artemkaxboy.android.autoredialce.utils.SettingsHelper

private const val DIALOG_FRAGMENT_TAG = "androidx.preference.PreferenceFragment.DIALOG"

abstract class TitledFragment : PreferenceFragmentCompat() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = AutoTransition()
        reenterTransition = AutoTransition()

        findPreference<SwitchPreferenceCompat>(getString(R.string.autoredial_enabled_key))?.let {
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

    fun setDisplayHomeAsUpEnabled(visible: Boolean) {
        activity
            ?.takeIf { it is MainActivity }
            ?.let { (activity as MainActivity).setDisplayHomeAsUpEnabled(visible) }
    }

    /**
     * Shows custom FragmentDialog for custom Preferences
     */
    override fun onDisplayPreferenceDialog(preference: Preference) {
        // check if dialog is already showing
        if (fragmentManager!!.findFragmentByTag(DIALOG_FRAGMENT_TAG) != null) {
            return
        }

        if (preference is EditIntegerPreference) {
            val f = EditNumberDialog.newInstance(preference.key)
            f.setTargetFragment(this, 0)
            f.show(fragmentManager!!, DIALOG_FRAGMENT_TAG)
        } else {
            super.onDisplayPreferenceDialog(preference)
        }
    }
}