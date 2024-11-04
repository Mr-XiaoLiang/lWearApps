package com.lollipop.wifip2p.stp.data

import android.util.Xml.Encoding

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
class DataPacket {

    companion object {
        val ENCODE = Encoding.UTF_8.name

        const val FLAG_NONE: Byte = 0x00

        const val FLAG_CLIENT: Byte = 0x01
        const val FLAG_SERVER: Byte = 0x02

        const val FLAG_PACKET_END: Byte = 0x0A

        const val TYPE_HEATH_BEAT: Byte = 0x00
        const val TYPE_TEXT: Byte = 0x01
        const val TYPE_FILE: Byte = 0x02

    }

    /**
     * 标志位，标识发送方是客户端还是服务端
     */
    var flag: Byte = FLAG_NONE

    /**
     * 数据包类型
     */
    var type: Byte = TYPE_HEATH_BEAT

    /**
     * 命令
     */
    var command: Int = 0

    /**
     * 数据包序列号
     */
    var serialNumber: Long = 0

    /**
     * 文本长度
     */
    var textLength: Int = 0

    /**
     * 文本信息
     */
    var textInfo: ByteArray = byteArrayOf()

    /**
     * 文件长度，用于读写文件时使用
     */
    var fileLength: Long = 0
        get() {
            // 如果类型不对，强行返回0
            if (type != TYPE_FILE) {
                return 0
            }
            return field
        }

    /**
     * 内容长度
     */
    fun contentLength(): Long {
        // 1 + 1 + 4 + 8 + 4 + textLength + 8 + fileLength
        return 1 + 1 + 4 + 8 + 4 + textLength + 8 + fileLength
    }

}