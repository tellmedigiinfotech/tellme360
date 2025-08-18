package tellme.sairajpatil108.tellme360.ios

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import platform.CoreGraphics.CGRectMake
import platform.CoreMotion.CMAttitudeReferenceFrameXArbitraryCorrectedZVertical
import platform.CoreMotion.CMMotionManager
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSSelectorFromString
import platform.SceneKit.*
import platform.UIKit.*

@OptIn(ExperimentalForeignApi::class)
class SimpleVRDemoController(
    private val videoTitle: String
) : UIViewController(nibName = null, bundle = null) {

    private var scnView: SCNView? = null
    private var cameraNode: SCNNode? = null
    private var motionManager: CMMotionManager? = null

    override fun viewDidLoad() {
        super.viewDidLoad()

        title = videoTitle
        view.backgroundColor = UIColor.blackColor

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

        // Create a simple colored sphere instead of video
        val sphere = SCNSphere.sphereWithRadius(50.0)
        sphere.setSegmentCount(96)
        val material = sphere.firstMaterial ?: SCNMaterial.material()
        material.setDoubleSided(true)
        material.setCullMode(SCNCullModeFront)
        
        // Use a gradient or pattern instead of video
        material.diffuse.setContents(UIColor.blueColor)
        sphere.setFirstMaterial(material)
        
        val sphereNode = SCNNode.nodeWithGeometry(sphere)
        scene.rootNode.addChildNode(sphereNode)

        // Close button
        val close = UIButton.buttonWithType(buttonType = UIButtonTypeSystem) as UIButton
        close.setTitle("Close", forState = UIControlStateNormal)
        close.addTarget(this, NSSelectorFromString("onClose"), UIControlEventTouchUpInside)
        close.setFrame(CGRectMake(16.0, 44.0, 80.0, 40.0))
        view.addSubview(close)

        // Info label
        val infoLabel = UILabel()
        infoLabel.setText("VR Demo Environment\n\nFor Simulator:\nDevice → More Tools → Send Device Motion\nOr try the buttons below\n\n(Video playback will be added later)")
        infoLabel.setTextColor(UIColor.whiteColor)
        infoLabel.setTextAlignment(NSTextAlignmentCenter)
        infoLabel.setNumberOfLines(0)
        infoLabel.setFrame(CGRectMake(50.0, 200.0, 250.0, 200.0))
        infoLabel.autoresizingMask = UIViewAutoresizingFlexibleLeftMargin or UIViewAutoresizingFlexibleRightMargin
        view.addSubview(infoLabel)
        
        // Add simulator test buttons
        val leftButton = UIButton.buttonWithType(buttonType = UIButtonTypeSystem) as UIButton
        leftButton.setTitle("← Look Left", forState = UIControlStateNormal)
        leftButton.setTitleColor(UIColor.whiteColor, forState = UIControlStateNormal)
        leftButton.backgroundColor = UIColor.systemBlueColor
        leftButton.addTarget(this, NSSelectorFromString("lookLeft"), UIControlEventTouchUpInside)
        leftButton.setFrame(CGRectMake(20.0, 450.0, 100.0, 40.0))
        view.addSubview(leftButton)
        
        val rightButton = UIButton.buttonWithType(buttonType = UIButtonTypeSystem) as UIButton
        rightButton.setTitle("Look Right →", forState = UIControlStateNormal)
        rightButton.setTitleColor(UIColor.whiteColor, forState = UIControlStateNormal)
        rightButton.backgroundColor = UIColor.systemBlueColor
        rightButton.addTarget(this, NSSelectorFromString("lookRight"), UIControlEventTouchUpInside)
        rightButton.setFrame(CGRectMake(200.0, 450.0, 100.0, 40.0))
        view.addSubview(rightButton)
        
        val upButton = UIButton.buttonWithType(buttonType = UIButtonTypeSystem) as UIButton
        upButton.setTitle("↑ Look Up", forState = UIControlStateNormal)
        upButton.setTitleColor(UIColor.whiteColor, forState = UIControlStateNormal)
        upButton.backgroundColor = UIColor.systemBlueColor
        upButton.addTarget(this, NSSelectorFromString("lookUp"), UIControlEventTouchUpInside)
        upButton.setFrame(CGRectMake(130.0, 400.0, 80.0, 40.0))
        view.addSubview(upButton)
        
        val downButton = UIButton.buttonWithType(buttonType = UIButtonTypeSystem) as UIButton
        downButton.setTitle("↓ Look Down", forState = UIControlStateNormal)
        downButton.setTitleColor(UIColor.whiteColor, forState = UIControlStateNormal)
        downButton.backgroundColor = UIColor.systemBlueColor
        downButton.addTarget(this, NSSelectorFromString("lookDown"), UIControlEventTouchUpInside)
        downButton.setFrame(CGRectMake(130.0, 500.0, 80.0, 40.0))
        view.addSubview(downButton)

        // Motion tracking
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
    fun lookLeft() {
        cameraNode?.setEulerAngles(SCNVector3Make(0f, 0.5f, 0f))
    }
    
    @ObjCAction
    fun lookRight() {
        cameraNode?.setEulerAngles(SCNVector3Make(0f, -0.5f, 0f))
    }
    
    @ObjCAction
    fun lookUp() {
        cameraNode?.setEulerAngles(SCNVector3Make(0.5f, 0f, 0f))
    }
    
    @ObjCAction
    fun lookDown() {
        cameraNode?.setEulerAngles(SCNVector3Make(-0.5f, 0f, 0f))
    }

    override fun viewWillDisappear(animated: Boolean) {
        super.viewWillDisappear(animated)
        motionManager?.stopDeviceMotionUpdates()
    }
}
