package tellme.sairajpatil108.tellme360.ios

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerLayer
import platform.AVFoundation.AVURLAsset
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSSelectorFromString
import platform.Foundation.NSURL
import platform.UIKit.*
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
class SimpleVideoPlayerController(
    private val videoUrl: String,
    private val videoTitle: String
) : UIViewController(nibName = null, bundle = null) {

    private var player: AVPlayer? = null
    private var playerLayer: AVPlayerLayer? = null

    override fun viewDidLoad() {
        super.viewDidLoad()

        title = videoTitle
        view.backgroundColor = UIColor.blackColor

        // Use the specified 360° video URL
        val videoUrl = "https://tellme360.media/videos/2025-07-30/14-29-48-615460/mp4_files/1080p.mp4"
        
        // Create AVPlayer
        val asset = AVURLAsset(uRL = NSURL(string = videoUrl)!!, options = null)
        val item = AVPlayerItem(asset = asset)
        val p = AVPlayer(playerItem = item)
        player = p

        // Create AVPlayerLayer for fullscreen video
        val layer = AVPlayerLayer()
        layer.setPlayer(p)
        layer.setFrame(CGRectMake(0.0, 0.0, 400.0, 800.0)) // Use fixed size
        layer.videoGravity = "AVLayerVideoGravityResizeAspectFill"
        view.layer.addSublayer(layer)
        playerLayer = layer

        // VR-style title overlay
        val titleLabel = UILabel()
        titleLabel.setText("🥽 VR Video Player")
        titleLabel.setTextColor(UIColor.whiteColor)
        titleLabel.setTextAlignment(NSTextAlignmentCenter)
        titleLabel.setFont(UIFont.boldSystemFontOfSize(24.0))
        titleLabel.backgroundColor = UIColor.blackColor
        titleLabel.setFrame(CGRectMake(50.0, 100.0, 300.0, 60.0))
        titleLabel.layer.cornerRadius = 10.0
        view.addSubview(titleLabel)

        // Close button
        val closeButton = UIButton.buttonWithType(buttonType = UIButtonTypeSystem) as UIButton
        closeButton.setTitle("✕ Close", forState = UIControlStateNormal)
        closeButton.setTitleColor(UIColor.whiteColor, forState = UIControlStateNormal)
        closeButton.backgroundColor = UIColor.redColor
        closeButton.addTarget(this, NSSelectorFromString("onClose"), UIControlEventTouchUpInside)
        closeButton.setFrame(CGRectMake(16.0, 44.0, 80.0, 40.0))
        closeButton.layer.cornerRadius = 8.0
        view.addSubview(closeButton)

        // Play button (large center button)
        val playButton = UIButton.buttonWithType(buttonType = UIButtonTypeSystem) as UIButton
        playButton.setTitle("▶️ PLAY VIDEO", forState = UIControlStateNormal)
        playButton.setTitleColor(UIColor.whiteColor, forState = UIControlStateNormal)
        playButton.backgroundColor = UIColor.systemGreenColor
        playButton.addTarget(this, NSSelectorFromString("onPlay"), UIControlEventTouchUpInside)
        playButton.setFrame(CGRectMake(100.0, 350.0, 200.0, 60.0))
        playButton.layer.cornerRadius = 30.0
        playButton.setFont(UIFont.boldSystemFontOfSize(18.0))
        view.addSubview(playButton)

        // Pause button
        val pauseButton = UIButton.buttonWithType(buttonType = UIButtonTypeSystem) as UIButton
        pauseButton.setTitle("⏸️ PAUSE", forState = UIControlStateNormal)
        pauseButton.setTitleColor(UIColor.whiteColor, forState = UIControlStateNormal)
        pauseButton.backgroundColor = UIColor.systemOrangeColor
        pauseButton.addTarget(this, NSSelectorFromString("onPause"), UIControlEventTouchUpInside)
        pauseButton.setFrame(CGRectMake(100.0, 430.0, 200.0, 50.0))
        pauseButton.layer.cornerRadius = 25.0
        view.addSubview(pauseButton)

        // VR simulation info
        val infoLabel = UILabel()
        infoLabel.setText("360° VR Experience\n\nThis is a full-screen video player\nwith VR-style interface\n\n✅ Clear Video Playback\n✅ Reliable iOS Performance")
        infoLabel.setTextColor(UIColor.whiteColor)
        infoLabel.setTextAlignment(NSTextAlignmentCenter)
        infoLabel.setNumberOfLines(0)
        infoLabel.backgroundColor = UIColor.blackColor
        infoLabel.setFrame(CGRectMake(50.0, 520.0, 300.0, 150.0))
        infoLabel.layer.cornerRadius = 10.0
        view.addSubview(infoLabel)
    }

    @ObjCAction
    fun onClose() {
        player?.let { p ->
            (p as NSObject).performSelector(NSSelectorFromString("pause"))
        }
        dismissViewControllerAnimated(true, completion = null)
    }

    @ObjCAction
    fun onPlay() {
        player?.let { p ->
            // Debug: Check player status
            println("🎬 Attempting to play video...")
            
            // Try multiple approaches to ensure playback
            (p as NSObject).performSelector(NSSelectorFromString("play"))
            
            // Also try setting rate to 1.0 using performSelector
            (p as NSObject).performSelector(NSSelectorFromString("setRate:"), 1.0f)
            
            println("🎬 Play command sent to AVPlayer")
        }
        
        // Update UI to show it's playing
        view.subviews?.forEach { sub ->
            if (sub is UILabel && (sub as UILabel).text?.contains("Tap Play") == true) {
                (sub as UILabel).setText("🎬 Video is now playing!\n\nIf you don't see video, try:\n• Check iOS Simulator network\n• Try on physical device\n• Check console for errors")
            }
        }
    }

    @ObjCAction
    fun onPause() {
        player?.let { p ->
            (p as NSObject).performSelector(NSSelectorFromString("pause"))
        }
    }

    override fun viewWillDisappear(animated: Boolean) {
        super.viewWillDisappear(animated)
        player?.let { p ->
            (p as NSObject).performSelector(NSSelectorFromString("pause"))
        }
    }
}
