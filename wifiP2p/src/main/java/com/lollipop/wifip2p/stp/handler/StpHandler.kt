package com.lollipop.wifip2p.stp.handler

import com.lollipop.wifip2p.stp.data.DataBody
import com.lollipop.wifip2p.stp.data.DataReader
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class StpHandler(
    val inputStream: DataInputStream,
    val outputStream: DataOutputStream
) {

    companion object {
        private val executor: ExecutorService = Executors.newCachedThreadPool()

        fun create(input: InputStream, output: OutputStream): StpHandler {
            return StpHandler(DataInputStream(input), DataOutputStream(output))
        }

    }

    private var isClosed = false
        private set

    fun stop() {
        isClosed = true
    }

    private class ReadTask(
        val handler: StpHandler,
        val listener: (DataBody) -> Unit
    ) : Runnable {
        override fun run() {
            while (!handler.isClosed) {
                val data = DataReader.readData(handler.inputStream)
                if (data != null) {
                    listener(data)
                }
            }
        }
    }

}