package tellme.sairajpatil108.tellme360.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

actual class VRVideoPlayer {
    actual fun playVideo(videoUrl: String, videoTitle: String) {
        // TODO: Implement iOS VR video player using Metal
        println("iOS VR video player not implemented yet")
    }
}

@Composable
actual fun rememberVRVideoPlayer(): VRVideoPlayer {
    return remember { VRVideoPlayer() }
}

