package com.example.rs_link.core.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.PI

class CompassManager(context: Context) {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    // Returns a Flow of the Azimuth (Direction in Degrees: 0-360)
    fun getAzimuth(): Flow<Float> = callbackFlow {
        val listener = object : SensorEventListener {
            private var gravity: FloatArray? = null
            private var geomagnetic: FloatArray? = null

            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) gravity = event.values
                if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) geomagnetic = event.values

                if (gravity != null && geomagnetic != null) {
                    val r = FloatArray(9)
                    val i = FloatArray(9)

                    if (SensorManager.getRotationMatrix(r, i, gravity, geomagnetic)) {
                        val orientation = FloatArray(3)
                        SensorManager.getOrientation(r, orientation)

                        // Convert radians to degrees
                        val azimuthInRadians = orientation[0]
                        var azimuthInDegrees = Math.toDegrees(azimuthInRadians.toDouble()).toFloat()

                        // Normalize to 0-360
                        if (azimuthInDegrees < 0) azimuthInDegrees += 360f

                        trySend(azimuthInDegrees)
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(listener, magnetometer, SensorManager.SENSOR_DELAY_UI)

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}