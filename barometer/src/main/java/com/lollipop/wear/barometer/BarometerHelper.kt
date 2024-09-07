package com.lollipop.wear.barometer

import android.hardware.SensorManager

object BarometerHelper {

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