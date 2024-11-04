package com.lollipop.wifip2p.stp.data

import java.io.DataOutputStream

object DataWriter {

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
    fun writeData(output: DataOutputStream, data: DataBody, progressListener: (Long) -> Unit) {
        val packet = data.packet
        val baseSize = output.size().toLong()
        output.writeByte(packet.flag.toInt())
        output.getProgress(baseSize, progressListener)
        output.writeByte(packet.type.toInt())
        output.getProgress(baseSize, progressListener)
        output.writeInt(packet.command)
        output.getProgress(baseSize, progressListener)
        output.writeLong(packet.serialNumber)
        output.getProgress(baseSize, progressListener)
        output.writeInt(packet.textLength)
        output.getProgress(baseSize, progressListener)
        output.write(packet.textInfo)
        output.getProgress(baseSize, progressListener)
        output.writeLong(packet.fileLength)
        output.getProgress(baseSize, progressListener)
        when (data) {
            is DataBody.File -> {
                // 直接拷贝出去
                val fileInput = MaxSizedInputStream(data.inputStream, data.getFileSize())
                val buffer = ByteArray(8 * 1024)
                var bytes = fileInput.read(buffer)
                while (bytes >= 0) {
                    output.write(buffer, 0, bytes)
                    output.getProgress(baseSize, progressListener)
                    bytes = fileInput.read(buffer)
                }
            }

            is DataBody.HeartBeat -> {
                // 不写其他的了
            }
            is DataBody.Text -> {
                // 不写其他的了
            }
        }
        output.writeByte(DataPacket.FLAG_PACKET_END.toInt())
        output.getProgress(baseSize, progressListener)
        output.flush()
    }

    private fun DataOutputStream.getProgress(baseSize: Long, output: (Long) -> Unit) {
        output(size().toLong() - baseSize)
    }

}