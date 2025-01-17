package com.lollipop.filebrowser.page

import android.content.Context
import android.icu.text.DecimalFormat
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import com.lollipop.filebrowser.R
import com.lollipop.filebrowser.databinding.ActivityFileDetailBinding
import com.lollipop.filebrowser.file.FileOpenHelper
import java.io.File
import java.util.Date
import java.util.Locale

class FileDetailActivity : FileBasicActivity() {

    companion object {

        fun open(context: Context, file: File) {
            openBasic<FileDetailActivity>(context, file)
        }
    }

    private val binding by lazy {
        ActivityFileDetailBinding.inflate(layoutInflater)
    }

    private val fileSizeFormat by lazy {
        DecimalFormat("#.##")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.openButton.setOnClickListener {
            FileOpenHelper.open(this, targetFile)
        }
        binding.fileNameView.text = targetFile.name
        binding.filePathView.text = targetFile.path
        binding.fileSizeView.text = fileSize(targetFile.length())
        binding.fileTimeView.text = fileTime(targetFile.lastModified())
    }

    private fun fileSize(length: Long): String {
        val unitArray = arrayOf("B", "KB", "MB", "GB", "TB", "PB")
        val maxIndex = unitArray.size - 1
        var index = 0
        val stepLength = 1000
        val step = 1F / stepLength
        var size = length.toFloat()
        while (size > stepLength) {
            size *= step
            index++
            if (index == maxIndex) {
                break
            }
        }
        return fileSizeFormat.format(size) + unitArray[index]
    }

    private fun fileTime(time: Long): String {
        val pattern = getString(R.string.file_last_modified)
        return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(time))
    }

}