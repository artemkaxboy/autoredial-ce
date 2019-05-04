package com.artemkaxboy.android.autoredialce.ui.fragments

import android.os.Bundle
import com.artemkaxboy.android.autoredialce.R

class AutoredialPrefx : TitledFragment() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefx_autoredial, rootKey)
        setDisplayHomeAsUpEnabled(true)
    }
}