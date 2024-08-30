/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.lollipop.wear.wf

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.lollipop.wear.devices.TimeViewDelegate
import com.lollipop.wear.wf.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        private const val SP_WF = "wf"
        private const val KEY_CLICK_COUNT = "CLICK_COUNT"
        private const val CLICK_DURATION = 800L
    }

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val mainThread by lazy {
        Handler(Looper.getMainLooper())
    }

    private val minuteTimerDelegate by lazy {
        TimeViewDelegate(mainHandler = mainThread) { value ->
            binding.timeView.text = value
        }
    }

    private var clickCount = 0

    private val saveInfoTask = Runnable {
        saveInfo()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.muyuPopupView.bindClickListener { x, y ->
            onMuyuClick()
            binding.muyuPopupView.addPopup(x, y)
        }
        initPopup()
        loadInfo()
        updateCountView()
    }

    private fun initPopup() {
        binding.muyuPopupView.popupDuration = CLICK_DURATION
        val plusOne = AppCompatResources.getDrawable(this, R.drawable.baseline_plus_one_24)
        plusOne!!.setTint(Color.WHITE)
        binding.muyuPopupView.setPopupDrawable(plusOne)
        val dp24 = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            36F,
            resources.displayMetrics
        ).toInt()
        binding.muyuPopupView.setPopupSize(dp24, dp24)
        binding.muyuPopupView.popupOffsetLength = dp24 * 2
    }

    private fun onMuyuClick() {
        clickCount++
        updateCountView()
        postSaveInfo()
    }

    override fun onResume() {
        super.onResume()
        minuteTimerDelegate.onResume()
    }

    override fun onPause() {
        super.onPause()
        minuteTimerDelegate.onPause()
    }

    private fun updateCountView() {
        runOnUiThread {
            binding.countView.text = clickCount.toString()
        }
    }

    private fun postSaveInfo() {
        mainThread.removeCallbacks(saveInfoTask)
        mainThread.postDelayed(saveInfoTask, CLICK_DURATION)
    }

    private fun loadInfo() {
        val sp = getSharedPreferences()
        clickCount = sp.getInt(KEY_CLICK_COUNT, 0)
        updateCountView()
    }

    private fun saveInfo() {
        val sp = getSharedPreferences()
        sp.edit().putInt(KEY_CLICK_COUNT, clickCount).apply()
    }

    private fun getSharedPreferences() = getSharedPreferences(SP_WF, MODE_PRIVATE)

}