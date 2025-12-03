package com.ramasofts.core


import android.util.Log

object DefaultDownloaderLogger : DownloaderLogger {

    private const val DEFAULT_TAG = "Downloader"

    override fun d(tag: String, msg: String) {
        Log.d(tag.ifEmpty { DEFAULT_TAG }, msg)
    }

    override fun e(tag: String, msg: String, error: Throwable?) {
        Log.e(tag.ifEmpty { DEFAULT_TAG }, msg, error)
    }
}
