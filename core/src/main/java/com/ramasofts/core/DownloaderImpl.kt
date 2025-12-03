package com.ramasofts.core

import android.content.Context
import com.ramasofts.foreground.ForegroundController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class DownloaderImpl(
    private val context: Context,
    private val fg: ForegroundController?,
    private val logger: DownloaderLogger = DefaultDownloaderLogger

) : Downloader {

    private val state = DownloaderStateMachine()
    private var job: Job? = null

    override fun start(config: DownloaderConfig) {

        logger.d("Downloader", "Start download: ${config.url}")


        state.transitionTo(DownloaderState.STARTING)
        fg?.startForeground(config.foregroundConfig!!)

        val worker = DownloadWorker(
            context = context,
            config = config,
            onProgress = { progress ->
                fg?.updateProgress(progress)
            },
            onCompleted = {
                fg?.updateProgress(100)
                fg?.stopForeground()

                state.transitionTo(DownloaderState.COMPLETED)
                state.transitionTo(DownloaderState.STOPPED)


                logger.d("Downloader", "Download complete")
            },
            onError = { error ->
                fg?.stopForeground()

                state.transitionTo(DownloaderState.FAILED)
                state.transitionTo(DownloaderState.STOPPED)


                logger.e("Downloader", "Download failed", error)

            }
        )

        // Run in coroutine
        job = CoroutineScope(Dispatchers.IO).launch {
            state.transitionTo(DownloaderState.RUNNING)
            worker.start()
        }
    }

    override fun stop() {
        if (job?.isActive == true) {
            state.transitionTo(DownloaderState.STOPPING)
            job?.cancel()
        }

        fg?.stopForeground()
        state.transitionTo(DownloaderState.STOPPED)
    }
}
