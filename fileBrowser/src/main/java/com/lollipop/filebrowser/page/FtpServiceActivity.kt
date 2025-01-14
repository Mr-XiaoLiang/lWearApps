package com.lollipop.filebrowser.page

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.wear.widget.CurvedTextView
import be.ppareit.swiftp.FsService
import be.ppareit.swiftp.FsSettings
import com.lollipop.filebrowser.R
import com.lollipop.filebrowser.databinding.ActivityFtpServiceBinding
import com.lollipop.qr.writer.BarcodeWriter

class FtpServiceActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityFtpServiceBinding.inflate(layoutInflater)
    }

    private val timerHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    private val screensaverTask = Runnable {
        updateScreensaver()
    }

    private val qrDelegate by lazy {
        QrDelegate(binding.qrCodeView, binding.pathView, BarcodeWriter(lifecycle))
    }

    private var fsActionsReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == null) {
                return
            }
            updateRunningState()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding.connectStatusView.isVisible = false
        updateQrCode(getString(R.string.app_name))

        binding.maskPanel.setOnClickListener {
            hideScreensaver()
        }

        registerFsReceiver()

        binding.loginInfoView.text = getString(
            R.string.ftp_login_info,
            getString(com.lollipop.swiftp.R.string.username_default),
            getString(com.lollipop.swiftp.R.string.password_default)
        )
        FsService.start()
    }

    override fun onResume() {
        super.onResume()
        updateRunningState()
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerFsReceiver() {
        val filter = IntentFilter()
        filter.addAction(FsService.ACTION_STARTED)
        filter.addAction(FsService.ACTION_STOPPED)
        filter.addAction(FsService.ACTION_FAILED_TO_START)
        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(fsActionsReceiver, filter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(fsActionsReceiver, filter)
        }
    }

    override fun onDestroy() {
        unregisterReceiver(fsActionsReceiver)
        FsService.stop()
        super.onDestroy()
    }

    private fun updateQrCode(content: String) {
        qrDelegate.updateQrCode(content)
    }

    private fun updateRunningState() {
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            return
        }
        val running = FsService.isRunning()
        if (running) {
            postScreensaverTask()
            val address = FsService.getLocalInetAddress()
            binding.connectStatusView.isVisible = false
            updateQrCode("ftp://${address.hostAddress}:${FsSettings.getPortNumber()}")
        } else {
            cancelScreensaverTask()
            hideScreensaver()
            qrDelegate.hideQr(getString(R.string.hint_ftp_service_error))
            binding.connectStatusView.isVisible = true
        }
    }

    private fun postScreensaverTask() {
        timerHandler.removeCallbacks(screensaverTask)
        if (FsService.isRunning()) {
            // 30S后进入屏保
            timerHandler.postDelayed(screensaverTask, 1000L * 30)
        }
    }

    private fun cancelScreensaverTask() {
        timerHandler.removeCallbacks(screensaverTask)
    }

    private fun hideScreensaver() {
        binding.maskPanel.isVisible = false
        postScreensaverTask()
    }

    private fun updateScreensaver() {
        binding.maskPanel.isVisible = FsService.isRunning()
    }

    private class QrDelegate(
        val qrView: ImageView,
        val labelView: CurvedTextView,
        val qrWriter: BarcodeWriter
    ) {

        private var currentMode = 0

        fun updateQrCode(content: String) {
            updateMode()
            labelView.text = content
            val localMode = currentMode
            qrView.post {
                qrWriter.encode(content).size(qrView.width).loadBitmap { result ->
                    onQrResult(localMode, result)
                }
            }
        }

        private fun onQrResult(mode: Int, result: Result<Bitmap>) {
            if (mode != currentMode) {
                return
            }
            qrView.setImageBitmap(result.getOrNull())
        }

        fun hideQr(label: String) {
            updateMode()
            labelView.text = label
            qrView.setImageBitmap(null)
        }

        private fun updateMode() {
            currentMode++
            if (currentMode == Int.MAX_VALUE) {
                currentMode = 0
            }
        }

    }

}