package tellme.sairajpatil108.tellme360.android.viewmodel

import android.app.Application
import android.graphics.SurfaceTexture
import android.view.Surface
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import tellme.sairajpatil108.tellme360.android.player.VRMediaPlayer
import tellme.sairajpatil108.tellme360.android.sensor.VRSensorManager
import tellme.sairajpatil108.tellme360.android.renderer.VRRenderer
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player

class VRVideoViewModel(app: Application) : AndroidViewModel(app),
    VRMediaPlayer.Listener, VRSensorManager.Listener {

    private val _playbackState = MutableLiveData<Int>()
    val playbackState: LiveData<Int> = _playbackState

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _rotationMatrix = MutableLiveData<FloatArray>()
    val rotationMatrix: LiveData<FloatArray> = _rotationMatrix

    private val _videoReady = MutableLiveData<Boolean>(false)
    val videoReady: LiveData<Boolean> = _videoReady

    private val _playbackEnded = MutableLiveData<Boolean>(false)
    val playbackEnded: LiveData<Boolean> = _playbackEnded

    private val _currentPosition = MutableLiveData<Long>(0L)
    val currentPosition: LiveData<Long> = _currentPosition

    private val _duration = MutableLiveData<Long>(0L)
    val duration: LiveData<Long> = _duration

    private val _isBuffering = MutableLiveData<Boolean>(false)
    val isBuffering: LiveData<Boolean> = _isBuffering

    private val player = VRMediaPlayer(app.applicationContext, this)
    private val sensorManager = VRSensorManager(app.applicationContext, this)
    private val renderer = VRRenderer(app.applicationContext)

    fun initializePlayer(surfaceTexture: SurfaceTexture, videoUrl: String) {
        // Ensure ExoPlayer operations occur on the main thread per Media3 requirements
        val mainHandler = android.os.Handler(android.os.Looper.getMainLooper())
        mainHandler.post {
            player.initialize()
            val surface = Surface(surfaceTexture)
            player.setVideoSurface(surface)
            player.setMediaItem(videoUrl)
            player.play()
        }
    }

    fun play() = player.play()
    fun pause() = player.pause()
    fun release() = player.release()

    fun registerSensors() = sensorManager.register()
    fun unregisterSensors() = sensorManager.unregister()

    fun setOnSurfaceTextureReadyListener(listener: (SurfaceTexture) -> Unit) {
        renderer.setOnSurfaceTextureReadyListener(listener)
    }

    fun updateRendererRotation(rotation: FloatArray) {
        renderer.updateRotationMatrix(rotation)
    }

    fun setVideoReady(ready: Boolean) {
        renderer.setVideoReady(ready)
        _videoReady.postValue(ready)
    }

    fun getRenderer() = renderer

    fun replay() {
        player.getPlayer()?.let { exoPlayer ->
            exoPlayer.seekTo(0)
            exoPlayer.playWhenReady = true
            _playbackEnded.value = false
        }
    }

    fun updateProgress() {
        player.getPlayer()?.let { exoPlayer ->
            _currentPosition.value = exoPlayer.currentPosition
            _duration.value = exoPlayer.duration.takeIf { it > 0 } ?: 0L
        }
    }

    fun seekTo(position: Long) {
        player.getPlayer()?.seekTo(position)
    }

    // VRMediaPlayer.Listener
    override fun onPlayerError(error: PlaybackException) {
        _error.postValue(error.message)
    }
    
    override fun onPlaybackStateChanged(playbackState: Int) {
        _playbackState.postValue(playbackState)
        setVideoReady(playbackState == Player.STATE_READY)
        _playbackEnded.postValue(playbackState == Player.STATE_ENDED)
        _isBuffering.postValue(playbackState == Player.STATE_BUFFERING)
        // Update duration when ready
        if (playbackState == Player.STATE_READY) {
            _duration.postValue(player.getPlayer()?.duration ?: 0L)
        }
    }

    // VRSensorManager.Listener
    override fun onOrientationChanged(rotationMatrix: FloatArray) {
        _rotationMatrix.postValue(rotationMatrix)
        updateRendererRotation(rotationMatrix)
    }

    // Head tracking calibration methods
    fun calibrateHeadTracking() {
        sensorManager.calibrate()
    }

    fun resetHeadTrackingCalibration() {
        sensorManager.resetCalibration()
    }
}

