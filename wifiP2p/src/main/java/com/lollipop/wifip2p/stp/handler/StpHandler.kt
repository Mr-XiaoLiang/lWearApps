package com.lollipop.wifip2p.stp.handler

import com.lollipop.wifip2p.stp.data.DataBody
import com.lollipop.wifip2p.stp.data.DataPacket
import com.lollipop.wifip2p.stp.data.DataReader
import com.lollipop.wifip2p.stp.data.DataWriter
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.LinkedList
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class StpHandler(
    val serviceMode: Boolean,
    val inputStream: InputStream,
    val outputStream: OutputStream,
    private val readerListener: ReaderListener,
) {

    companion object {
        private val executor: ExecutorService = Executors.newCachedThreadPool()

        fun create(
            serviceMode: Boolean,
            input: InputStream,
            output: OutputStream,
            readerListener: ReaderListener,
        ): StpHandler {
            return StpHandler(
                serviceMode,
                input,
                output,
                readerListener,
            )
        }

    }

    private val readTask = ReadTask(this)
    private val writeTask = WriteTask(this)

    var isClosed = false
        private set

    fun stop() {
        isClosed = true
    }

    fun start() {
        executor.execute(readTask)
        executor.execute(writeTask)
    }

    fun post(request: StpRequest) {
        // TODO
    }

    private class ReadTask(
        val handler: StpHandler,
    ) : Runnable {
        override fun run() {
            while (!handler.isClosed) {
                val data = DataReader.readData(DataInputStream(handler.inputStream))
                if (data != null) {
                    handler.readerListener.onRead(data)
                }
            }
        }
    }

    private class WriteTask(
        val handler: StpHandler,
    ) : Runnable {

        private val requestList = LinkedList<StpRequest>()

        override fun run() {
            while (!handler.isClosed) {
                if (requestList.isEmpty()) {
                    try {
                        Thread.sleep(100)
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                    continue
                }
                val first = requestList.removeFirst()
                val body = first.requestBody
                // 会在这里对模式做覆盖
                val packet = body.packet
                if (packet.flag == DataPacket.FLAG_NONE) {
                    packet.flag = if (handler.serviceMode) {
                        DataPacket.FLAG_SERVER
                    } else {
                        DataPacket.FLAG_CLIENT
                    }
                }
                val contentLength = packet.contentLength()
                DataWriter.writeData(DataOutputStream(handler.outputStream), body) { written ->
                    val progress = written * 1F / contentLength
                    first.fileWriteListener?.onProgress(progress, contentLength, written)
                }
            }
        }
    }


    interface ReaderListener {
        fun onRead(data: DataBody)
    }

}