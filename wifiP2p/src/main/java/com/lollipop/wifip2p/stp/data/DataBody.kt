package com.lollipop.wifip2p.stp.data

import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import kotlin.math.min

sealed class DataBody(val packet: DataPacket) {

    class HeartBeat(packet: DataPacket): DataBody(packet)

    class Text(packet: DataPacket): DataBody(packet) {

        fun getText(charset: Charset = Charsets.UTF_8): String {
            return String(packet.textInfo, charset)
        }

    }

    class File(packet: DataPacket, val inputStream: InputStream): DataBody(packet) {

        fun getFileSize(): Long {
            return packet.fileLength
        }

        fun getFileName(charset: Charset = Charsets.UTF_8): String {
            return String(packet.textInfo, charset)
        }

        fun writeTo(outputStream: OutputStream) {
            val fileSize = getFileSize()
            val bufferMax = 1024L
            val buffer = ByteArray(bufferMax.toInt())
            var total = 0L
            do {
                val leftSize = fileSize - total
                val readSize = min(leftSize, bufferMax).toInt()
                // 有多少读多少，不能多读，也不能多写
                val readResult = inputStream.read(buffer, 0, readSize)
                if (readResult < 0) {
                    break
                }
                outputStream.write(buffer, 0, readResult)
                total += readResult
                if (total >= fileSize) {
                    break
                }
            } while (true)
            outputStream.flush()
        }

    }

}