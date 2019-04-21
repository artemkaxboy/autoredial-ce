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

    private lateinit var activity: AppCompatActivity
    private lateinit var permissions: List<String>

    /**
     * Checks if API level requires permissions for actions.
     */
    private fun isApplicable(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    /**
     * Creates permissions list and saves activity link.
     */
    fun create(activity: AppCompatActivity) {
        if (!isApplicable()) {
            return
        }

        this.activity = activity

        // cannot init it earlier because field READ_CALL_LOG requires API 16+
        permissions = listOf(
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.GET_ACCOUNTS)
    }

    /**
     * Asks for permissions if they have not been granted yet.
     */
    fun askIfNeeded() {
        if (!isApplicable()) {
            return
        }

        getNeeded()
                .takeIf { it.isNotEmpty() }
                ?.let { activity.requestPermissions(it, request_code) }
    }

    /**
     * Returns a list of permissions to be asked for.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    fun getNeeded(): Array<String> {
        return permissions
                .filter { s ->
                    activity.checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED
                }
                .toTypedArray()
    }

    /**
     * Checks user's response for permissions request.
     */
    fun checkResults(requestCode: Int, grantResults: IntArray) {
        if (requestCode != request_code) {
            return
        }

        grantResults.any { it != PackageManager.PERMISSION_GRANTED }
                .let { complain(activity) }
    }

    /**
     * Shows message to inform user that some permissions have not been granted.
     */
    fun complain(context: Context) {
        Alert.alert(context, R.string.permissions_error, R.string.permissions_error_desc)
    }

}
