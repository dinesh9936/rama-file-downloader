package com.ramasofts.foreground

import android.os.Binder

class ForegroundBinder(
    private val service: DownloaderForegroundService
) : Binder() {

    fun getService(): DownloaderForegroundService = service
}
