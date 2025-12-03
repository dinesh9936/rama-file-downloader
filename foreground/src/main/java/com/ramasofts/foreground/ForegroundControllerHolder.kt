package com.ramasofts.foreground

object ForegroundControllerHolder {
    var controller: ForegroundActionHandler? = null
}

interface ForegroundActionHandler {
    fun pauseDownload()
    fun cancelDownload()
}
