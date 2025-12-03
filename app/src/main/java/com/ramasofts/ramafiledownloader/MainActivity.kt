package com.ramasofts.ramafiledownloader

import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ramasofts.core.DefaultDownloaderLogger
import com.ramasofts.core.DownloaderConfig
import com.ramasofts.core.DownloaderImpl
import com.ramasofts.foreground.ForegroundConfig
import com.ramasofts.foreground.ForegroundManager
import com.ramasofts.ramafiledownloader.ui.theme.RamaFileDownloaderTheme

class MainActivity : ComponentActivity() {


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RamaFileDownloaderTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Button(
                        modifier = Modifier.padding(innerPadding),
                        onClick = {

                            // ---- Step 1: MediaStore entry for video file ----
                            val contentValues = ContentValues().apply {
                                put(MediaStore.Video.Media.DISPLAY_NAME, "sample_video.mp4")
                                put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
                                put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)
                            }

                            val saveUri = contentResolver.insert(
                                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                contentValues
                            )

                            // ---- Step 2: Downloader config ----
                            val config = DownloaderConfig(
                                url = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                                appName = "MyApp",
                                fileName = "sample_video.mp4",
                                fileType = "video/mp4",
                                saveUri = saveUri,
                                foregroundConfig = ForegroundConfig(
                                    channelId = "downloader",
                                    channelName = "File Downloads",
                                    notificationId = 999,
                                    appName = "MyApp",
                                    smallIconRes = R.drawable.ic_launcher_foreground,
                                    pauseAction = true,
                                    cancelAction = true
                                )
                            )

                            // ---- Step 3: Start download ----
                            val downloader = DownloaderImpl(
                                context = this,
                                fg = ForegroundManager(this),
                                logger = DefaultDownloaderLogger
                            )

                            downloader.start(config)
                        }
                    ) {
                        Text("Download Video")
                    }

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RamaFileDownloaderTheme {
        Greeting("Android")
    }
}