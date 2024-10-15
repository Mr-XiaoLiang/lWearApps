package com.lollipop.filebrowser.page

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import com.lollipop.filebrowser.databinding.ActivityPermissionBasicBinding
import com.lollipop.filebrowser.file.FileHelper
import java.io.File

abstract class FilePermissionActivity : AppCompatActivity() {

    companion object {
        private const val SDCARD = "SDCard"
    }

    private val binding by lazy {
        ActivityPermissionBasicBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding.permissionAllowButton.setOnClickListener {
            FileHelper.requestPermission(this)
        }
    }

    protected fun setContent(view: View) {
        binding.contentGroup.addView(
            view,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onResume() {
        super.onResume()
        val hasPermission = FileHelper.hasPermission(this)
        binding.permissionPanel.isVisible = !hasPermission
        onResume(hasPermission)
    }

    abstract fun onResume(hasPermission: Boolean)

    protected fun getRootDirectory(): File {
        return FileHelper.getRootDirectory()
    }

}