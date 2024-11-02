package com.lollipop.wifip2p.stp.data

import java.io.DataInputStream

object DataReader {

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
    fun readData(inputStream: DataInputStream): DataBody? {
        val flag = inputStream.readByte()
        if (flag == DataPacket.FLAG_PACKET_END) {
            // 如果第一个字节是结束标志，那么就直接返回，重新读取数据包
            return readData(inputStream)
        }
        val type = inputStream.readByte()
        val command = inputStream.readInt()
        val serialNumber = inputStream.readLong()
        val textLength = inputStream.readInt()
        // 根据长度读取文本信息
        val textInfo = if (textLength < 1) {
            byteArrayOf()
        } else {
            ByteArray(textLength)
        }
        // 如果有文本信息，就进行读取
        if (textInfo.isNotEmpty()) {
            inputStream.readFully(textInfo)
        }
        val fileLength = inputStream.readLong()

        val packet = DataPacket().apply {
            this.flag = flag
            this.type = type
            this.command = command
            this.serialNumber = serialNumber
            this.textLength = textLength
            this.textInfo = textInfo
            this.fileLength = fileLength
        }

        when (type) {
            DataPacket.TYPE_TEXT -> {
                return DataBody.Text(packet)
            }

            DataPacket.TYPE_FILE -> {
                return DataBody.File(packet, MaxSizedInputStream(inputStream, fileLength))
            }

            DataPacket.TYPE_HEATH_BEAT -> {
                return DataBody.HeartBeat(packet)
            }

            else -> {
                return null
            }
        }
    }

}