package tellme.sairajpatil108.tellme360.android.activity

import android.R.attr.padding
import androidx.activity.ComponentActivity
import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Surface
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import android.view.GestureDetector
import android.view.MotionEvent
import kotlin.math.cos
import kotlin.math.sin
import android.widget.TextView
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.lifecycle.ViewModelProvider
import tellme.sairajpatil108.tellme360.android.viewmodel.VRVideoViewModel
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.Slider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import kotlinx.coroutines.Job
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.livedata.observeAsState
import tellme.sairajpatil108.tellme360.android.renderer.GL360Renderer
import android.app.DownloadManager
import android.content.Intent
import android.os.Environment
import android.webkit.URLUtil
import java.io.File

// Download Manager for handling video downloads
class VideoDownloadManager(private val context: Context) {
    
    fun downloadVideo(videoUrl: String, videoTitle: String, onProgress: (Float) -> Unit, onComplete: (String) -> Unit, onError: (String) -> Unit) {
        try {
            // Create download request
            val request = DownloadManager.Request(Uri.parse(videoUrl)).apply {
                setTitle(videoTitle)
                setDescription("Downloading 360° video")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "${videoTitle.replace(" ", "_")}.mp4")
                setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                setAllowedOverRoaming(false)
            }
            
            // Get download service and enqueue download
            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadId = downloadManager.enqueue(request)
            
            // Monitor download progress
            Thread {
                var downloading = true
                while (downloading) {
                    val query = DownloadManager.Query().setFilterById(downloadId)
                    val cursor = downloadManager.query(query)
                    
                    if (cursor.moveToFirst()) {
                        val bytesDownloaded = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                        val bytesTotal = cursor.getLong(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                        val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                        
                        when (status) {
                            DownloadManager.STATUS_RUNNING -> {
                                if (bytesTotal > 0) {
                                    val progress = bytesDownloaded.toFloat() / bytesTotal.toFloat()
                                    onProgress(progress)
                                }
                            }
                            DownloadManager.STATUS_SUCCESSFUL -> {
                                val uri = downloadManager.getUriForDownloadedFile(downloadId)
                                downloading = false
                                onComplete(uri?.toString() ?: "Download completed")
                            }
                            DownloadManager.STATUS_FAILED -> {
                                downloading = false
                                onError("Download failed")
                            }
                        }
                    }
                    cursor.close()
                    Thread.sleep(1000) // Check every second
                }
            }.start()
            
        } catch (e: Exception) {
            onError("Download error: ${e.message}")
        }
    }
}

@UnstableApi
class VRVideoActivity : ComponentActivity() {
    
    private lateinit var glSurfaceView: GLSurfaceView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var viewModel: VRVideoViewModel
    private lateinit var downloadManager: VideoDownloadManager
    
