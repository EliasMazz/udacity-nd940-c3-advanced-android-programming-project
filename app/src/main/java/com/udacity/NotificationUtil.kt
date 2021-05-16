package com.udacity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat

private const val NOTIFICATION_ID = 420
const val CHANNEL_ID = "CHANNEL_ID"
const val CHANNEL_NAME = "CHANNEL_NAME"

fun NotificationManager.sendNotification(
    messageBody: String,
    applicationContext: Context,
    contentIntent: Intent
) {
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        CHANNEL_ID
    )
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentIntent(contentPendingIntent)
        .addAction(R.drawable.ic_assistant_black_24dp, applicationContext.getString(R.string.notification_check_status), contentPendingIntent)
        .setAutoCancel(true)

    createChannel(applicationContext, this)

    notify(NOTIFICATION_ID, builder.build())
}

private fun createChannel(applicationContext: Context, notificationManager: NotificationManager) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
        )

        notificationChannel.lightColor = Color.RED
        notificationChannel.enableVibration(true)
        notificationChannel.enableLights(true)
        notificationChannel.description = applicationContext.getString(R.string.download_status_channel_description)

        notificationManager.createNotificationChannel(notificationChannel)
    }
}

