package com.artemkaxboy.android.autoredialce

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.CallLog
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.artemkaxboy.android.autoredialce.utils.Logger
import com.artemkaxboy.android.autoredialce.utils.SettingsHelper
import java.util.Timer
import kotlin.concurrent.schedule

object Redialing {

    private var notificationChannelCreated = false
    private const val NOTIFICATION_MAIN_ID = 1
    private const val COMMON_NOTIFICATIONS_CHANNEL_ID = "common_notifications_channel"

    /**
     * Shows dialog which asks if user want to start redialing.
     *
     * @param context app context
     * @param number number to redial
     */
    fun query(context: Context, number: String) {
        if (isNoPromptRedial(context)) {
            start(context, number)
            nextCall(context)
            return
        }
        with(Intent(context, ActivityDialog::class.java)) {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(ActivityDialog.TYPE, ActivityDialog.TYPE_QUERY)
            putExtra("number", number)
            putExtra(CallLog.Calls.TYPE, CallLog.Calls.OUTGOING_TYPE)
            context.startActivity(this)
        }
    }

    /**
     * Starts redialing process.
     *
     * @param context app context
     * @param number number to redial
     */
    fun start(context: Context, number: String?) {
        if (number.isNullOrEmpty()) {
            throw IllegalArgumentException("Number cannot be null or empty")
        }
        setRedialing(context, true)
        setCurrentAttempt(context, 0)
        setRedialingNumber(context, number)
        createNotification(context)
    }

    private fun createNotificationChannelIfNeeded(context: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (!notificationChannelCreated && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.app_name)
            val descriptionText = context.getString(R.string.common_notifications_channel)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(COMMON_NOTIFICATIONS_CHANNEL_ID, name,
                    importance).apply {
                description = descriptionText
            }

            // Register the channel with the system
            val notificationManager: NotificationManager = context.getSystemService(
                    Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            notificationChannelCreated = true
        }
    }

    private fun createNotification(context: Context) {
        createNotificationChannelIfNeeded(context)

        val statusDialogIntent = Intent(context, ActivityDialog::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(ActivityDialog.TYPE, ActivityDialog.TYPE_STATUS)
        val contentIntent = PendingIntent.getActivity(context, 0, statusDialogIntent, 0)
        val stop = PendingIntent.getBroadcast(context, 0,
                ReceiverCommand.getIntent(context, ReceiverCommand.ACTION_REDIALING_STOP), 0)
        val next = PendingIntent.getBroadcast(context, 0,
                ReceiverCommand.getIntent(context, ReceiverCommand.ACTION_REDIALING_CALL_NOW), 0)

        val builder = NotificationCompat.Builder(context, COMMON_NOTIFICATIONS_CHANNEL_ID)
                .setSmallIcon(R.drawable.notify)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                .setTicker(context.getString(R.string.redialing))
                .setContentIntent(contentIntent)
                .setOngoing(true)
                .setContentTitle(context.getString(R.string.redialing))
                .setContentText(context.getString(R.string.attemptString,
                        getCurrentAttempt(context) + 1,
                        getAttemptsCount(context)))
                .addAction(R.drawable.ic_stop, context.getString(R.string.finish_button_text), stop)
                .addAction(R.drawable.ic_next, context.getString(R.string.next_button_text), next)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(NOTIFICATION_MAIN_ID, builder.build())
        }
    }

    fun stop(context: Context) {
        setMasterCall(context, false)
        setRedialing(context, false)
        setIgnoreLast(context, false)
        if (isWidgetRedialing(context)) {
            restoreEnabled(context)
            SettingsHelper.setBoolean(context, SettingsHelper.SERVICES_ENABLED,
                    SettingsHelper.getBoolean(context, SettingsHelper.SERVICES_ENABLED_BACKUP))
            setWidgetRedialing(context, false)
        }
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(NOTIFICATION_MAIN_ID)
    }

    fun hangup(context: Context) {
        try {
            // Get the boring old TelephonyManager
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            // Get the getITelephony() method
            val classTelephony = Class.forName(telephonyManager.javaClass.name)
            val methodGetITelephony = classTelephony.getDeclaredMethod("getITelephony")

            // Ignore that the method is supposed to be private
            methodGetITelephony.isAccessible = true

            // Invoke getITelephony() to get the ITelephony interface
            val telephonyInterface = methodGetITelephony.invoke(telephonyManager)

            // Get the endCall method from ITelephony
            val telephonyInterfaceClass = Class.forName(telephonyInterface.javaClass.name)
            val methodEndCall = telephonyInterfaceClass.getDeclaredMethod("endCall")

            // Invoke endCall()
            methodEndCall.invoke(telephonyInterface)
        } catch (ex: Exception) { // Many things can go wrong with reflection calls
            Logger.error { "cannot hangup call **$ex" }
        }
    }

