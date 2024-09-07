package com.lollipop.wear.barometer

import android.hardware.SensorManager

object BarometerHelper {

    const val PRESSURE_MAX = 1100.0
    const val PRESSURE_MIN = 300.0
    const val ALTITUDE_MAX = 9000.0
    const val ALTITUDE_MIN = -800.0

    fun getAltitudeProgress(altitude: Float): Float {
        return ((altitude - ALTITUDE_MIN) / (ALTITUDE_MAX - ALTITUDE_MIN)).toFloat()
    }

    fun getPressureProgress(pressure: Float): Float {
        return ((pressure - PRESSURE_MIN) / (PRESSURE_MAX - PRESSURE_MIN)).toFloat()
    }

    fun getAltitude(pressure: Double): Double {
        return 44330000 * (1 - Math.pow((pressure / 1013.25), 1.0 / 5255.0))
    }

    fun getPressure(altitude: Double): Double {
        return 1013.25 * Math.pow(1 - (altitude / 44330000), 5255.0)
    }

    fun getSensorStateColor(accuracy: Int): Int {
        when (accuracy) {
            SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> {
                return R.color.sensor_status_accuracy_high
            }

            SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> {
                return R.color.sensor_status_accuracy_medium
            }

            SensorManager.SENSOR_STATUS_ACCURACY_LOW -> {
                return R.color.sensor_status_accuracy_low
            }

            SensorManager.SENSOR_STATUS_NO_CONTACT -> {
                return R.color.sensor_status_no_contact
            }

            SensorManager.SENSOR_STATUS_UNRELIABLE -> {
                return R.color.sensor_status_unreliable
            }

            else -> {
                return R.color.sensor_status_no_contact
            }
        }
    }

}