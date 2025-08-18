import Foundation
import UIKit
import AVFoundation
import Metal
import MetalKit
import CoreMotion
import CoreVideo
import GLKit

@objc public class VRPlayerHelper: NSObject {
    
    @objc public static func createVRPlayerViewController(videoUrl: String, videoTitle: String) -> UIViewController {
        let controller = VRPlayerViewController()
        controller.setup(videoUrl: videoUrl, videoTitle: videoTitle)
        return controller
    }
}

class VRPlayerViewController: UIViewController {
    
    private var metalView: MTKView!
    private var device: MTLDevice!
    private var commandQueue: MTLCommandQueue!
    private var renderPipelineState: MTLRenderPipelineState!
    
    private var player: AVPlayer?
    private var playerItem: AVPlayerItem?
    private var videoOutput: AVPlayerItemVideoOutput!
    private var displayLink: CADisplayLink?
    
    private var motionManager: CMMotionManager!
    private var rotationMatrix = matrix_identity_float4x4
    
    // Sphere geometry
    private var vertexBuffer: MTLBuffer!
    private var indexBuffer: MTLBuffer!
    private var indexCount: Int = 0
    
    // Video texture
    private var videoTexture: MTLTexture?
    private var textureCache: CVMetalTextureCache!
    
    // Stereoscopic rendering
    private let eyeSeparation: Float = 0.064 // 6.4cm average IPD
    private var leftProjectionMatrix = matrix_identity_float4x4
    private var rightProjectionMatrix = matrix_identity_float4x4
    
    private var videoUrl: String = ""
    private var videoTitle: String = ""
    
    func setup(videoUrl: String, videoTitle: String) {
        self.videoUrl = videoUrl
        self.videoTitle = videoTitle
        self.title = videoTitle
        self.modalPresentationStyle = .fullScreen
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        setupMetal()
        setupVideo()
        setupMotion()
        setupUI()
        
        // Start video playback
        if !videoUrl.isEmpty {
            loadVideo()
        }
    }
    
    private func setupMetal() {
        // Initialize Metal
        device = MTLCreateSystemDefaultDevice()!
        
        // Create Metal view
        metalView = MTKView(frame: view.bounds, device: device)
        metalView.delegate = self
        metalView.framebufferOnly = false
        metalView.colorPixelFormat = .bgra8Unorm
        metalView.depthStencilPixelFormat = .depth32Float
        metalView.preferredFramesPerSecond = 60
        view.addSubview(metalView)
        
        // Create command queue
        commandQueue = device.makeCommandQueue()!
        
        // Create texture cache
        CVMetalTextureCacheCreate(nil, nil, device, nil, &textureCache)
        
        // Setup render pipeline
        setupRenderPipeline()
        
        // Generate sphere geometry
        generateSphere()
        
        // Setup projection matrices for stereo
        setupProjectionMatrices()
    }
    
    private func setupRenderPipeline() {
        let library = device.makeDefaultLibrary()!
        let vertexFunction = library.makeFunction(name: "vr_vertex")!
        let fragmentFunction = library.makeFunction(name: "vr_fragment")!
        
        let renderPipelineDescriptor = MTLRenderPipelineDescriptor()
        renderPipelineDescriptor.vertexFunction = vertexFunction
        renderPipelineDescriptor.fragmentFunction = fragmentFunction
        renderPipelineDescriptor.colorAttachments[0].pixelFormat = metalView.colorPixelFormat
        renderPipelineDescriptor.depthAttachmentPixelFormat = metalView.depthStencilPixelFormat
        
        // Vertex descriptor
        let vertexDescriptor = MTLVertexDescriptor()
        vertexDescriptor.attributes[0].format = .float3
        vertexDescriptor.attributes[0].offset = 0
        vertexDescriptor.attributes[0].bufferIndex = 0
        vertexDescriptor.attributes[1].format = .float2
        vertexDescriptor.attributes[1].offset = 12
        vertexDescriptor.attributes[1].bufferIndex = 0
        vertexDescriptor.layouts[0].stride = 20 // 3 floats + 2 floats = 5 * 4 bytes
        renderPipelineDescriptor.vertexDescriptor = vertexDescriptor
        
        do {
            renderPipelineState = try device.makeRenderPipelineState(descriptor: renderPipelineDescriptor)
        } catch {
            fatalError("Failed to create render pipeline state: \(error)")
        }
    }
    
