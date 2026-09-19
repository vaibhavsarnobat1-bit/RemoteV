package com.smartremote.pro.core.gesture

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.abs

class CompassPointDetector(
    private val context: Context,
    private val targetAzimuthDegrees: Float = 0f,
    private val toleranceDegrees: Float = 25f,
    private val onTargetPointed: () -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val rotationSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    private var isCurrentlyPointed = false
    private var lastTriggerTime = 0L

    fun start() {
        rotationSensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stop() {
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || event.sensor.type != Sensor.TYPE_ROTATION_VECTOR) return

        val rotationMatrix = FloatArray(9)
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

        val orientation = FloatArray(3)
        SensorManager.getOrientation(rotationMatrix, orientation)

        val azimuthRad = orientation[0]
        var azimuthDeg = Math.toDegrees(azimuthRad.toDouble()).toFloat()
        if (azimuthDeg < 0) azimuthDeg += 360f

        val diff = abs(azimuthDeg - targetAzimuthDegrees)
        val angularDist = if (diff > 180f) 360f - diff else diff

        val pointed = angularDist <= toleranceDegrees

        val now = System.currentTimeMillis()
        if (pointed && !isCurrentlyPointed && (now - lastTriggerTime > 3000L)) {
            isCurrentlyPointed = true
            lastTriggerTime = now
            onTargetPointed()
        } else if (!pointed) {
            isCurrentlyPointed = false
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
