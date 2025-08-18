package tellme.sairajpatil108.tellme360.ios

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVURLAsset
import platform.CoreGraphics.CGRectMake
import platform.CoreMotion.CMAttitudeReferenceFrameXArbitraryCorrectedZVertical
import platform.CoreMotion.CMMotionManager
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSSelectorFromString
import platform.Foundation.NSURL
import platform.darwin.NSObject
import platform.SceneKit.*
import platform.UIKit.*

@OptIn(ExperimentalForeignApi::class)
class VRPlayerViewController(
    private val videoUrl: String,
    private val videoTitle: String
) : UIViewController(nibName = null, bundle = null) {

    private var scnView: SCNView? = null
    private var cameraNode: SCNNode? = null
    private var motionManager: CMMotionManager? = null
    private var player: AVPlayer? = null

    override fun viewDidLoad() {
        super.viewDidLoad()

        title = videoTitle

        val sView = SCNView(frame = view.bounds)
        sView.autoresizingMask = UIViewAutoresizingFlexibleWidth or UIViewAutoresizingFlexibleHeight
        sView.backgroundColor = UIColor.blackColor
        sView.allowsCameraControl = false
        view.addSubview(sView)
        scnView = sView

        val scene = SCNScene.scene()
        sView.setScene(scene)

        val camNode = SCNNode.node()
        camNode.setCamera(SCNCamera.camera())
        camNode.setPosition(SCNVector3Make(0f, 0f, 0f))
        scene.rootNode.addChildNode(camNode)
        cameraNode = camNode

        val sphere = SCNSphere.sphereWithRadius(50.0)
        sphere.setSegmentCount(96)
        val fm = sphere.firstMaterial ?: SCNMaterial.material()
        fm.setDoubleSided(true)
        fm.setCullMode(SCNCullModeFront)
        sphere.setFirstMaterial(fm)
        val sphereNode = SCNNode.nodeWithGeometry(sphere)
        scene.rootNode.addChildNode(sphereNode)

        // Use the specified 360° video URL
        val videoUrl = "https://drive.google.com/uc?export=download&id=10oyKIe1AlliTD4HdS1j8fDxNo-zlVAbg"
        val asset = AVURLAsset(uRL = NSURL(string = videoUrl)!!, options = null)
        val item = AVPlayerItem(asset = asset)
        val p = AVPlayer(playerItem = item)
        player = p
        
        // Set player as material content
        fm.diffuse.setContents(p)

        val close = UIButton.buttonWithType(buttonType = UIButtonTypeSystem) as UIButton
        close.setTitle("Close", forState = UIControlStateNormal)
        close.addTarget(this, NSSelectorFromString("onClose"), UIControlEventTouchUpInside)
        close.setFrame(CGRectMake(16.0, 44.0, 80.0, 40.0))
        view.addSubview(close)

        // Overlay Play button to satisfy autoplay and readiness
        val playBtn = UIButton.buttonWithType(buttonType = UIButtonTypeSystem) as UIButton
        playBtn.setTitle("Play", forState = UIControlStateNormal)
        // Center the play button (fixed coordinates work for most screen sizes)
        playBtn.setFrame(CGRectMake(160.0, 300.0, 100.0, 50.0))
        playBtn.addTarget(this, NSSelectorFromString("onPlay"), UIControlEventTouchUpInside)
        view.addSubview(playBtn)

        val mm = CMMotionManager()
        motionManager = mm
        if (mm.isDeviceMotionAvailable()) {
            mm.deviceMotionUpdateInterval = 1.0 / 60.0
            mm.startDeviceMotionUpdatesUsingReferenceFrame(
                CMAttitudeReferenceFrameXArbitraryCorrectedZVertical,
                toQueue = NSOperationQueue.mainQueue
            ) { motion, _ ->
                motion?.let {
                    val att = it.attitude
                    cameraNode?.setEulerAngles(SCNVector3Make(att.pitch.toFloat(), att.yaw.toFloat(), att.roll.toFloat()))
                }
            }
        }
    }

    @ObjCAction
    fun onClose() {
        dismissViewControllerAnimated(true, completion = null)
    }

    @ObjCAction
    fun onPlay() {
        player?.let { p ->
            // Use NSObject.performSelector to call play method
            (p as NSObject).performSelector(NSSelectorFromString("play"))
        }
        // Hide play button after starting
        view.subviews?.forEach { sub ->
            if (sub is UIButton && (sub as UIButton).currentTitle == "Play") {
                sub.removeFromSuperview()
            }
        }
    }

    override fun viewWillDisappear(animated: Boolean) {
        super.viewWillDisappear(animated)
        // Pause via rate property
        player?.let { p ->
            (p as NSObject).performSelector(NSSelectorFromString("pause"))
        }
        motionManager?.stopDeviceMotionUpdates()
    }
}



