package com.lollipop.wifip2p

import android.content.Context
import android.net.Uri
import java.io.Closeable
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket

object FileSender {

    const val SOCKET_TIMEOUT = 5000

    fun send(
        inputProvider: InputProvider,
        outputProvider: OutputProvider,
        writeSize: (Long) -> Unit
    ): Result<Boolean> {
        var outputStream: OutputStream? = null
        var inputStream: InputStream? = null
        try {
            outputStream = outputProvider.getOutputStream() ?: return Result.success(false)
            inputStream = inputProvider.getInputStream() ?: return Result.success(false)
            inputStream.copyTo(outputStream, writeSize = writeSize)
            outputStream.flush()
            return Result.success(true)
        } catch (e: Throwable) {
            return Result.failure(e)
        } finally {
            outputStream?.tryClose()
            inputStream?.tryClose()
        }
    }

    private fun Closeable.tryClose() {
        try {
            this.close()
        } catch (_: Throwable) {
        }
    }

    sealed class InputProvider {
        abstract fun getInputStream(): InputStream?

        class FromSocket(
            private val host: String,
            private val port: Int,
            private val socketTimeout: Int = SOCKET_TIMEOUT
        ) : InputProvider() {
            override fun getInputStream(): InputStream? {
                val socket = Socket()
                socket.bind(null)
                socket.connect(
                    InetSocketAddress(host, port),
                    socketTimeout
                )
                return socket.getInputStream()
            }
        }

        class FromContentProvider(
            private val context: Context,
            private val fileUri: String
        ) : InputProvider() {
            override fun getInputStream(): InputStream? {
                return context.contentResolver.openInputStream(Uri.parse(fileUri))
            }
        }

        class FromFile(
            private val file: File
        ) : InputProvider() {
            override fun getInputStream(): InputStream {
                return file.inputStream()
            }
        }

        class FromStream(
            private val stream: InputStream
        ) : InputProvider() {
            override fun getInputStream(): InputStream {
                return stream
            }
        }

    }

    sealed class OutputProvider {
        abstract fun getOutputStream(): OutputStream?

        class FromSocket(
            private val host: String,
            private val port: Int,
            private val socketTimeout: Int = SOCKET_TIMEOUT
        ) : OutputProvider() {
            override fun getOutputStream(): OutputStream? {
                val socket = Socket()
                socket.bind(null)
                socket.connect(
                    InetSocketAddress(host, port),
                    socketTimeout
                )
                return socket.getOutputStream()
            }
        }

        class FromFile(
            private val file: File
        ) : OutputProvider() {
            override fun getOutputStream(): OutputStream {
                return file.outputStream()
            }
        }

        class FromStream(
            private val stream: OutputStream
        ) : OutputProvider() {
            override fun getOutputStream(): OutputStream {
                return stream
            }
        }

        class FromContentProvider(
            private val context: Context,
            private val fileUri: String
        ) : OutputProvider() {
            override fun getOutputStream(): OutputStream? {
                return context.contentResolver.openOutputStream(Uri.parse(fileUri))
            }
        }

    }

    private fun InputStream.copyTo(
        out: OutputStream,
        bufferSize: Int = DEFAULT_BUFFER_SIZE,
        writeSize: (Long) -> Unit
    ): Long {
        var bytesCopied: Long = 0
        val buffer = ByteArray(bufferSize)
        var bytes = read(buffer)
        while (bytes >= 0) {
            out.write(buffer, 0, bytes)
            bytesCopied += bytes
            writeSize(bytesCopied)
            bytes = read(buffer)
        }
        return bytesCopied
    }

}