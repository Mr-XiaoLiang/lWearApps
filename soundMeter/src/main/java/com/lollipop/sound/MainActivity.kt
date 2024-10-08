package com.lollipop.sound

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.lollipop.sound.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
//        initSensor()
//        binding.noSensorPanel.isVisible = currentSensor == null
//        binding.viewPager.adapter = PagerAdapter(
//            this,
//            listOf(
//                SubPage.State,
//                SubPage.History
//            )
//        )
    }

}