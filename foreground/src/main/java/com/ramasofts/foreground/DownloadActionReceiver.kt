package com.ramasofts.foreground

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DownloadActionReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_PAUSE = "downloader.pause"
        const val ACTION_CANCEL = "downloader.cancel"
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_PAUSE -> ForegroundControllerHolder.controller?.pauseDownload()
            ACTION_CANCEL -> ForegroundControllerHolder.controller?.cancelDownload()
        }
    }
}
