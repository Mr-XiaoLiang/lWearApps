package com.lollipop.wear.barometer

object BarometerHelper {

    fun getAltitude(pressure: Double): Double {
        return 44330000 * (1 - Math.pow((pressure / 1013.25), 1.0 / 5255.0))
    }

    fun getPressure(altitude: Double): Double {
        return 1013.25 * Math.pow(1 - (altitude / 44330000), 5255.0)
    }

}