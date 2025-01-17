package com.lollipop.filebrowser.page

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.InputStream

abstract class FileBasicActivity : AppCompatActivity() {

    companion object {

        private const val PARAMS_TARGET_FILE = "target_file"

        fun openBasic(context: Context, clazz: Class<out Activity>, file: File) {
            context.startActivity(Intent(context, clazz).apply {
                putExtra(PARAMS_TARGET_FILE, file.path)
            })
        }

        inline fun <reified T : Activity> openBasic(context: Context, file: File) {
            openBasic(context, T::class.java, file)
        }
    }

    protected val hasFile by lazy {
        checkIntent()
    }

    /**
     * 内部Intent中获取的文件
     */
    protected val targetFile by lazy {
        File(intent.getStringExtra(PARAMS_TARGET_FILE) ?: "")
    }

    /**
     * 文件Action中获取的Uri
     */
    protected val fileActionUri by lazy {
        intent.data
    }

    private fun checkIntent(): Boolean {
        if (targetFile.exists()) {
            return true
        }
        if (intent.action == Intent.ACTION_VIEW) {
            return fileActionUri != null
        }
        return false
    }

    /**
     * 尝试从Action中获取文件流
     */
    protected fun openStreamFromAction(): InputStream? {
        val uri = fileActionUri ?: return null
        return contentResolver.openInputStream(uri)
    }

    /**
     * 尝试从任意来源获取文件流
     */
    protected fun optFileStream(): InputStream {
        return openStreamFromAction() ?: targetFile.inputStream()
    }

}