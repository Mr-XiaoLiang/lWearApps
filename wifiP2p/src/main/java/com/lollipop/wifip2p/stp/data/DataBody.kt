package com.lollipop.wifip2p.stp.data

import java.io.InputStream
import java.io.OutputStream
import java.nio.charset.Charset
import kotlin.math.min

sealed class DataBody(val packet: DataPacket) {

    class HeartBeat(packet: DataPacket) : DataBody(packet) {


        companion object {

            /**
             * 数据包
             * 结构设定为：
             * flag: Byte 标识位，表示数据来源是客户端还是服务端
             * type: Byte 数据包类型，用于区分数据包类型，纯文本、或文件发送、或心跳
             * command: Int 命令，用于区分数据的业务类型，接收方一般情况下会原封不动的返回
             * serialNumber: Long 数据包序列号，用于标识数据包，一般由发送方自动生成，用于标识数据包是否丢失，同时接收方直接返回
             * textLength: Int 文本信息长度，用于确定后续读取内容的长度
             * textInfo: ByteArray 文本信息
             * fileLength: Long 文件的长度，用于确定后续读取内容的长度
             * fileInfo: ByteArray 文件信息，它不会出现在数据包内，需要以流的形式读取
             * endFlag: Byte 结束标志，用于标识数据包是否已经读取完毕，也用于重置数据包
             */
            fun create(text: String): Text {
                val textBytes = text.toByteArray(Charsets.UTF_8)
                return Text(DataPacket().apply {
                    type = DataPacket.TYPE_HEATH_BEAT
                    textLength = textBytes.size
                    textInfo = textBytes
                })
            }
        }

        fun getText(charset: Charset = Charsets.UTF_8): String {
            return String(packet.textInfo, charset)
        }
    }

    class Text(packet: DataPacket) : DataBody(packet) {

        companion object {

            /**
             * 数据包
             * 结构设定为：
             * flag: Byte 标识位，表示数据来源是客户端还是服务端
             * type: Byte 数据包类型，用于区分数据包类型，纯文本、或文件发送、或心跳
             * command: Int 命令，用于区分数据的业务类型，接收方一般情况下会原封不动的返回
             * serialNumber: Long 数据包序列号，用于标识数据包，一般由发送方自动生成，用于标识数据包是否丢失，同时接收方直接返回
             * textLength: Int 文本信息长度，用于确定后续读取内容的长度
             * textInfo: ByteArray 文本信息
             * fileLength: Long 文件的长度，用于确定后续读取内容的长度
             * fileInfo: ByteArray 文件信息，它不会出现在数据包内，需要以流的形式读取
             * endFlag: Byte 结束标志，用于标识数据包是否已经读取完毕，也用于重置数据包
             */
            fun create(text: String): Text {
                val textBytes = text.toByteArray(Charsets.UTF_8)
                return Text(DataPacket().apply {
                    type = DataPacket.TYPE_TEXT
                    textLength = textBytes.size
                    textInfo = textBytes
                })
            }
        }

        fun getText(charset: Charset = Charsets.UTF_8): String {
            return String(packet.textInfo, charset)
        }

    }

    class File(packet: DataPacket, val inputStream: InputStream) : DataBody(packet) {

        companion object {
            fun create(filePath: String): File {
                val fileInfo = java.io.File(filePath)
                val fileName = fileInfo.name.toByteArray(Charsets.UTF_8)
                return File(
                    DataPacket().apply {
                        type = DataPacket.TYPE_FILE
                        fileLength = fileInfo.length()
                        textInfo = fileName
                        textLength = fileName.size
                    },
                    java.io.FileInputStream(fileInfo)
                )
            }
        }

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