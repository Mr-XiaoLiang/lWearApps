package com.lollipop.wear.ps.utils

import android.content.Context
import com.lollipop.wear.data.FileHelper
import org.json.JSONObject
import java.io.File

object PreferenceHelper {

    private fun getFile(context: Context, name: String): File {
        return File(context.filesDir, name)
    }

    fun saveJson(
        context: Context,
        name: String,
        contentBuilder: (JSONObject) -> Unit
    ): SaveTaskCallback {
        return save(context, name) { file ->
            val json = JSONObject()
            contentBuilder(json)
            FileHelper.write(file, json)
        }
    }

    fun saveString(
        context: Context,
        name: String,
        contentBuilder: () -> String
    ): SaveTaskCallback {
        return save(context, name) { file ->
            val content = contentBuilder()
            FileHelper.write(file, content)
        }
    }

    fun saveByteArray(
        context: Context,
        name: String,
        contentBuilder: () -> ByteArray
    ): SaveTaskCallback {
        return save(context, name) { file ->
            val content = contentBuilder()
            FileHelper.write(file, content)
        }
    }

    fun readJson(
        context: Context,
        name: String,
        resultCallback: (FileHelper.FileResult<JSONObject>) -> Unit
    ) {
        read(context, name, resultCallback) {
            FileHelper.readJson(it)
        }
    }

    fun readString(
        context: Context,
        name: String,
        resultCallback: (FileHelper.FileResult<String>) -> Unit
    ) {
        read(context, name, resultCallback) {
            FileHelper.readString(it)
        }
    }

    fun readByteArray(
        context: Context,
        name: String,
        resultCallback: (FileHelper.FileResult<ByteArray>) -> Unit
    ) {
        read(context, name, resultCallback) {
            FileHelper.readByteArray(it)
        }
    }

    private fun <T : Any> read(
        context: Context,
        name: String,
        result: (FileHelper.FileResult<T>) -> Unit,
        workBlock: (File) -> FileHelper.FileResult<T>
    ) {
        val app = context.applicationContext
        val taskCallback = ReadTaskCallback<T>()
        taskCallback.onResult(result)
        doAsync(taskCallback) {
            taskCallback.invokeResult(workBlock(getFile(app, name)))
        }
    }

    private fun save(
        context: Context,
        name: String,
        workBlock: (File) -> FileHelper.FileResult<File>
    ): SaveTaskCallback {
        val app = context.applicationContext
        val taskCallback = SaveTaskCallback()
        doAsync(taskCallback) {
            val file = getFile(app, name)
            val result = workBlock(file)
            taskCallback.invokeResult(result)
        }
        return taskCallback
    }

    class SaveTaskCallback : ThreadHelper.OnErrorCallback {

        private var resultCallback: ((FileHelper.FileResult<File>) -> Unit)? = null

        fun onResult(block: (FileHelper.FileResult<File>) -> Unit) {
            resultCallback = block
        }

        override fun onError(e: Throwable) {
            invokeResult(FileHelper.FileResult.Failure(e))
        }

        fun invokeResult(result: FileHelper.FileResult<File>) {
            onUI {
                resultCallback?.invoke(result)
            }
        }

    }

    class ReadTaskCallback<T> : ThreadHelper.OnErrorCallback {

        private var resultCallback: ((FileHelper.FileResult<T>) -> Unit)? = null

        fun onResult(block: (FileHelper.FileResult<T>) -> Unit) {
            resultCallback = block
        }

        override fun onError(e: Throwable) {
            invokeResult(FileHelper.FileResult.Failure(e))
        }

        fun invokeResult(result: FileHelper.FileResult<T>) {
            onUI {
                resultCallback?.invoke(result)
            }
        }

    }

}