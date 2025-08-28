package tellme.sairajpatil108.tellme360.android.renderer

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class VRRenderer(context: Context) : GLSurfaceView.Renderer {
    private val renderer = GL360Renderer(context)

    fun setOnSurfaceTextureReadyListener(listener: (SurfaceTexture) -> Unit) {
        renderer.setOnSurfaceTextureReadyListener(listener)
    }

    fun updateRotationMatrix(rotation: FloatArray) {
        renderer.updateRotationMatrix(rotation)
    }

    fun setVideoReady(ready: Boolean) {
        renderer.setVideoReady(ready)
    }

    // GLSurfaceView.Renderer implementation
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        renderer.onSurfaceCreated(gl, config)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        renderer.onSurfaceChanged(gl, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        renderer.onDrawFrame(gl)
    }
}




