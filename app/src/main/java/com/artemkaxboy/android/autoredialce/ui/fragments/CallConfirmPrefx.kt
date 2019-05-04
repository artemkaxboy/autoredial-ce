package com.artemkaxboy.android.autoredialce.ui.fragments

import android.os.Bundle
import com.artemkaxboy.android.autoredialce.R

class CallConfirmPrefx : TitledFragment() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefx_confirmation, rootKey)
        setDisplayHomeAsUpEnabled(true)
    }
}