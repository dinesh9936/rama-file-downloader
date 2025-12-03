package com.ramasofts.core

enum class DownloaderState {
    IDLE,
    STARTING,
    RUNNING,
    PAUSED,
    STOPPING,
    STOPPED,
    COMPLETED,
    FAILED
}
