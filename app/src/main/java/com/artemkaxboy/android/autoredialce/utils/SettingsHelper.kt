package com.artemkaxboy.android.autoredialce.utils

import androidx.appcompat.app.AppCompatActivity

object SettingsHelper {
    private lateinit var activity: AppCompatActivity

    fun create(activity: AppCompatActivity) {
        this.activity = activity
    }


}