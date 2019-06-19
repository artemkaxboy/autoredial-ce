package com.artemkaxboy.android.autoredialce.ui.fragments

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.transition.AutoTransition
import com.artemkaxboy.android.autoredialce.ui.activities.MainActivity
import com.artemkaxboy.android.autoredialce.ui.preferences.EditNumberDialog
import com.artemkaxboy.android.autoredialce.ui.preferences.EditIntegerPreference

private const val DIALOG_FRAGMENT_TAG = "androidx.preference.PreferenceFragment.DIALOG"

abstract class TitledFragment : PreferenceFragmentCompat() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = AutoTransition()
        reenterTransition = AutoTransition()
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