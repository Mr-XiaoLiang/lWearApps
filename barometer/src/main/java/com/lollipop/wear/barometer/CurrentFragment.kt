package com.lollipop.wear.barometer

import android.content.Context
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lollipop.wear.barometer.databinding.FragmentCurrentBinding
import com.lollipop.wear.basic.BasicFragment
import java.text.DecimalFormat

/**
 *     // sweepAngle = 140
 *     // startAngle = 185
 *     // 300hPa = 140 * 0.1 + 185 + 90 = 289
 *     // 1100hPa = 140 * 0.9 + 185 + 90 = 41
 *
 *
 *     // startAngle = 35
 *     // sweepAngle = 140
 *     // -800m = 140 * 0.9 + 35 + 90 = 251
 *     // 9000m = 140 * 0.1 + 35 + 90 = 139
 */
class CurrentFragment : BasicFragment() {

    companion object {
        const val PRESSURE_MAX = 1100.0
        const val PRESSURE_MIN = 300.0
        const val ALTITUDE_MAX = 9000.0
        const val ALTITUDE_MIN = -800.0
    }

    private var callback: Callback? = null
    private val binding by lazy {
        FragmentCurrentBinding.inflate(layoutInflater)
    }
    private val decimalFormat = DecimalFormat("0.00")

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = findCallback(context)
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val c = callback
        if (c != null) {
            onSensorStatusChanged(c.getSensorStatus())
            onPressureChanged(c.getPressure(), c.getAltitude())
        } else {
            onSensorStatusChanged(SensorManager.SENSOR_STATUS_NO_CONTACT)
            onPressureChanged(BarometerHelper.getPressure(0.0).toFloat(), 0F)
        }
    }

    fun onSensorStatusChanged(accuracy: Int) {
        if (!isResumed) {
            return
        }
        when (accuracy) {
            SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> {
                binding.sensorStatePoint.setBackgroundResource(R.color.sensor_status_accuracy_high)
                binding.sensorStatePoint.setText(R.string.sensor_status_accuracy_high)
            }

            SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> {
                binding.sensorStatePoint.setBackgroundResource(R.color.sensor_status_accuracy_medium)
                binding.sensorStatePoint.setText(R.string.sensor_status_accuracy_medium)
            }

            SensorManager.SENSOR_STATUS_ACCURACY_LOW -> {
                binding.sensorStatePoint.setBackgroundResource(R.color.sensor_status_accuracy_low)
                binding.sensorStatePoint.setText(R.string.sensor_status_accuracy_low)
            }

            SensorManager.SENSOR_STATUS_NO_CONTACT -> {
                binding.sensorStatePoint.setBackgroundResource(R.color.sensor_status_no_contact)
                binding.sensorStatePoint.setText(R.string.sensor_status_no_contact)
            }

            SensorManager.SENSOR_STATUS_UNRELIABLE -> {
                binding.sensorStatePoint.setBackgroundResource(R.color.sensor_status_unreliable)
                binding.sensorStatePoint.setText(R.string.sensor_status_unreliable)
            }

            else -> {
                binding.sensorStatePoint.setBackgroundResource(R.color.sensor_status_no_contact)
                binding.sensorStatePoint.setText(R.string.sensor_status_no_contact)
            }
        }
    }

    fun onPressureChanged(pressure: Float, altitude: Float) {
        if (!isResumed) {
            return
        }
        binding.pressureValueView.text = decimalFormat.format(pressure)
        binding.altitudeValueView.text = decimalFormat.format(altitude)
        var pressureProgress = ((pressure - PRESSURE_MIN) / (PRESSURE_MAX - PRESSURE_MIN)).toFloat()
        if (pressureProgress < 0) {
            pressureProgress = 0F
        }
        if (pressureProgress > 1) {
            pressureProgress = 1F
        }
        binding.pressureProgressIndicator.progress = pressureProgress
        var altitudeProgress = ((altitude - ALTITUDE_MIN) / (ALTITUDE_MAX - ALTITUDE_MIN)).toFloat()
        if (altitudeProgress < 0) {
            altitudeProgress = 0F
        }
        if (altitudeProgress > 1) {
            altitudeProgress = 1F
        }
        binding.altitudeProgressIndicator.progress = altitudeProgress
    }

    interface Callback {

        fun getSensorStatus(): Int

        fun getPressure(): Float

        fun getAltitude(): Float

    }

}