    fun startTimerToApprove(context: Context) {
        Timer("Hangup", false).schedule(10000) {
            hangup(context)
        }
    }

    fun approve(context: Context) {
        Logger.debug { "approve" }
    }

    fun waitNext(context: Context) {
        if (getPause(context) == 0) {
            nextCall(context)
        } else {
            val service = Intent(context, ServiceWait::class.java)
            context.startService(service)
        }
    }

    fun nextCall(context: Context) {
        setCurrentAttempt(context, getCurrentAttempt(context) + 1)
        call(context)
    }

    /**
     * Makes call to the number which was earlier saved in Preferences using system intent.
     *
     * @param context app context
     */
    fun call(context: Context) {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (tm.callState == TelephonyManager.CALL_STATE_IDLE) {
            if (ActivityCompat.checkSelfPermission(context,
                            Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                setMasterCall(context, true)
                SettingsHelper.setBoolean(context, SettingsHelper.CONFIRMATION_GOT, true)
                val num = getRedialingNumber(context).replace("#".toRegex(), Uri.encode("#"))
                val call = Intent(Intent.ACTION_CALL, Uri.parse("tel:$num"))
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(call)

                showApprover(context)
            } else {
                Toast.makeText(context, "Cant call 2! Denied!", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showApprover(context: Context) {
        with(Intent(context, com.artemkaxboy.android.autoredialce.ui.activities.ActivityDialog::class.java)) {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(this)
        }

        startTimerToApprove(context)
    }

    fun endCall(context: Context) {
        setMasterCall(context, false)
    }

    fun keepOn(context: Context): Boolean {
        if (getCurrentAttempt(context) >= getAttemptsCount(context)) return false

        createNotification(context)
        return true
    }

    fun isEnabled(context: Context) =
            SettingsHelper.readBoolean(context, SettingsHelper.SERVICES_ENABLED) &&
                    SettingsHelper.readBoolean(context, SettingsHelper.SERVICES_ENABLED)

    fun setEnabled(context: Context, value: Boolean) {
        SettingsHelper.setBoolean(context, SettingsHelper.AUTOREDIAL_ENABLED, value)
    }

    fun backupEnabled(context: Context) {
        SettingsHelper.setBoolean(context, SettingsHelper.AUTOREDIAL_ENABLED_BACKUP,
                SettingsHelper.readBoolean(context, SettingsHelper.AUTOREDIAL_ENABLED))
    }

    private fun restoreEnabled(context: Context) {
        setEnabled(context,
                SettingsHelper.getBoolean(context, SettingsHelper.AUTOREDIAL_ENABLED_BACKUP))
    }

    fun isRedialing(context: Context) = SettingsHelper.getBoolean(context, SettingsHelper.REDIALING)

    private fun setRedialing(context: Context, value: Boolean) {
        SettingsHelper.setBoolean(context, SettingsHelper.REDIALING, value)
    }

    fun isMasterCall(context: Context) =
            SettingsHelper.getBoolean(context, SettingsHelper.MASTER_CALL)

    private fun setMasterCall(context: Context, value: Boolean) {
        SettingsHelper.setBoolean(context, SettingsHelper.MASTER_CALL, value)
    }

    fun isIgnoreLast(context: Context) =
            SettingsHelper.getBoolean(context, SettingsHelper.IGNORE_LAST)

    fun setIgnoreLast(context: Context, value: Boolean) {
        SettingsHelper.setBoolean(context, SettingsHelper.IGNORE_LAST, value)
    }

    private fun isWidgetRedialing(context: Context) =
            SettingsHelper.getBoolean(context, SettingsHelper.WIDGET_REDIALING)

    fun setWidgetRedialing(context: Context, value: Boolean) {
        SettingsHelper.setBoolean(context, SettingsHelper.WIDGET_REDIALING, value)
    }

    private fun isNoPromptRedial(context: Context) =
            SettingsHelper.readBoolean(context, SettingsHelper.NO_PROMPT_REDIAL)

    fun getAttemptsCount(context: Context) =
            SettingsHelper.readInt(context, SettingsHelper.REDIALING_ATTEMPTS_COUNT)

    fun getCurrentAttempt(context: Context) =
            SettingsHelper.getInt(context, SettingsHelper.REDIALING_CURRENT_ATTEMPT)

    private fun setCurrentAttempt(context: Context, attempt: Int) {
        SettingsHelper.setInt(context, SettingsHelper.REDIALING_CURRENT_ATTEMPT, attempt)
    }

    fun getPause(context: Context) = SettingsHelper.readInt(context, SettingsHelper.REDIALING_PAUSE)

    fun getRedialingNumber(context: Context) =
            SettingsHelper.getString(context, SettingsHelper.REDIALING_NUMBER)

    private fun setRedialingNumber(context: Context, number: String) {
        SettingsHelper.setString(context, SettingsHelper.REDIALING_NUMBER, number)
    }
}
