package com.artemkaxboy.android.autoredialce.ui.fragments

import androidx.preference.PreferenceFragmentCompat
import com.artemkaxboy.android.autoredialce.ui.activities.MainActivity

abstract class TitledFragment : PreferenceFragmentCompat() {
    fun setDisplayHomeAsUpEnabled(visible: Boolean) {
        activity
            ?.takeIf { it is MainActivity }
            .let { (activity as MainActivity).setDisplayHomeAsUpEnabled(visible) }
    }
}