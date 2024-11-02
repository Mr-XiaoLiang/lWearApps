package com.lollipop.wifip2p.stp.handler

import com.lollipop.wifip2p.stp.data.DataBody
import java.io.File

class AutoSaveReadListener(
    val fileDir: File,
    val infoListener: (ReadResult) -> Unit
) : StpHandler.ReaderListener {


    override fun onRead(data: DataBody) {
        when (data) {
            is DataBody.File -> {
                TODO()
            }
            is DataBody.HeartBeat -> TODO()
            is DataBody.Text -> TODO()
        }
    }

    sealed class ReadResult {

        class TextInfo(val text: String) : ReadResult()

        class FileInfo(val file: File) : ReadResult()

        class Error(val error: Throwable) : ReadResult()

    }

}