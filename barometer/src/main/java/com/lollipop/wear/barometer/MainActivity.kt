package com.lollipop.wear.barometer

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import com.lollipop.wear.barometer.databinding.ActivityMainBinding
import com.lollipop.wear.basic.BasicFragment
import com.lollipop.wear.basic.Page
import com.lollipop.wear.basic.PagerAdapter
import com.lollipop.wear.basic.findTypedFragment


class MainActivity : AppCompatActivity(), SensorEventListener, CurrentFragment.Callback,
    HistoryFragment.Callback {

    // sweepAngle = 140
    // startAngle = 185
    // 300hPa = 140 * 0.1 + 185 + 90 = 289
    // 1100hPa = 140 * 0.9 + 185 + 90 = 41


    // startAngle = 35
    // sweepAngle = 140
    // -800m = 140 * 0.9 + 35 + 90 = 251
    // 9000m = 140 * 0.1 + 35 + 90 = 139

    companion object {
        private const val SAVE_INTERVAL = 10 * 1000L
    }

    private var currentSensor: Sensor? = null

    private var currentSensorState = SensorManager.SENSOR_STATUS_NO_CONTACT
    private var currentPressure = 0F
    private var currentAltitude = 0F

    private var lastSaveTime = 0L

    private val historyDatabase by lazy {
        HistoryDatabase(this)
    }

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initSensor()
        binding.noSensorPanel.isVisible = currentSensor == null
        binding.viewPager.adapter = PagerAdapter(
            this,
            listOf(
                SubPage.State,
                SubPage.History
            )
        )
    }

    private fun initSensor() {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE)
        if (sensorManager is SensorManager) {
            currentSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        }
    }

    override fun onResume() {
        super.onResume()
        currentSensor?.let { s ->
            val sensorManager = getSystemService(Context.SENSOR_SERVICE)
            if (sensorManager is SensorManager) {
                sensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        currentSensor?.let {
            val sensorManager = getSystemService(Context.SENSOR_SERVICE)
            if (sensorManager is SensorManager) {
                sensorManager.unregisterListener(this)
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
        val s = currentSensor ?: return
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            if (event.sensor.type == s.type) {
                val pressure = event.values[0]
                val altitude = BarometerHelper.getAltitude(pressure.toDouble()).toFloat()
                currentPressure = pressure
                currentAltitude = altitude
                supportFragmentManager.findTypedFragment<CurrentFragment>()?.let {
                    if (it.isResumed) {
                        it.onPressureChanged(pressure, altitude)
                    }
                }
                savePressure(pressure, altitude)
            }
        }
    }

    private fun savePressure(pressure: Float, altitude: Float) {

        val now = System.currentTimeMillis()
        if (now - lastSaveTime < SAVE_INTERVAL) {
            return
        }
        lastSaveTime = now

        val info = PressureInfo(
            sensorState = currentSensorState,
            pressure = pressure,
            altitude = altitude,
            time = now
        )
        historyDatabase.insert(
            info
        )
        supportFragmentManager.findTypedFragment<HistoryFragment>()?.let {
            if (it.isResumed) {
                it.onNewPressure(info)
            }
        }
    }

    private fun resetSaveTime() {
        lastSaveTime = 0L
    }

    /**
     *     /**
     *       * The values returned by this sensor cannot be trusted because the sensor
     *       * had no contact with what it was measuring (for example, the heart rate
     *       * monitor is not in contact with the user).
     *       */
     *     public static final int SENSOR_STATUS_NO_CONTACT = -1;
     *
     *     /**
     *      * The values returned by this sensor cannot be trusted, calibration is
     *      * needed or the environment doesn't allow readings
     *      */
     *     public static final int SENSOR_STATUS_UNRELIABLE = 0;
     *
     *     /**
     *      * This sensor is reporting data with low accuracy, calibration with the
     *      * environment is needed
     *      */
     *     public static final int SENSOR_STATUS_ACCURACY_LOW = 1;
     *
     *     /**
     *      * This sensor is reporting data with an average level of accuracy,
     *      * calibration with the environment may improve the readings
     *      */
     *     public static final int SENSOR_STATUS_ACCURACY_MEDIUM = 2;
     *
     *     /** This sensor is reporting data with maximum accuracy */
     *     public static final int SENSOR_STATUS_ACCURACY_HIGH = 3;
     */
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        val s = this.currentSensor ?: return
        sensor ?: return
        if (s.type == sensor.type) {
            currentSensorState = accuracy
            // 如果传感器状态发生变化，那么我们需要让它尽快保存，所以清空上一次的保存时间
            resetSaveTime()
            supportFragmentManager.findTypedFragment<CurrentFragment>()?.let {
                if (it.isResumed) {
                    it.onSensorStatusChanged(accuracy)
                }
            }
        }
    }

    private enum class SubPage(override val clazz: Class<out BasicFragment>) : Page {
        State(CurrentFragment::class.java),
        History(HistoryFragment::class.java),
    }

    override fun getSensorStatus(): Int {
        return currentSensorState
    }

    override fun getPressure(): Float {
        return currentPressure
    }

    override fun getAltitude(): Float {
        return currentAltitude
    }

    override fun loadMore(lastTime: Long, callback: (List<PressureInfo>) -> Unit) {
        historyDatabase.select(lastTime, 100, callback)
    }

}