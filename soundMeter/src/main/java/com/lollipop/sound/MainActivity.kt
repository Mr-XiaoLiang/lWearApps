package com.lollipop.sound

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import com.lollipop.sound.databinding.ActivityMainBinding
import com.lollipop.wear.devices.TimeViewDelegate
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {

    companion object {
        private const val CODE_REQUEST_PERMISSION = 233
    }

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val volumeDelegate = VolumeDelegate(this) { pcm, volume ->
        updateVolume(pcm, volume)
    }

    private val minuteTimerDelegate by lazy {
        TimeViewDelegate { value ->
            binding.timeView.text = value
        }
    }

    private val decimalFormat = DecimalFormat("0.00")

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        volumeDelegate.setEnable(true)
        binding.permissionPanel.setOnClickListener { }
        binding.permissionAllowButton.setOnClickListener {
            requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO), CODE_REQUEST_PERMISSION)
        }
        binding.hintPanel.setOnClickListener { }
        binding.hintCloseButton.setOnClickListener {
            binding.hintPanel.isVisible = false
        }
        binding.infoButton.setOnClickListener {
            binding.hintPanel.isVisible = true
        }
    }

    override fun onResume() {
        super.onResume()
        minuteTimerDelegate.onResume()
        if (volumeDelegate.hasPermission()) {
            volumeDelegate.onStart()
            binding.permissionPanel.isVisible = false
        } else {
            binding.permissionPanel.isVisible = true
        }
    }

    override fun onPause() {
        super.onPause()
        minuteTimerDelegate.onPause()
        volumeDelegate.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        volumeDelegate.setEnable(false)
        volumeDelegate.onDestroy()
    }

    private fun updateVolume(pcm: Float, volume: Float) {
        binding.dbValueView.text = decimalFormat.format(volume)
        binding.pcmValueView.text = decimalFormat.format(pcm)
        val volumeProgress = VolumeDelegate.getVolumeProgress(volume)
        binding.dbProgressIndicator.progress = volumeProgress
        binding.volumeSpectrumView.push(volumeProgress)
    }

}