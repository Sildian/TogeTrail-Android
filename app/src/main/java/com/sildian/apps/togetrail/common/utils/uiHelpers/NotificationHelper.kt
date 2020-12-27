package com.sildian.apps.togetrail.common.utils.uiHelpers

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import com.sildian.apps.togetrail.R

/*************************************************************************************************
 * Provides with some functions allowing to display notifications
 ************************************************************************************************/

object NotificationHelper {

    @JvmStatic
    fun createLowPriorityNotification(context: Context, @StringRes titleRes: Int, @StringRes descriptionRes: Int,
                                      channelId: String): Notification =
        createNotification(context, titleRes, descriptionRes, NotificationCompat.PRIORITY_LOW, channelId)

    @JvmStatic
    fun createMediumPriorityNotification(context: Context, @StringRes titleRes: Int, @StringRes descriptionRes: Int,
                                         channelId: String): Notification =
        createNotification(context, titleRes, descriptionRes, NotificationCompat.PRIORITY_DEFAULT, channelId)

    @JvmStatic
    fun createHighPriorityNotification(context: Context, @StringRes titleRes: Int, @StringRes descriptionRes: Int,
                                       channelId: String): Notification =
        createNotification(context, titleRes, descriptionRes, NotificationCompat.PRIORITY_HIGH, channelId)

    @JvmStatic
    @Suppress("DEPRECATION")
    private fun createNotification(context: Context, @StringRes titleRes: Int, @StringRes descriptionRes: Int,
                           priority: Int, channelId: String): Notification
    {
        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelImportance = when (priority) {
                NotificationCompat.PRIORITY_LOW -> NotificationManager.IMPORTANCE_LOW
                NotificationCompat.PRIORITY_HIGH -> NotificationManager.IMPORTANCE_HIGH
                else -> NotificationManager.IMPORTANCE_DEFAULT
            }
            createNotificationChannel(context, channelId, titleRes, descriptionRes, channelImportance)
            NotificationCompat.Builder(context, channelId)
        }
        else {
            NotificationCompat.Builder(context)
        }
        return builder
            .setSmallIcon(R.drawable.ic_togetrail_logo_png)
            .setContentTitle(context.getString(titleRes))
            .setContentText(context.getString(descriptionRes))
            .setPriority(priority)
            .build()
    }

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(context: Context, id: String, @StringRes nameRes: Int,
                                          @StringRes descriptionRes: Int, importance: Int)
    {
        val channel = NotificationChannel(id, context.getString(nameRes), importance).apply {
            description = context.getString(descriptionRes)
        }
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}