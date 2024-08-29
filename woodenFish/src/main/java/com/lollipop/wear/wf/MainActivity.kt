/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.lollipop.wear.wf

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.lollipop.wear.devices.TimeViewDelegate
import com.lollipop.wear.wf.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val minuteTimerDelegate by lazy {
        TimeViewDelegate { value ->
            binding.timeView.text = value
        }
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
    }

    private fun initPopup() {
        binding.muyuPopupView.popupDuration = 1000L
        val plusOne = AppCompatResources.getDrawable(this, R.drawable.baseline_plus_one_24)
        plusOne!!.setTint(Color.WHITE)
        binding.muyuPopupView.setPopupDrawable(plusOne)
        val dp24 = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            24F,
            resources.displayMetrics
        ).toInt()
        binding.muyuPopupView.setPopupSize(dp24, dp24)
        binding.muyuPopupView.popupOffsetLength = dp24 * 2
    }

    private fun onMuyuClick() {
        // TODO
    }

    override fun onResume() {
        super.onResume()
        minuteTimerDelegate.onResume()
    }

    override fun onPause() {
        super.onPause()
        minuteTimerDelegate.onPause()
    }

}