package tellme.sairajpatil108.tellme360.android.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.opengl.Matrix

/**
 * VR Sensor Manager for Head Tracking
 * 
 * Key Principles:
 * 1. Use ROTATION_VECTOR sensor for accurate head tracking
 * 2. Proper coordinate system remapping for VR
 * 3. Calibration to set reference orientation
 * 4. Clean matrix transformations
 */
class VRSensorManager(
    private val context: Context,
    private val listener: Listener? = null
) : SensorEventListener {
    interface Listener {
        fun onOrientationChanged(rotationMatrix: FloatArray)
    }

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var rotationSensor: Sensor? = null
    private val rotationMatrix = FloatArray(16)
    private val deviceOrientationMatrix = FloatArray(16)
    private val calibrationMatrix = FloatArray(16)
    private val tempMatrix = FloatArray(16)
    private var isCalibrated = false

    init {
        // Use ROTATION_VECTOR for most accurate head tracking
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        if (rotationSensor == null) {
            // Fallback to GAME_ROTATION_VECTOR if ROTATION_VECTOR not available
            rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR)
        }
        
        // Initialize matrices
        Matrix.setIdentityM(rotationMatrix, 0)
        Matrix.setIdentityM(deviceOrientationMatrix, 0)
        Matrix.setIdentityM(calibrationMatrix, 0)
    }
    
    /**
     * Calibrate head tracking to current orientation
     */
    fun calibrate() {
        if (deviceOrientationMatrix.any { it != 0f }) {
            Matrix.invertM(calibrationMatrix, 0, deviceOrientationMatrix, 0)
            isCalibrated = true
        }
    }
    
    /**
     * Reset calibration
     */
    fun resetCalibration() {
        Matrix.setIdentityM(calibrationMatrix, 0)
        isCalibrated = false
    }

    fun register() {
        rotationSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun unregister() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR ||
            event.sensor.type == Sensor.TYPE_GAME_ROTATION_VECTOR) {
            
            // Get rotation matrix from sensor
            SensorManager.getRotationMatrixFromVector(deviceOrientationMatrix, event.values)
            
            // Apply calibration if calibrated
            val orientationMatrix = if (isCalibrated) {
                Matrix.multiplyMM(tempMatrix, 0, deviceOrientationMatrix, 0, calibrationMatrix, 0)
                tempMatrix
            } else {
                deviceOrientationMatrix
            }
            
            // Remap coordinate system for landscape VR applications
            // This is the industry standard remapping for VR in landscape mode
            // Based on Android documentation: "Using the device as a mechanical compass when rotation is Surface.ROTATION_90"
            SensorManager.remapCoordinateSystem(
                orientationMatrix,
                SensorManager.AXIS_Y,           // Device Y becomes World X (left/right)
                SensorManager.AXIS_MINUS_X,     // Device -X becomes World Y (up/down)
                rotationMatrix
            )
            
            listener?.onOrientationChanged(rotationMatrix.copyOf())
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed for VR head tracking
    }
}

