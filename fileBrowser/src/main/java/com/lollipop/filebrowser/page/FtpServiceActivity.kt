package com.lollipop.filebrowser.page

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.lollipop.filebrowser.R
import com.lollipop.filebrowser.databinding.ActivityFtpServiceBinding
import com.lollipop.qr.writer.BarcodeWriter

class FtpServiceActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityFtpServiceBinding.inflate(layoutInflater)
    }

    private val qrWriter by lazy {
        BarcodeWriter(this.lifecycle)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.qrCodeView.outlineProvider
        updateQrCode(getString(R.string.app_name))

        binding.loginInfoView.text = getString(
            R.string.ftp_login_info,
            getString(com.lollipop.swiftp.R.string.username_default),
            getString(com.lollipop.swiftp.R.string.password_default)
        )
    }

    private fun updateQrCode(content: String) {
        binding.pathView.text = content
        binding.qrCodeView.post {
            qrWriter.encode(content).size(binding.qrCodeView.width).loadBitmap { result ->
                Log.e("FtpServiceActivity", "updateQrCode: $result")
                binding.qrCodeView.setImageBitmap(result.getOrNull())
            }
        }

    }

}