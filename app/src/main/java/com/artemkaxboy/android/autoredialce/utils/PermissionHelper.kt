package com.artemkaxboy.android.autoredialce.utils

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.artemkaxboy.android.autoredialce.R

object PermissionHelper {
    private const val request_code = 12

    private lateinit var activity: AppCompatActivity
    private lateinit var permissions: List<String>
    private lateinit var complains: Map<String, Int>

    private fun isApplicable(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    fun create(activity: AppCompatActivity) {
        if (!isApplicable()) {
            return
        }

        this.activity = activity

        // cannot init in earlier because fields READ_CALL_LOG requires API 16+
        permissions = listOf(
                Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.CALL_PHONE,
                Manifest.permission.GET_ACCOUNTS)

        complains = mapOf(Pair(Manifest.permission.READ_CALL_LOG, R.string.cant_read_calllog),
                Pair(Manifest.permission.READ_CONTACTS,
                        R.string.cant_read_contacts_permission_denied),
                Pair(Manifest.permission.CALL_PHONE, R.string.cant_call_permission_denied),
                Pair(Manifest.permission.GET_ACCOUNTS,
                        R.string.cant_read_contacts_permission_denied))
    }

    fun askIfNeeded() {
        if (!isApplicable()) {
            return
        }

        getNeeded()
                .takeIf { it.isNotEmpty() }
                ?.let { activity.requestPermissions(it, request_code) }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun getNeeded(): Array<String> {
        return permissions
                .filter { s ->
                    activity.checkSelfPermission(s) != PackageManager.PERMISSION_GRANTED
                }
                .toTypedArray()
    }

    fun result(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode != request_code) {
            return
        }

        assert(permissions.size == grantResults.size)

        grantResults.zip(permissions).asSequence()
                .filter { it.first != PackageManager.PERMISSION_GRANTED }
                .map { complains.getValue(it.second) }
                .distinct()
                .map { activity.getString(it) }
                .reduce { acc, s -> acc + "\n" + s }
                .let { Alert.complain(activity, it) }
    }

}