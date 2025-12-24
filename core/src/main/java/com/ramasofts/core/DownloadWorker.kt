package com.ramasofts.core

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit



class DownloadWorker(
    private val context: Context,
    private val config: DownloaderConfig,
    private val onProgress: (Int) -> Unit,
    private val onCompleted: () -> Unit,
    private val onError: (Throwable) -> Unit,
    private val logger: DownloaderLogger = DefaultDownloaderLogger
) {

    private val TAG = "RamaDownloaderWorker"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    // withContext(Dispatchers.IO) make download function on background thread
    // temFile - created to store chunks of file downloaded in temp file before complete
    //
    suspend fun start() = withContext(Dispatchers.IO) {

        logger.d(TAG, "Starting download: ${config.url}")
        val tmpFile = File(context.cacheDir, config.fileName + ".tmp")

        try {
            val request = Request.Builder()
                .url(config.url)
                .header("Accept-Encoding", "identity") // ✓ Prevent chunk/gzip issues
                .build()

            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                throw IOException("HTTP error ${response.code}")
            }

            val body = response.body ?: throw IOException("Empty body")
            val totalBytes = body.contentLength()

            logger.d(TAG, "Total bytes = $totalBytes")

            val source = body.source()
            val output = FileOutputStream(tmpFile)
            val sink = output.sink().buffer()

            val bufferSize = 8 * 1024L
            var downloaded = 0L

            // ===== Progress Throttling =====
            var lastProgress = -1
            var lastUpdateTime = 0L
            val updateInterval = 300L // ms
            // ================================

            logger.d(TAG, "Downloading to temp file: ${tmpFile.absolutePath}")

            while (true) {
                val read = source.read(sink.buffer, bufferSize)

                if (read == -1L) break
                downloaded += read

                sink.emit()

                if (totalBytes > 0) {
                    val progress = ((downloaded * 100) / totalBytes).toInt()
                    val now = System.currentTimeMillis()

                    // Throttle: send progress only every 300 ms OR on actual progress change
                    if (progress != lastProgress && (now - lastUpdateTime) >= updateInterval) {
                        lastProgress = progress
                        lastUpdateTime = now

                        logger.d(TAG, "Progress = $progress% ($downloaded/$totalBytes)")
                        onProgress(progress)
                    }
                }
            }

            sink.close()
            source.close()
            output.close()

            logger.d(TAG, "Download complete. Moving file…")

            saveDownloadedFile(tmpFile)

            logger.d(TAG, "File saved successfully.")
            onCompleted()

        } catch (e: Exception) {
            logger.e(TAG, "Download failed", e)
            tmpFile.delete()
            onError(e)
        }
    }

    private fun saveDownloadedFile(tmpFile: File) {
        val uri = config.saveUri

        if (uri != null) {
            // Save to SAF/MediaStore
            logger.d(TAG, "Saving to URI: $uri")

            context.contentResolver.openOutputStream(uri)?.use { out ->
                FileInputStream(tmpFile).use { input ->
                    input.copyTo(out)
                }
            } ?: throw IOException("Failed to open OutputStream for URI")

        } else {
            // Save to app directory
            val finalFile = File(context.getExternalFilesDir(null), config.fileName)
            logger.d(TAG, "Saving to: ${finalFile.absolutePath}")

            tmpFile.copyTo(finalFile, overwrite = true)
        }

        tmpFile.delete()
        logger.d(TAG, "Temp file removed.")
    }
}
