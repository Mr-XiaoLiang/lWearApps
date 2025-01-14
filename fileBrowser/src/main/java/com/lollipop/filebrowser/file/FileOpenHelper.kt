package com.lollipop.filebrowser.file

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.nio.file.Files

object FileOpenHelper {

    private const val APK = "apk"

    fun open(context: Context, file: File) {
        val name = file.name
        val filePath = file.toPath()
        val suffix = getSuffix(name)
        val type = Files.probeContentType(filePath)
        Log.d(
            "FileOpenHelper",
            "open: file = $filePath, name = $name, suffix = $suffix, type = $type"
        )
        if (APK.equals(suffix, ignoreCase = true) && !checkInstallPermission(context)) {
            return
        }
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            intent.setDataAndType(getFileUri(context, file), type)
            context.startActivity(intent)
        } catch (e: Throwable) {
            Log.e("FileOpenHelper", "open: ", e)
        }
    }

    private fun getFileUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            context.packageName + ".fileprovider",
            file
        )
    }

    private fun checkInstallPermission(context: Context): Boolean {
        val pm = context.packageManager
        if (pm.canRequestPackageInstalls()) {
            return true
        } else {
            //跳转到该应用的安装应用的权限页面
            val packageURI = Uri.parse("package:" + context.packageName)
            val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI)
            context.startActivity(intent)
        }
        return false
    }

    private fun getSuffix(name: String): String {
        val index = name.lastIndexOf(".")
        if (index < 0) {
            return ""
        }
        return name.substring(index + 1)
    }

}