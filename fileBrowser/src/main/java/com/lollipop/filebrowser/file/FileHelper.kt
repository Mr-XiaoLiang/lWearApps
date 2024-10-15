package com.lollipop.filebrowser.file

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.icu.text.DecimalFormat
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File


object FileHelper {

    private const val REQUEST_CODE = 233

    private val FILE_SIZE_UNIT = arrayOf(
        "Byte", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"
    )
    private val decimalFormat by lazy {
        DecimalFormat("0.00")
    }

    fun getRootDirectory(): File {
        return Environment.getExternalStorageDirectory()
    }

    fun hasPermission(context: Context): Boolean {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            Environment.isExternalStorageManager()
//        } else { }
        val permissions = arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        for (permission in permissions) {
            if (!checkPermission(context, permission)) {
                return false
            }
        }
        return true
    }

    private fun checkPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(context: Activity) {
        if (hasPermission(context)) {
            return
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            try {
//                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
//                intent.setData(Uri.parse("package:" + context.packageName))
//                context.startActivityForResult(intent, REQUEST_CODE)
//                return
//            } catch (e: Throwable) {
//                Log.e("FileBrowser", e.message ?: "", e)
//            }
//        }
        ActivityCompat.requestPermissions(
            context,
            arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            REQUEST_CODE
        )
    }

    fun getFileSizeDisplay(size: Long): String {
        var unitIndex = 0
        var unitSize = 1
        val unitMaxIndex = FILE_SIZE_UNIT.size - 1
        while (size / unitSize >= 1024 && unitIndex < unitMaxIndex) {
            unitSize *= 1024
            unitIndex++
        }
        val result = decimalFormat.format(size.toDouble() / unitSize)
        return "$result ${FILE_SIZE_UNIT[unitIndex]}"
    }

}