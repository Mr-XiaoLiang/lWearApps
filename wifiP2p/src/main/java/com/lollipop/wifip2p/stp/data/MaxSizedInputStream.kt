package com.lollipop.wifip2p.stp.data

import java.io.InputStream

class MaxSizedInputStream(
    private val inputStream: InputStream,
    private val maxSize: Long,
    private val closeBase: Boolean = false,
    private val wasteOnClose: Boolean = true
) : InputStream() {

    private var currentSize = 0L

    override fun read(): Int {
        if (currentSize >= maxSize) {
            return -1
        }
        val byte = inputStream.read()
        if (byte == -1) {
            // 没有数据，那么我们就认为数据已经读取完毕
            currentSize = maxSize
            return -1
        }
        // 否则我们计数+1，然后让它能继续读取
        currentSize++
        return byte
    }

    override fun close() {
        super.close()
        if (closeBase) {
            inputStream.close()
        } else if (wasteOnClose) {
            // 如果没有关闭，那么就直接把数据丢弃
            while (read() != -1) {
                // 循环读取，直到读取完毕
            }
        }
    }

}