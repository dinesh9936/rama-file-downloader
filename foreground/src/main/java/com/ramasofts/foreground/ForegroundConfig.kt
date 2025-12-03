package com.ramasofts.foreground

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ForegroundConfig(
    val channelId: String,
    val channelName: String,
    val notificationId: Int,
    val appName: String,
    val smallIconRes: Int,
    val pauseAction: Boolean,
    val cancelAction: Boolean
) : Parcelable
