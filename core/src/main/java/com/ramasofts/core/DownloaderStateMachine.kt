package com.ramasofts.core

class DownloaderStateMachine {

    var state: DownloaderState = DownloaderState.IDLE
        private set

    fun transitionTo(newState: DownloaderState) {
        if (!isValidTransition(state, newState)) {
            throw IllegalStateException("Invalid state transition: $state → $newState")
        }
        state = newState
        println("Downloader state = $state")
    }

    private fun isValidTransition(from: DownloaderState, to: DownloaderState): Boolean {
        return when(from) {

            DownloaderState.IDLE ->
                to == DownloaderState.STARTING

            DownloaderState.STARTING ->
                to == DownloaderState.RUNNING ||
                        to == DownloaderState.FAILED ||
                        to == DownloaderState.STOPPED

            DownloaderState.RUNNING ->
                to == DownloaderState.PAUSED ||
                        to == DownloaderState.STOPPING ||
                        to == DownloaderState.COMPLETED ||
                        to == DownloaderState.FAILED

            DownloaderState.PAUSED ->
                to == DownloaderState.RUNNING ||
                        to == DownloaderState.STOPPING ||
                        to == DownloaderState.STOPPED

            DownloaderState.STOPPING ->
                to == DownloaderState.STOPPED

            DownloaderState.COMPLETED ->
                to == DownloaderState.STOPPED

            DownloaderState.FAILED ->
                to == DownloaderState.STOPPED

            DownloaderState.STOPPED ->
                false
        }
    }
}
