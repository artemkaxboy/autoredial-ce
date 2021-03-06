package com.artemkaxboy.android.autoredialce.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.artemkaxboy.android.autoredialce.R
import com.artemkaxboy.android.autoredialce.ui.fragments.SettingsFragment
import com.artemkaxboy.android.autoredialce.utils.FirstRunHelper
import com.artemkaxboy.android.autoredialce.utils.PermissionHelper

private const val TITLE_TAG = "settingsActivityTitle"

// source https://github.com/googlesamples/android-preferences

class MainActivity : AppCompatActivity(),
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.settings,
                    SettingsFragment()
                )
                .commit()
            onStarted()
        } else {
            title = savedInstanceState.getCharSequence(TITLE_TAG)
        }
        supportFragmentManager.addOnBackStackChangedListener {
            if (supportFragmentManager.backStackEntryCount == 0) {
                setTitle(R.string.app_name)
            }
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onRequestPermissionsResult
            (requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        PermissionHelper.checkResults(this, requestCode, grantResults)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save current activity title so we can set it again after a configuration change
        outState.putCharSequence(TITLE_TAG, title)
    }

    override fun onSupportNavigateUp(): Boolean {
        if (supportFragmentManager.popBackStackImmediate()) {
            return true
        }
        return super.onSupportNavigateUp()
    }

    override fun onPreferenceStartFragment
            (caller: PreferenceFragmentCompat, pref: Preference): Boolean {
        // Instantiate the new Fragment
        val fragment =
                supportFragmentManager.fragmentFactory.instantiate(classLoader, pref.fragment)
            .apply {
                arguments = pref.extras
                setTargetFragment(caller, 0)
            }
        // Replace the existing Fragment with the new Fragment
        supportFragmentManager.beginTransaction()
            .replace(R.id.settings, fragment)
            .addToBackStack(null)
            .commit()
        title = pref.title
        return true
    }

    private fun onStarted() {
        PermissionHelper.askIfNeeded(this)
        FirstRunHelper.showIfNeeded(this)
    }

    fun setDisplayHomeAsUpEnabled(visible: Boolean) {
        supportActionBar?.setDisplayHomeAsUpEnabled(visible)
    }
}
