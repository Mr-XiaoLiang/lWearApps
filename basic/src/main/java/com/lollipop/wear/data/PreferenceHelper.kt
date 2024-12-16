package com.lollipop.wear.data

import android.content.Context
import com.lollipop.wear.basic.ThreadHelper
import com.lollipop.wear.basic.doAsync
import com.lollipop.wear.basic.onUI
import org.json.JSONObject
import java.io.File

object PreferenceHelper {

    fun getFile(context: Context, name: String): File {
        return File(context.filesDir, name)
    }

    fun from(context: Context, name: String): Controller {
        return Controller.from(context, name)
    }

    fun from(file: File): Controller {
        return Controller.from(file)
    }

    private fun <T : Any> read(
        file: File,
        result: (FileHelper.FileResult<T>) -> Unit,
        workBlock: (File) -> FileHelper.FileResult<T>
    ) {
        val taskCallback = ReadTaskCallback<T>()
        taskCallback.onResult(result)
        doAsync(taskCallback) {
            taskCallback.invokeResult(workBlock(file))
        }
    }

    private fun save(
        file: File,
        workBlock: (File) -> FileHelper.FileResult<File>
    ): SaveTaskCallback {
        val taskCallback = SaveTaskCallback()
        doAsync(taskCallback) {
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

    class Controller(private val file: File) {

        companion object {
            fun from(context: Context, name: String): Controller {
                return Controller(getFile(context, name))
            }

            fun from(file: File): Controller {
                return Controller(file)
            }
        }

        fun readJson(
            resultCallback: (FileHelper.FileResult<JSONObject>) -> Unit
        ) {
            read(file, resultCallback) {
                FileHelper.readJson(it)
            }
        }

        fun readString(
            resultCallback: (FileHelper.FileResult<String>) -> Unit
        ) {
            read(file, resultCallback) {
                FileHelper.readString(it)
            }
        }

        fun readByteArray(
            resultCallback: (FileHelper.FileResult<ByteArray>) -> Unit
        ) {
            read(file, resultCallback) {
                FileHelper.readByteArray(it)
            }
        }


        fun saveJson(
            contentBuilder: (JSONObject) -> Unit
        ): SaveTaskCallback {
            return save(file) { file ->
                val json = JSONObject()
                contentBuilder(json)
                FileHelper.write(file, json)
            }
        }

        fun saveString(
            contentBuilder: () -> String
        ): SaveTaskCallback {
            return save(file) { file ->
                val content = contentBuilder()
                FileHelper.write(file, content)
            }
        }

        fun saveByteArray(
            contentBuilder: () -> ByteArray
        ): SaveTaskCallback {
            return save(file) { file ->
                val content = contentBuilder()
                FileHelper.write(file, content)
            }
        }

    }

}