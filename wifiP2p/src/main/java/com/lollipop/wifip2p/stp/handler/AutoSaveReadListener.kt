package com.lollipop.wifip2p.stp.handler

import com.lollipop.wifip2p.stp.data.DataBody
import java.io.File
import java.io.FileOutputStream

class AutoSaveReadListener(
    private val fileDir: File,
    val infoListener: (ReadResult) -> Unit
) : StpHandler.ReaderListener {


    override fun onRead(data: DataBody) {
        when (data) {
            is DataBody.File -> {
                try {
                    val file = getFile(data.getFileName())
                    file.parentFile?.mkdirs()
                    val outputStream = FileOutputStream(file)
                    data.writeTo(outputStream)
                    outputStream.flush()
                    outputStream.close()
                    infoListener(ReadResult.FileInfo(file))
                } catch (e: Throwable) {
                    infoListener(ReadResult.Error(e))
                }
            }

            is DataBody.HeartBeat -> {
                infoListener(ReadResult.HeartBeatInfo(data.getText()))
            }

            is DataBody.Text -> {
                infoListener(ReadResult.TextInfo(data.getText()))
            }
        }
    }

    private fun getFile(name: String): File {
        var file = File(fileDir, name)
        while (file.exists()) {
            val fileName = file.name
            val lastIndexDot = fileName.lastIndexOf(".")
            if (lastIndexDot >= 0) {
                val oldName = fileName.substring(0, lastIndexDot)
                val newName = "${oldName}-1.${fileName.substring(lastIndexDot + 1)}"
                file = File(fileDir, newName)
            } else {
                file = File(fileDir, "$fileName-1")
            }
        }
        return file
    }

    sealed class ReadResult {

        class TextInfo(val text: String) : ReadResult()

        class HeartBeatInfo(val text: String) : ReadResult()

        class FileInfo(val file: File) : ReadResult()

        class Error(val error: Throwable) : ReadResult()

    }

}