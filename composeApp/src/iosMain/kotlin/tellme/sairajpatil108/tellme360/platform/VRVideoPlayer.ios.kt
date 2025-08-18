package tellme.sairajpatil108.tellme360.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.UIScreen
import platform.UIKit.UIWindowScene
import platform.Foundation.NSSet
import platform.Foundation.allObjects
import platform.UIKit.UIModalPresentationFullScreen
import platform.darwin.NSObject
import tellme.sairajpatil108.tellme360.ios.RootControllerHolder
import tellme.sairajpatil108.tellme360.ios.SimpleVideoPlayerController

actual class VRVideoPlayer {
    actual fun playVideo(videoUrl: String, videoTitle: String) {
        presentVRPlayer(videoUrl, videoTitle)
    }
}

@Composable
actual fun rememberVRVideoPlayer(): VRVideoPlayer {
    return remember { VRVideoPlayer() }
}

@OptIn(ExperimentalForeignApi::class)
private fun presentVRPlayer(videoUrl: String, videoTitle: String) {
    val topController = findTopMostController() ?: return
    
    // Create simple video player controller
    val videoController = SimpleVideoPlayerController(videoUrl = videoUrl, videoTitle = videoTitle)
    videoController.setModalPresentationStyle(UIModalPresentationFullScreen)
    topController.presentViewController(videoController, animated = true, completion = null)
}

@OptIn(ExperimentalForeignApi::class)
private fun findTopMostController(): UIViewController? {
    // Prefer the Compose root controller captured at app startup
    val root = RootControllerHolder.rootController
    var top = root
    while (top?.presentedViewController != null) {
        top = top?.presentedViewController
    }
    return top
}