    companion object {
        private const val TAG = "VRVideoActivity"
        const val VIDEO_URL_EXTRA = "video_url"
        const val VIDEO_TITLE_EXTRA = "video_title"
        
        // Sample 360° video URL
        const val SAMPLE_360_VIDEO_URL = "https://drive.google.com/uc?export=download&id=10oyKIe1AlliTD4HdS1j8fDxNo-zlVAbg"
        
        fun start(context: Context, videoUrl: String, videoTitle: String) {
            val intent = android.content.Intent(context, VRVideoActivity::class.java).apply {
                putExtra(VIDEO_URL_EXTRA, videoUrl)
                putExtra(VIDEO_TITLE_EXTRA, videoTitle)
            }
            if (context !is android.app.Activity) {
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            Log.d(TAG, "Starting VR Video Activity")
            
            // Initialize download manager
            downloadManager = VideoDownloadManager(this)
            
            // Keep screen on and hide system UI for immersive VR experience
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            hideSystemUI()
        
            // ViewModel
            viewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(VRVideoViewModel::class.java)

            // OpenGL
            glSurfaceView = GLSurfaceView(this)
            glSurfaceView.setEGLContextClientVersion(2)
            glSurfaceView.setRenderer(viewModel.getRenderer())
            glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY

            // UI overlays
            addUIOverlays()
            
            // Compose overlay for Play Again button and progress bar
            val composeView = ComposeView(this)
            composeView.setContent {
                val playbackEnded by viewModel.playbackEnded.observeAsState(false)
                val currentPosition by viewModel.currentPosition.observeAsState(0L)
                val duration by viewModel.duration.observeAsState(0L)
                val isBuffering by viewModel.isBuffering.observeAsState(false)
                var sliderPosition by remember(currentPosition) { mutableStateOf(currentPosition.toFloat()) }
                var isUserSeeking by remember { mutableStateOf(false) }
                var showProgressBar by remember { mutableStateOf(false) }
                var hideJob by remember { mutableStateOf<Job?>(null) }
                val coroutineScope = rememberCoroutineScope()

                // Periodically update progress
                LaunchedEffect(playbackEnded) {
                    while (!playbackEnded) {
                        if (!isUserSeeking) viewModel.updateProgress()
                        delay(500)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    showProgressBar = true
                                    hideJob?.cancel()
                                    hideJob = coroutineScope.launch {
                                        delay(2000)
                                        showProgressBar = false
                                    }
                                },
                                onDoubleTap = {
                                    // Double-tap to calibrate head tracking
                                    viewModel.calibrateHeadTracking()
                                    Toast.makeText(this@VRVideoActivity, "Head tracking calibrated", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                ) {
                    // Progress bar at bottom
                    if (duration > 0L && !playbackEnded && showProgressBar) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(bottom = 32.dp, start = 16.dp, end = 16.dp)
                        ) {
                            if (isBuffering) {
                                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(formatTime(if (isUserSeeking) sliderPosition.toLong() else currentPosition), fontSize = 12.sp)
                                Slider(
                                    value = if (isUserSeeking) sliderPosition else currentPosition.toFloat(),
                                    onValueChange = {
                                        sliderPosition = it
                                        isUserSeeking = true
                                    },
                                    onValueChangeFinished = {
                                        viewModel.seekTo(sliderPosition.toLong())
                                        isUserSeeking = false
                                    },
                                    valueRange = 0f..duration.toFloat(),
                                    modifier = Modifier.weight(1f)
                                )
                                Text(formatTime(duration), fontSize = 12.sp)
                            }
                        }
                    }
                    // Play Again and Exit buttons at end
                    if (playbackEnded) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Row {
                                Button(onClick = { viewModel.replay() }) {
                                    Text("Play Again")
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Button(onClick = { finish() }) {
                                    Text("Exit")
                                }
                            }
                        }
                    }
                }
            }
            (findViewById<FrameLayout>(android.R.id.content) as? FrameLayout)?.addView(composeView)

            // Observe ViewModel
            viewModel.videoReady.observeForever { ready ->
                loadingIndicator.visibility = if (ready) View.GONE else View.VISIBLE
            }
            viewModel.error.observeForever { errorMsg ->
                errorMsg?.let {
                    Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                }
            }

            // Setup VR
            val videoUrl = intent.getStringExtra(VIDEO_URL_EXTRA) ?: SAMPLE_360_VIDEO_URL
            viewModel.setOnSurfaceTextureReadyListener { surfaceTexture ->
                viewModel.initializePlayer(surfaceTexture, videoUrl)
            }
            
            Log.d(TAG, "VR Video Activity initialized successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate", e)
            Toast.makeText(this, "Error initializing VR player: ${e.message}", Toast.LENGTH_LONG).show()
            finish()
        }
    }
    
    // Function to download video (can be called from Compose)
    fun downloadVideo(videoUrl: String, videoTitle: String) {
        downloadManager.downloadVideo(
            videoUrl = videoUrl,
            videoTitle = videoTitle,
            onProgress = { progress ->
                // Update progress in UI
                Log.d(TAG, "Download progress: ${(progress * 100).toInt()}%")
            },
            onComplete = { result ->
                runOnUiThread {
                    Toast.makeText(this, "Download completed: $result", Toast.LENGTH_LONG).show()
                }
            },
            onError = { error ->
                runOnUiThread {
                    Toast.makeText(this, "Download failed: $error", Toast.LENGTH_LONG).show()
                }
            }
        )
    }
    
    private fun addUIOverlays() {
        try {
            Log.d(TAG, "Adding UI overlays")
            
            // Create loading indicator
            loadingIndicator = ProgressBar(this).apply { visibility = View.VISIBLE }
            
            // Layout for loading indicator (center)
            val loadingParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
            }
            
            // Create main layout
            val mainLayout = FrameLayout(this).apply {
                addView(glSurfaceView)
                addView(loadingIndicator, loadingParams)
            }
            
            setContentView(mainLayout)
            
            Log.d(TAG, "UI overlays added successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error adding UI overlays", e)
        }
    }
    
    override fun onResume() {
        super.onResume()
        try {
            Log.d(TAG, "Resuming VR activity")
            glSurfaceView.onResume()
            viewModel.registerSensors()
            viewModel.play()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onResume", e)
        }
    }
    
    override fun onPause() {
        super.onPause()
        try {
            Log.d(TAG, "Pausing VR activity")
            glSurfaceView.onPause()
            viewModel.unregisterSensors()
            viewModel.pause()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onPause", e)
        }
    }
                
    override fun onDestroy() {
        super.onDestroy()
        try {
            Log.d(TAG, "Destroying VR activity")
            viewModel.release()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onDestroy", e)
        }
    }
    
    private fun hideSystemUI() {
        try {
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error hiding system UI", e)
        }
    }

    private fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }
}

