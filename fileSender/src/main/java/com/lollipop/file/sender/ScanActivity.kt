package com.lollipop.file.sender

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.lollipop.file.sender.databinding.ActivityScanBinding
import com.lollipop.file.sender.scan.FocusAnimationHelper
import com.lollipop.qr.BarcodeReader
import com.lollipop.qr.comm.BarcodeResult
import com.lollipop.qr.reader.OnBarcodeScanResultListener

class ScanActivity : AppCompatActivity(), OnBarcodeScanResultListener {

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
        setContentView(R.layout.activity_scan)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initCamera()
    }

    private fun initCamera() {
        barcodeReader.bindContainer(binding.previewContainer)
        barcodeReader.addOnFocusChangedListener(focusAnimationHelper)
        barcodeReader.addOnBarcodeScanResultListener(this)
    }

    override fun onBarcodeScanResult(result: BarcodeResult) {
        TODO("Not yet implemented")
    }

}