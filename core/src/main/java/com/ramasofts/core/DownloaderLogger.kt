package com.ramasofts.core


interface DownloaderLogger {
    fun d(tag: String, msg: String)
    fun e(tag: String, msg: String, error: Throwable? = null)
}

