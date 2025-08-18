package tellme.sairajpatil108.tellme360.platform

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import tellme.sairajpatil108.tellme360.android.activity.VRVideoActivity

actual class VRVideoPlayer(private val context: Context) {
    actual fun playVideo(videoUrl: String, videoTitle: String) {
        VRVideoActivity.start(context, videoUrl, videoTitle)
    }
}

@Composable
actual fun rememberVRVideoPlayer(): VRVideoPlayer {
    val context = LocalContext.current
    return remember { VRVideoPlayer(context) }
}



