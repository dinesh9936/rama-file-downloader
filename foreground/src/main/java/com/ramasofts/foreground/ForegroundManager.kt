package com.ramasofts.foreground

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder

class ForegroundManager(
    private val context: Context
) : ForegroundController {

    private var service: DownloaderForegroundService? = null
    private var isBound = false
    private var hasStartedService = false
    private var pendingConfig: ForegroundConfig? = null

    override fun startForeground(config: ForegroundConfig) {
        pendingConfig = config

        val intent = Intent(context, DownloaderForegroundService::class.java).apply {
            putExtra("foregroundConfig", config)
        }

        context.startForegroundService(intent)
        hasStartedService = true

        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun updateProgress(progress: Int) {
        service?.updateProgress(progress)
    }


    override fun stopForeground() {

        // 1) Stop notification if service is alive
        try {
            service?.stopForeground()
        } catch (_: Exception) { }

        // 2) Safely unbind ONLY if service was ever bound
        if (isBound) {
            try {
                context.unbindService(serviceConnection)
            } catch (_: Exception) { }
        }

        isBound = false
        service = null
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            isBound = true
            val s = (binder as ForegroundBinder).getService()
            service = s
            pendingConfig?.let { s.startForeground(it) }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            service = null
        }
    }
}
