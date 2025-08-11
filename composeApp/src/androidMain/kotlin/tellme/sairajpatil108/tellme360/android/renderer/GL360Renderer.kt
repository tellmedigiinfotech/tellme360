package tellme.sairajpatil108.tellme360.android.renderer

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.GLES11Ext
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class GL360Renderer(private val context: Context) : GLSurfaceView.Renderer {
    
    companion object {
        private const val TAG = "GL360Renderer"
        
        // Vertex shader for 360-degree sphere
        private const val VERTEX_SHADER = """
            uniform mat4 uMVPMatrix;
            uniform mat4 uRotationMatrix;
            attribute vec4 aPosition;
            attribute vec2 aTexCoord;
            varying vec2 vTexCoord;
            void main() {
                gl_Position = uMVPMatrix * uRotationMatrix * aPosition;
                vTexCoord = aTexCoord;
            }
        """
        
        // Fragment shader for texture mapping using external OES (SurfaceTexture)
        private const val FRAGMENT_SHADER = """
            #extension GL_OES_EGL_image_external : require
            precision mediump float;
            uniform samplerExternalOES uTexture;
            varying vec2 vTexCoord;
            void main() {
                gl_FragColor = texture2D(uTexture, vTexCoord);
            }
        """
    }
    
    private var mProgram = 0
    private var muMVPMatrixHandle = 0
    private var muRotationMatrixHandle = 0
    private var maPositionHandle = 0
    private var maTexCoordHandle = 0
    private var muTextureHandle = 0
    
    private val mMVPMatrix = FloatArray(16)
    private val mProjectionMatrix = FloatArray(16)
    private val mModelViewMatrix = FloatArray(16)
    private val mRotationMatrix = FloatArray(16)
    private val mTempMatrix = FloatArray(16)

    // Screen dimensions and stereo config
    private var screenWidth: Int = 0
    private var screenHeight: Int = 0
    private val eyeSeparation: Float = 0.064f

    // Initial video orientation adjustments based on working implementation
    private val initialVideoRotationY = -270f
    private val initialVideoRotationX = 0f
    private val initialVideoRotationZ = 270f
    
    private var sphereVertices: FloatBuffer? = null
    private var sphereTextureCoords: FloatBuffer? = null
    private var sphereIndexBuffer: java.nio.ShortBuffer? = null
    private var sphereIndexCount = 0
    
    private var surfaceTexture: SurfaceTexture? = null
    private var textureId = -1
    private var onSurfaceTextureReadyListener: ((SurfaceTexture) -> Unit)? = null
    private var videoReady = false
    
    init {
        // Initialize rotation matrix
        Matrix.setIdentityM(mRotationMatrix, 0)
    }
    
    fun setOnSurfaceTextureReadyListener(listener: (SurfaceTexture) -> Unit) {
        onSurfaceTextureReadyListener = listener
    }
    
    fun updateRotationMatrix(rotation: FloatArray) {
        System.arraycopy(rotation, 0, mRotationMatrix, 0, 16)
    }
    
    fun setVideoReady(ready: Boolean) {
        videoReady = ready
    }
    
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        Log.d(TAG, "Surface created")
        
        // Set background color to black
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        
        // Enable depth testing and back-face culling; indices are set for inside viewing
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthFunc(GLES20.GL_LEQUAL)
        GLES20.glEnable(GLES20.GL_CULL_FACE)
        GLES20.glCullFace(GLES20.GL_BACK)
        
        // Create and compile shaders
        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER)
        
        // Create program
        mProgram = GLES20.glCreateProgram().also { program ->
            GLES20.glAttachShader(program, vertexShader)
            GLES20.glAttachShader(program, fragmentShader)
            GLES20.glLinkProgram(program)
        }
        
        // Get handles to shader variables
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition")
        maTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoord")
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix")
        muRotationMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uRotationMatrix")
        muTextureHandle = GLES20.glGetUniformLocation(mProgram, "uTexture")
        
        // Generate sphere geometry
        generateSphere()
        
        // Create texture
        createTexture()
    }
    
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        Log.d(TAG, "Surface changed: ${width}x${height}")
        
        GLES20.glViewport(0, 0, width, height)

        screenWidth = width
        screenHeight = height

        // Per-eye aspect ratio
        val aspect = (width.toFloat() / 2f) / height.toFloat()
        Matrix.perspectiveM(mProjectionMatrix, 0, 90f, aspect, 0.5f, 500f)
    }
    
    override fun onDrawFrame(gl: GL10?) {
        // Clear the screen
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)

        // Render left and right eyes
        renderEye(0)
        renderEye(1)

        // Update surface texture if available
        surfaceTexture?.updateTexImage()

    }

    private fun renderEye(eye: Int) {
        // Set viewport to half screen for each eye
        val eyeWidth = screenWidth / 2
        val eyeX = eye * eyeWidth
        GLES20.glViewport(eyeX, 0, eyeWidth, screenHeight)

        // Build view matrix: camera at origin, apply eye offset
        Matrix.setIdentityM(mModelViewMatrix, 0)
        val offset = if (eye == 0) -eyeSeparation / 2f else eyeSeparation / 2f
        Matrix.translateM(mModelViewMatrix, 0, offset, 0f, 0f)

        // MVP = Projection * View
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mModelViewMatrix, 0)

        // Include head rotation
        Matrix.multiplyMM(mTempMatrix, 0, mMVPMatrix, 0, mRotationMatrix, 0)

        // Initial content orientation
        val rotatedModelMatrix = FloatArray(16)
        Matrix.setIdentityM(rotatedModelMatrix, 0)
        Matrix.rotateM(rotatedModelMatrix, 0, initialVideoRotationY, 0f, 1f, 0f)
        Matrix.rotateM(rotatedModelMatrix, 0, initialVideoRotationX, 1f, 0f, 0f)
        Matrix.rotateM(rotatedModelMatrix, 0, initialVideoRotationZ, 0f, 0f, 1f)

        // Use program and bind texture
        GLES20.glUseProgram(mProgram)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId)
        GLES20.glUniform1i(muTextureHandle, 0)

        // Upload matrices
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mTempMatrix, 0)
        GLES20.glUniformMatrix4fv(muRotationMatrixHandle, 1, false, rotatedModelMatrix, 0)

        // Attributes
        GLES20.glEnableVertexAttribArray(maPositionHandle)
        GLES20.glEnableVertexAttribArray(maTexCoordHandle)
        sphereVertices?.let { vertices ->
            GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertices)
        }
        sphereTextureCoords?.let { texCoords ->
            GLES20.glVertexAttribPointer(maTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0, texCoords)
        }

        // Draw
        sphereIndexBuffer?.let { indicesBuf ->
            indicesBuf.position(0)
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, sphereIndexCount, GLES20.GL_UNSIGNED_SHORT, indicesBuf)
        }

        // Disable
        GLES20.glDisableVertexAttribArray(maPositionHandle)
        GLES20.glDisableVertexAttribArray(maTexCoordHandle)
    }
    
    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }
    
    private fun generateSphere() {
        val radius = 50.0f
        val stacks = 32
        val slices = 64

        val vertices = mutableListOf<Float>()
        val textureCoords = mutableListOf<Float>()
        val indices = mutableListOf<Short>()

        // Generate vertices with duplicated seam column and both poles
        for (i in 0..stacks) {
            val latitude = Math.PI * (0.5 - i.toDouble() / stacks) // PI/2 to -PI/2
            val cosLat = Math.cos(latitude).toFloat()
            val sinLat = Math.sin(latitude).toFloat()

            for (j in 0..slices) {
                val longitude = 2.0 * Math.PI * j / slices
                val cosLon = Math.cos(longitude).toFloat()
                val sinLon = Math.sin(longitude).toFloat()

                val x = radius * cosLat * cosLon
                val y = radius * sinLat
                val z = radius * cosLat * sinLon

                // Position
                vertices.add(x)
                vertices.add(y)
                vertices.add(z)

                // UVs for equirectangular
                val u = j.toFloat() / slices
                val v = 1.0f - (i.toFloat() / stacks)
                textureCoords.add(u)
                textureCoords.add(v)
            }
        }

        // Generate indices with reversed winding for inside viewing
        val stride = slices + 1
        for (i in 0 until stacks) {
            for (j in 0 until slices) {
                val first = (i * stride + j).toShort()
                val second = (first + stride).toShort()

                indices.add(first)
                indices.add(second)
                indices.add((first + 1).toShort())

                indices.add((first + 1).toShort())
                indices.add(second)
                indices.add((second + 1).toShort())
            }
        }

        // Create buffers
        val vertexBuffer = ByteBuffer.allocateDirect(vertices.size * 4).order(ByteOrder.nativeOrder())
        sphereVertices = vertexBuffer.asFloatBuffer().apply {
            put(vertices.toFloatArray())
            position(0)
        }

        val texCoordBuffer = ByteBuffer.allocateDirect(textureCoords.size * 4).order(ByteOrder.nativeOrder())
        sphereTextureCoords = texCoordBuffer.asFloatBuffer().apply {
            put(textureCoords.toFloatArray())
            position(0)
        }

        val indexByteBuffer = ByteBuffer.allocateDirect(indices.size * 2).order(ByteOrder.nativeOrder())
        sphereIndexBuffer = indexByteBuffer.asShortBuffer().apply {
            put(indices.toShortArray())
            position(0)
        }
        sphereIndexCount = indices.size
    }
    
    private fun createTexture() {
        val textureIds = IntArray(1)
        GLES20.glGenTextures(1, textureIds, 0)
        textureId = textureIds[0]
        
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
        // Clamp to edge to avoid sampling outside and causing seams
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
        
        // Create SurfaceTexture for video rendering
        surfaceTexture = SurfaceTexture(textureId).apply {
            setOnFrameAvailableListener {
                // Frame is available, trigger redraw
            }
        }
        
        onSurfaceTextureReadyListener?.invoke(surfaceTexture!!)
    }
}

