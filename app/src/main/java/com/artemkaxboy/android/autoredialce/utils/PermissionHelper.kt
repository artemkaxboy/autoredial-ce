package com.artemkaxboy.android.autoredialce.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.artemkaxboy.android.autoredialce.R

object PermissionHelper {
    private const val request_code = 12

    private var rawPermissions: List<String>? = null
    private val permissions: List<String>
        @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
        get() {
            if (rawPermissions == null) {
                rawPermissions = listOf(Manifest.permission.READ_CALL_LOG,
                        Manifest.permission.READ_CONTACTS,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.GET_ACCOUNTS,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.PROCESS_OUTGOING_CALLS)
            }
            return rawPermissions ?: throw AssertionError("Internal error")
        }

    /**
     * Checks if API level requires permissions for actions.
     */
    private fun isApplicable(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    /**
     * Asks for permissions if they have not been granted yet.
     */
    fun askIfNeeded(activity: AppCompatActivity) {
        if (!isApplicable()) {
            return
        }

        getNeeded(activity)
                .takeIf { it.isNotEmpty() }
                ?.let { activity.requestPermissions(it, request_code) }
    }

    /**
     * Returns a list of permissions to be asked for.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun getNeeded(activity: AppCompatActivity): Array<String> {
        return permissions
                .filter { s ->
                    activity.checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED
                }
                .toTypedArray()
    }

    /**
     * Checks user's response for permissions request.
     */
    fun checkResults(activity: AppCompatActivity, requestCode: Int, grantResults: IntArray) {
        if (requestCode != request_code) {
            return
        }

        if (grantResults.any { it != PackageManager.PERMISSION_GRANTED }) {
            complain(activity)
        }
    }

    /**
     * Shows message to inform user that some permissions have not been granted.
     */
    fun complain(context: Context) {
        Alert.alert(context, R.string.permissions_error, R.string.permissions_error_desc)
    }
}
