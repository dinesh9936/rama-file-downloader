package com.ramasofts.foreground

import com.ramasofts.foreground.ForegroundConfig

interface ForegroundController {
    fun startForeground(config: ForegroundConfig)
    fun updateProgress(progress: Int)
    fun stopForeground()
}
