package com.artemkaxboy.android.autoredialce.ui.fragments

import android.os.Bundle
import com.artemkaxboy.android.autoredialce.R

class SettingsFragment : TitledFragment() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefx_root, rootKey)
    }

    // to call setDisplayHomeAsUpEnabled when user returns from sub-fragments
    override fun onStart() {
        super.onStart()
        setDisplayHomeAsUpEnabled(false)
    }
}
