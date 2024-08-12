/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.lollipop.wear.ttt

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.lollipop.wear.ttt.databinding.ActivityMainBinding
import com.lollipop.wear.ttt.ui.GameBoardFragment
import com.lollipop.wear.ttt.ui.GameRecordFragment
import com.lollipop.wear.ttt.ui.GameStateFragment
import com.lollipop.wear.ttt.ui.GameThemeFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    companion object {
        private val pageList: Array<Class<out Fragment>> = arrayOf(
            GameStateFragment::class.java,
            GameBoardFragment::class.java,
            GameRecordFragment::class.java,
            GameThemeFragment::class.java,
        )
    }

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    private val mainHandler by lazy {
        Handler(mainLooper)
    }

    private val minuteTimer by lazy {
        MinuteTimer(mainHandler) {
            updateTime()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.viewPager.adapter = FragmentAdapter(this)
        binding.viewPager.currentItem = 1
        binding.pageIndicator.indicatorCount = pageList.size
        binding.viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    binding.pageIndicator.indicatorIndex = position
                    binding.pageIndicator.indicatorOffset = 0F
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    binding.pageIndicator.indicatorIndex = position
                    binding.pageIndicator.indicatorOffset = positionOffset
                }
            }
        )
    }

    private fun updateTime() {
        binding.timeView.text = dateFormat.format(Date(System.currentTimeMillis()))
    }

    override fun onResume() {
        super.onResume()
        updateTime()
        minuteTimer.onResume()
    }

    override fun onPause() {
        super.onPause()
        minuteTimer.onPause()
    }

    private class FragmentAdapter(
        activity: AppCompatActivity
    ) : FragmentStateAdapter(activity) {

        private val fragmentList = pageList

        override fun getItemCount(): Int {
            return fragmentList.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragmentList[position].getDeclaredConstructor().newInstance()
        }
    }

    private class MinuteTimer(
        private val handler: Handler,
        private val callback: () -> Unit
    ) {

        companion object {
            private const val MINUTE = 60 * 1000L
        }

        private val updateTask = Runnable {
            next()
            callback()
        }

        fun onResume() {
            next()
        }

        private fun next() {
            val now = now()
            val offset = now % MINUTE
            handler.removeCallbacks(updateTask)
            handler.postDelayed(updateTask, MINUTE - offset)
        }

        fun onPause() {
            handler.removeCallbacks(updateTask)
        }

        private fun now(): Long {
            return System.currentTimeMillis()
        }

    }

}

