package com.lollipop.filebrowser.page

import android.content.Intent
import android.os.Bundle
import com.lollipop.filebrowser.R
import com.lollipop.filebrowser.databinding.ActivityMainBinding
import com.lollipop.filebrowser.file.FileHelper
import com.lollipop.wear.devices.TimeViewDelegate
import java.io.File

class MainActivity : FilePermissionActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val timeViewDelegate by lazy {
        TimeViewDelegate {
            binding.timeView.text = it
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent(binding.root)
        binding.storageButton.setOnClickListener {
            startActivity(Intent(this, FileBrowserActivity::class.java))
        }
        binding.ftpButton.setOnClickListener {
            startActivity(Intent(this, FtpServiceActivity::class.java))
        }
    }

    override fun onResume(hasPermission: Boolean) {
        timeViewDelegate.onResume()
        if (!hasPermission) {
            return
        }
        updateDirectoryInfo(getRootDirectory())
    }

    override fun onPause() {
        super.onPause()
        timeViewDelegate.onPause()
    }

    private fun updateDirectoryInfo(rootDir: File) {
        updateUseSpace(rootDir)
    }

    private fun updateUseSpace(rootDir: File) {
        val totalSpace = rootDir.totalSpace
        val freeSpace = rootDir.freeSpace
        val useSpace = totalSpace - freeSpace
        binding.storageOccupancyProgressView.progress = useSpace * 1F / totalSpace
        binding.storageSizeView.text = getString(
            R.string.storage_size,
            FileHelper.getFileSizeDisplay(useSpace),
            FileHelper.getFileSizeDisplay(totalSpace)
        )

    }


}