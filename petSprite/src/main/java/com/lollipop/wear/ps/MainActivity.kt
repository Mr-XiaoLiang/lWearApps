package com.lollipop.wear.ps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.lollipop.wear.ps.databinding.ActivityMainBinding

class MainActivity : ComponentActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.coronaView.init("corona/")
    }

    override fun onResume() {
        super.onResume()
        binding.coronaView.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.coronaView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.coronaView.destroy()
    }

}