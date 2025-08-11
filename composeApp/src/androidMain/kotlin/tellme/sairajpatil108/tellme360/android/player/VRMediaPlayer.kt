package tellme.sairajpatil108.tellme360.android.player

import android.content.Context
import android.net.Uri
import android.view.Surface
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import android.util.Log

@UnstableApi
class VRMediaPlayer(
    private val context: Context,
    private val listener: Listener? = null
) {
    interface Listener {
        fun onPlayerError(error: PlaybackException)
        fun onPlaybackStateChanged(playbackState: Int)
    }

    private var exoPlayer: ExoPlayer? = null

    companion object {
        private const val TAG = "VRMediaPlayer"
    }

    fun initialize() {
        exoPlayer = ExoPlayer.Builder(context).build().apply {
            addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    Log.e(TAG, "Player error: ${error.message}")
                    listener?.onPlayerError(error)
                }
                override fun onPlaybackStateChanged(playbackState: Int) {
                    Log.d(TAG, "Playback state changed: $playbackState")
                    listener?.onPlaybackStateChanged(playbackState)
                }
                override fun onVideoSizeChanged(videoSize: androidx.media3.common.VideoSize) {
                    Log.d(TAG, "Video size changed: ${videoSize.width}x${videoSize.height}, unappliedRotationDegrees: ${videoSize.unappliedRotationDegrees}, pixelWidthHeightRatio: ${videoSize.pixelWidthHeightRatio}")
                }
            })
        }
    }

    fun setVideoSurface(surface: Surface) {
        Log.d(TAG, "Setting video surface")
        exoPlayer?.setVideoSurface(surface)
    }

    fun setMediaItem(uri: String) {
        Log.d(TAG, "Setting media item: $uri")
        val mediaItem = MediaItem.fromUri(uri)
        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()
    }

    fun play() {
        Log.d(TAG, "Starting playback")
        exoPlayer?.playWhenReady = true
    }

    fun pause() {
        Log.d(TAG, "Pausing playback")
        exoPlayer?.playWhenReady = false
    }

    fun release() {
        Log.d(TAG, "Releasing player")
        exoPlayer?.release()
        exoPlayer = null
    }

    fun getPlayer(): ExoPlayer? = exoPlayer
}

