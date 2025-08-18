package tellme.sairajpatil108.tellme360.platform

import androidx.compose.runtime.Composable

expect class VRVideoPlayer {
    fun playVideo(videoUrl: String, videoTitle: String)
}

@Composable
expect fun rememberVRVideoPlayer(): VRVideoPlayer



