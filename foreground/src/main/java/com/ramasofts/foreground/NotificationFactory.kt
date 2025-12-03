package com.ramasofts.foreground

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationFactory(
    private val context: Context
) {

    fun createNotification(
        config: ForegroundConfig,
        progress: Int
    ): Notification {

        createChannel(config)

        val pauseIntent = PendingIntent.getBroadcast(
            context, 1001,
            Intent(DownloadActionReceiver.ACTION_PAUSE),
            PendingIntent.FLAG_IMMUTABLE
        )

        val cancelIntent = PendingIntent.getBroadcast(
            context, 1002,
            Intent(DownloadActionReceiver.ACTION_CANCEL),
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, config.channelId)
            .setSmallIcon(config.smallIconRes)
            .setContentTitle("${config.appName} Downloading")
            .setContentText("$progress% completed")
            .setProgress(100, progress, false)
            .setOnlyAlertOnce(true)
            .setOngoing(true)

        if (config.pauseAction) {
            builder.addAction(0, "Pause", pauseIntent)
        }
        if (config.cancelAction) {
            builder.addAction(0, "Cancel", cancelIntent)
        }

        return builder.build()
    }


    private fun createChannel(config: ForegroundConfig) {
        val channel = NotificationChannel(
            config.channelId,
            config.channelName,
            NotificationManager.IMPORTANCE_LOW
        )
        val nm = context.getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(channel)
    }
}
