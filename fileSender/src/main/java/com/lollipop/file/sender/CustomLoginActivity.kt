package com.lollipop.file.sender

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.lollipop.file.sender.databinding.ActivityCustomLoginBinding
import com.lollipop.file.sender.ftp.ConnectInfo
import com.lollipop.file.sender.ftp.FtpManager
import com.lollipop.file.sender.ftp.RequestResult
import com.lollipop.wear.basic.DialogHelper

class CustomLoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityCustomLoginBinding.inflate(layoutInflater)
    }

    private val log by lazy {
        FTPLog.with(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        initInsets()
        initView()
    }

    private fun initInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.actionBar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.contentContainer) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initView() {
        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
        binding.scanButton.setOnClickListener {
            startActivity(Intent(this, ScanActivity::class.java))
            finish()
        }
        binding.connectButton.setOnClickListener {
            login()
        }
    }

    private fun login() {
        log.d("login")
        val uriStr = binding.uriInputLayout.editText?.text?.toString()?.trim() ?: ""
        if (uriStr.isEmpty()) {
            binding.uriInputLayout.error = getString(R.string.error_uri_empty)
            log.e("Invalid uri, uriStr.isEmpty()")
            return
        }
//        val uriInfo = try {
//            uriStr.toUri()
//        } catch (e: Throwable) {
//            binding.uriInputLayout.error = getString(R.string.error_uri_invalid)
//            log.e("Invalid uri", e)
//            return
//        }
//        if (uriInfo.host == null || TextUtils.isEmpty(uriInfo.host) || TextUtils.isEmpty(uriInfo.scheme)) {
//            binding.uriInputLayout.error = getString(R.string.error_uri_invalid)
//            log.e("Invalid uri, uriInfo.host == null || TextUtils.isEmpty(uriInfo.host) || TextUtils.isEmpty(uriInfo.scheme)")
//            return
//        }
//        val uriPath = try {
//            uriInfo.toString()
//        } catch (e: Throwable) {
//            log.e("Invalid uri", e)
//            binding.uriInputLayout.error = getString(R.string.error_uri_invalid)
//            return
//        }
//        if (uriPath.isEmpty()) {
//            binding.uriInputLayout.error = getString(R.string.error_uri_invalid)
//            log.e("Invalid uri, uriPath.isNullOrEmpty()")
//            return
//        }
        val port = binding.portInputLayout.editText?.text?.toString()?.trim() ?: ""
        if (port.isEmpty()) {
            binding.portInputLayout.error = getString(R.string.error_port_empty)
            log.e("Invalid port, port.isEmpty()")
            return
        }
        val portInt = port.toIntOrNull()
        if (portInt == null) {
            binding.portInputLayout.error = getString(R.string.error_port_invalid)
            log.e("Invalid port, portInt == null")
            return
        }
        val username = binding.nameInputLayout.editText?.text?.toString()?.trim() ?: ""
        val password = binding.pwdInputLayout.editText?.text?.toString()?.trim() ?: ""
        val isAnonymous = binding.anonymousSwitch.isChecked

        binding.uriInputLayout.error = null
        binding.portInputLayout.error = null
        binding.nameInputLayout.error = null
        binding.pwdInputLayout.error = null

        val connectInfo = ConnectInfo(
            host = uriStr,
            port = portInt,
            username = username,
            password = password,
            isAnonymous = isAnonymous
        )
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
            val msg = if (isSuccess) {
                R.string.connect_success
            } else {
                R.string.connect_failed
            }
            DialogHelper.alert(
                context = this,
                messageRes = msg,
                positiveRes = R.string.ok,
                onPositive = {
                    it.dismiss()
                    if (isSuccess) {
                        finish()
                    }
                }
            )
        }
    }

}