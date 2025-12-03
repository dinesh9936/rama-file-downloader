package com.ramasofts.core

interface Downloader {
    fun start(config: DownloaderConfig)
    fun stop()
}
