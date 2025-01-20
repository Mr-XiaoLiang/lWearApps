package com.lollipop.file.sender

import android.Manifest
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
import com.lollipop.file.sender.scan.FocusAnimationHelper
import com.lollipop.qr.BarcodeReader
import com.lollipop.qr.comm.BarcodeResult
import com.lollipop.qr.reader.OnBarcodeScanResultListener

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
            // TODO("Not yet implemented")
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
        // TODO("Not yet implemented")

    }

}