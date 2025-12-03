package com.ramasofts.core

import android.net.Uri
import com.ramasofts.foreground.ForegroundConfig

data class DownloaderConfig(
    val url: String,
    val appName: String,
    val fileName: String,
    val fileType: String,
    val saveUri: Uri? = null,
    val foregroundConfig: ForegroundConfig? = null
)