    private func setupVideo() {
        // Initialize video output with optimal settings for Metal
        let settings: [String: Any] = [
            kCVPixelBufferPixelFormatTypeKey as String: kCVPixelFormatType_32BGRA
        ]
        videoOutput = AVPlayerItemVideoOutput(pixelBufferAttributes: settings)
    }
    
    private func setupMotion() {
        motionManager = CMMotionManager()
        motionManager.deviceMotionUpdateInterval = 1.0 / 60.0
        
        if motionManager.isDeviceMotionAvailable {
            motionManager.startDeviceMotionUpdates(using: .xMagneticNorthZVertical)
        }
    }
    
    private func setupUI() {
        view.backgroundColor = .black
        
        // Add close button
        let closeButton = UIButton(type: .system)
        closeButton.setTitle("✕", for: .normal)
        closeButton.setTitleColor(.white, for: .normal)
        closeButton.backgroundColor = UIColor.black.withAlphaComponent(0.7)
        closeButton.titleLabel?.font = UIFont.systemFont(ofSize: 20, weight: .bold)
        closeButton.frame = CGRect(x: 16, y: 50, width: 44, height: 44)
        closeButton.layer.cornerRadius = 22
        closeButton.addTarget(self, action: #selector(closePressed), for: .touchUpInside)
        view.addSubview(closeButton)
        
        // Add title label
        let titleLabel = UILabel()
        titleLabel.text = videoTitle
        titleLabel.textColor = .white
        titleLabel.font = UIFont.systemFont(ofSize: 16, weight: .medium)
        titleLabel.frame = CGRect(x: 70, y: 50, width: view.bounds.width - 90, height: 44)
        titleLabel.autoresizingMask = [.flexibleWidth]
        view.addSubview(titleLabel)
        
        // Add VR mode indicator
        let vrLabel = UILabel()
        vrLabel.text = "VR MODE"
        vrLabel.textColor = .green
        vrLabel.font = UIFont.systemFont(ofSize: 12, weight: .bold)
        vrLabel.textAlignment = .center
        vrLabel.frame = CGRect(x: view.bounds.width/2 - 40, y: view.bounds.height - 60, width: 80, height: 20)
        vrLabel.autoresizingMask = [.flexibleLeftMargin, .flexibleRightMargin, .flexibleTopMargin]
        view.addSubview(vrLabel)
    }
    
    private func loadVideo() {
        guard let url = URL(string: videoUrl) else { return }
        
        playerItem = AVPlayerItem(url: url)
        playerItem?.add(videoOutput)
        
        player = AVPlayer(playerItem: playerItem)
        
        // Start display link for video frame updates
        displayLink = CADisplayLink(target: self, selector: #selector(updateVideoFrame))
        displayLink?.add(to: .main, forMode: .common)
        
        player?.play()
    }
    
    @objc private func updateVideoFrame() {
        guard let videoOutput = videoOutput else { return }
        
        let currentTime = CACurrentMediaTime()
        if videoOutput.hasNewPixelBuffer(forItemTime: CMTime(seconds: currentTime, preferredTimescale: 600)) {
            if let pixelBuffer = videoOutput.copyPixelBuffer(forItemTime: CMTime(seconds: currentTime, preferredTimescale: 600), itemTimeForDisplay: nil) {
                updateVideoTexture(pixelBuffer: pixelBuffer)
            }
        }
    }
    
    private func updateVideoTexture(pixelBuffer: CVPixelBuffer) {
        let width = CVPixelBufferGetWidth(pixelBuffer)
        let height = CVPixelBufferGetHeight(pixelBuffer)
        
        var textureRef: CVMetalTexture?
        let status = CVMetalTextureCacheCreateTextureFromImage(
            nil, textureCache, pixelBuffer, nil,
            .bgra8Unorm, width, height, 0, &textureRef
        )
        
        if status == kCVReturnSuccess, let texture = textureRef {
            videoTexture = CVMetalTextureGetTexture(texture)
        }
    }
    
    private func generateSphere() {
        let radius: Float = 50.0
        let stacks = 32
        let slices = 64
        
        var vertices: [Float] = []
        var indices: [UInt16] = []
        
        // Generate vertices
        for i in 0...stacks {
            let latitude = Float.pi * (0.5 - Float(i) / Float(stacks))
            let cosLat = cos(latitude)
            let sinLat = sin(latitude)
            
            for j in 0...slices {
                let longitude = 2.0 * Float.pi * Float(j) / Float(slices)
                let cosLon = cos(longitude)
                let sinLon = sin(longitude)
                
                let x = radius * cosLat * cosLon
                let y = radius * sinLat
                let z = radius * cosLat * sinLon
                
                // Position
                vertices.append(x)
                vertices.append(y)
                vertices.append(z)
                
                // Texture coordinates
                let u = Float(j) / Float(slices)
                let v = 1.0 - Float(i) / Float(stacks)
                vertices.append(u)
                vertices.append(v)
            }
        }
        
        // Generate indices (reverse winding for inside viewing)
        let stride = slices + 1
        for i in 0..<stacks {
            for j in 0..<slices {
                let first = UInt16(i * stride + j)
                let second = UInt16(Int(first) + stride)
                
                // Reverse winding for inside viewing
                indices.append(first)
                indices.append(second)
                indices.append(first + 1)
                
                indices.append(first + 1)
                indices.append(second)
                indices.append(second + 1)
            }
        }
        
        // Create Metal buffers
        vertexBuffer = device.makeBuffer(bytes: vertices, length: vertices.count * MemoryLayout<Float>.size, options: [])!
        indexBuffer = device.makeBuffer(bytes: indices, length: indices.count * MemoryLayout<UInt16>.size, options: [])!
        indexCount = indices.count
    }
    
    private func setupProjectionMatrices() {
        let fovy: Float = 90.0 * Float.pi / 180.0
        let aspect: Float = 0.5 // Half screen width per eye
        let near: Float = 0.1
        let far: Float = 1000.0
        
        // Create perspective projection matrices for each eye
        leftProjectionMatrix = perspectiveMatrix(fovy: fovy, aspect: aspect, near: near, far: far)
        rightProjectionMatrix = perspectiveMatrix(fovy: fovy, aspect: aspect, near: near, far: far)
    }
    
    private func perspectiveMatrix(fovy: Float, aspect: Float, near: Float, far: Float) -> matrix_float4x4 {
        let f = 1.0 / tan(fovy * 0.5)
        
        return matrix_float4x4(
            SIMD4<Float>(f / aspect, 0, 0, 0),
            SIMD4<Float>(0, f, 0, 0),
            SIMD4<Float>(0, 0, (far + near) / (near - far), -1),
            SIMD4<Float>(0, 0, (2 * far * near) / (near - far), 0)
        )
    }
    
    private func updateRotationMatrix() {
        if let motion = motionManager.deviceMotion {
            let attitude = motion.attitude
            
            // Convert quaternion to rotation matrix
            let x = Float(attitude.quaternion.x)
            let y = Float(attitude.quaternion.y)
            let z = Float(attitude.quaternion.z)
            let w = Float(attitude.quaternion.w)
            
            rotationMatrix = matrix_float4x4(
                SIMD4<Float>(1 - 2*y*y - 2*z*z, 2*x*y + 2*w*z, 2*x*z - 2*w*y, 0),
                SIMD4<Float>(2*x*y - 2*w*z, 1 - 2*x*x - 2*z*z, 2*y*z + 2*w*x, 0),
                SIMD4<Float>(2*x*z + 2*w*y, 2*y*z - 2*w*x, 1 - 2*x*x - 2*y*y, 0),
                SIMD4<Float>(0, 0, 0, 1)
            )
        }
    }
    
    @objc private func closePressed() {
        displayLink?.invalidate()
        player?.pause()
        motionManager.stopDeviceMotionUpdates()
        dismiss(animated: true, completion: nil)
    }
    
    override func viewDidDisappear(_ animated: Bool) {
        super.viewDidDisappear(animated)
        displayLink?.invalidate()
        player?.pause()
        motionManager.stopDeviceMotionUpdates()
    }
    
    override var prefersStatusBarHidden: Bool {
        return true
    }
    
    override var prefersHomeIndicatorAutoHidden: Bool {
        return true
    }
}

// MARK: - MTKViewDelegate
extension VRPlayerViewController: MTKViewDelegate {
    
    func mtkView(_ view: MTKView, drawableSizeWillChange size: CGSize) {
        // Update projection matrices when view size changes
        setupProjectionMatrices()
    }
    
    func draw(in view: MTKView) {
        updateRotationMatrix()
        
        guard let drawable = view.currentDrawable,
              let commandBuffer = commandQueue.makeCommandBuffer(),
              let renderPassDescriptor = view.currentRenderPassDescriptor else { return }
        
        renderPassDescriptor.colorAttachments[0].clearColor = MTLClearColor(red: 0, green: 0, blue: 0, alpha: 1)
        renderPassDescriptor.colorAttachments[0].loadAction = .clear
        
        guard let renderEncoder = commandBuffer.makeRenderCommandEncoder(descriptor: renderPassDescriptor) else { return }
        
        renderEncoder.setRenderPipelineState(renderPipelineState)
        renderEncoder.setVertexBuffer(vertexBuffer, offset: 0, index: 0)
        
        if let texture = videoTexture {
            renderEncoder.setFragmentTexture(texture, index: 0)
        }
        
        let viewSize = view.drawableSize
        let eyeWidth = Int(viewSize.width / 2)
        let eyeHeight = Int(viewSize.height)
        
        // Render left eye
        renderEncoder.setViewport(MTLViewport(
            originX: 0, originY: 0,
            width: Double(eyeWidth), height: Double(eyeHeight),
            znear: 0.0, zfar: 1.0
        ))
        renderEye(encoder: renderEncoder, projectionMatrix: leftProjectionMatrix, eyeOffset: -eyeSeparation / 2)
        
        // Render right eye
        renderEncoder.setViewport(MTLViewport(
            originX: Double(eyeWidth), originY: 0,
            width: Double(eyeWidth), height: Double(eyeHeight),
            znear: 0.0, zfar: 1.0
        ))
        renderEye(encoder: renderEncoder, projectionMatrix: rightProjectionMatrix, eyeOffset: eyeSeparation / 2)
        
        renderEncoder.endEncoding()
        commandBuffer.present(drawable)
        commandBuffer.commit()
    }
    
    private func renderEye(encoder: MTLRenderCommandEncoder, projectionMatrix: matrix_float4x4, eyeOffset: Float) {
        // Create view matrix with eye offset
        var viewMatrix = matrix_identity_float4x4
        viewMatrix.columns.3.x = eyeOffset
        
        // Combine matrices: MVP = Projection * View * Rotation
        var mvpMatrix = matrix_multiply(matrix_multiply(projectionMatrix, viewMatrix), rotationMatrix)
        
        // Upload matrix
        encoder.setVertexBytes(&mvpMatrix, length: MemoryLayout<matrix_float4x4>.size, index: 2)
        
        // Draw sphere
        encoder.drawIndexedPrimitives(
            type: .triangle,
            indexCount: indexCount,
            indexType: .uint16,
            indexBuffer: indexBuffer,
            indexBufferOffset: 0
        )
    }
}
