package com.lollipop.file.sender

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import com.lollipop.file.sender.databinding.ActivityScanBinding
import com.lollipop.file.sender.ftp.ConnectInfo
import com.lollipop.file.sender.ftp.FtpManager
import com.lollipop.file.sender.ftp.FtpUri
import com.lollipop.file.sender.ftp.RequestResult
import com.lollipop.file.sender.scan.FocusAnimationHelper
import com.lollipop.qr.BarcodeReader
import com.lollipop.qr.comm.BarcodeResult
import com.lollipop.qr.reader.OnBarcodeScanResultListener
import com.lollipop.wear.basic.DialogHelper

class ScanActivity : AppCompatActivity(), OnBarcodeScanResultListener {

    companion object {
        private const val REQUEST_CODE_PERMISSION_CAMERA = 1001
    }

    private val barcodeReader by lazy {
        BarcodeReader.fromCamera(this)
    }

    private val binding by lazy {
        ActivityScanBinding.inflate(layoutInflater)
    }

    private val focusAnimationHelper = FocusAnimationHelper {
        binding.focusView
    }

    private val log by lazy {
        FTPLog.with(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        initView()
        initCamera()
    }

    private fun initView() {
        WindowInsetsControllerCompat(window, window.decorView).let {
            it.isAppearanceLightStatusBars = false
            it.isAppearanceLightNavigationBars = false
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.widgetGroup) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.permissionButton.setOnClickListener {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CODE_PERMISSION_CAMERA
            )
        }
        binding.customButton.setOnClickListener {
            startActivity(Intent(this, CustomLoginActivity::class.java))
            finish()
        }
    }

    private fun initCamera() {
        barcodeReader.bindContainer(binding.previewContainer)
        barcodeReader.addOnFocusChangedListener(focusAnimationHelper)
        barcodeReader.addOnBarcodeScanResultListener(this)
    }

    override fun onResume() {
        super.onResume()
        binding.permissionHintPanel.isVisible = !hasPermission(Manifest.permission.CAMERA)
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onBarcodeScanResult(result: BarcodeResult) {
        barcodeReader.analyzerEnable = false
        val list = result.list
        var isFound = false
        for (barcode in list) {
            val rawValue = barcode.info.rawValue
            val ftpUri = FtpUri.parse(rawValue)
            if (ftpUri != FtpUri.EMPTY) {
                connect(ftpUri)
                isFound = true
                break
            }
        }
        if (!isFound) {
            barcodeReader.analyzerEnable = true
        }
    }

    private fun connect(ftpUri: FtpUri) {
        val connectInfo = ConnectInfo.fromUri(ftpUri)
        val loading = DialogHelper.loading(this, R.string.connecting)
        FtpManager.getOrCreate(connectInfo).connect { result ->
            loading.dismiss()
            val isSuccess = when (result) {
                is RequestResult.Success -> {
                    log.d("Connect success: ${result.data}")
                    result.data
                }

                is RequestResult.Failure -> {
                    log.e("Connect failed", result.error)
                    false
                }
            }
            if (isSuccess) {
                DialogHelper.alert(
                    context = this,
                    messageRes = R.string.connect_success,
                    positiveRes = R.string.ok,
                    onPositive = {
                        it.dismiss()
                        finish()
                    }
                )
            } else {
                DialogHelper.alert(
                    context = this,
                    messageRes = R.string.connect_failed,
                    positiveRes = R.string.retry,
                    negativeRes = R.string.label_custom_uri,
                    onPositive = {
                        it.dismiss()
                        barcodeReader.analyzerEnable = true
                    },
                    onNegative = {
                        it.dismiss()
                        CustomLoginActivity.start(this, ftpUri.getUrl())
                        finish()
                    }
                )
            }
        }
    }

}