package com.ramasofts.foreground

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat

class DownloaderForegroundService : Service() {

    private val binder = ForegroundBinder(this)
    private lateinit var notificationFactory: NotificationFactory
    private var cfg: ForegroundConfig? = null

    override fun onCreate() {
        super.onCreate()
        notificationFactory = NotificationFactory(this)
    }

    override fun onBind(intent: Intent): IBinder = binder

    fun startForeground(config: ForegroundConfig) {
        cfg = config
        val initialNotification = notificationFactory.createNotification(
            config = config,
            progress = 0
        )
        // Start service in foreground
        startForeground(config.notificationId, initialNotification)
    }

    fun updateProgress(progress: Int) {
        cfg?.let {
            val updated = notificationFactory.createNotification(
                config = it,
                progress = progress
            )
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }
            NotificationManagerCompat.from(this)
                .notify(it.notificationId, updated)
        }
    }

    fun stopForeground() {
        stopForeground(true)
        stopSelf()
    }
}
