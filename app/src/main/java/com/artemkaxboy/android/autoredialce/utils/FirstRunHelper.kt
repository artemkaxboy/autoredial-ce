package com.artemkaxboy.android.autoredialce.utils

import android.content.Context
import com.artemkaxboy.android.autoredialce.R

object FirstRunHelper {
    /**
     * Checks if it is the first run of the app and shows main help alert if so.
     */
    fun showIfNeeded(context: Context) {
        if (SettingsHelper.getBoolean(context, SettingsHelper.FIRST_RUN)) {
            Alert.alert(context, R.string.remember, R.string.mainHelp)
            SettingsHelper.setBoolean(context, SettingsHelper.FIRST_RUN, false)
        }
    }
}
