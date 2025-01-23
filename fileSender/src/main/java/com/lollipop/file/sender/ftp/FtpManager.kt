package com.lollipop.file.sender.ftp

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.lollipop.wear.basic.ListenerManager
import it.sauronsoftware.ftp4j.FTPClient
import it.sauronsoftware.ftp4j.FTPDataTransferListener
import it.sauronsoftware.ftp4j.FTPFile
import java.io.File
import java.util.concurrent.Executors

object FtpManager {

    private val clientMap = HashMap<String, Client>()

    private val executor by lazy {
        Executors.newCachedThreadPool()
    }

    private val mainThread by lazy {
        Handler(Looper.getMainLooper())
    }

    fun getOrCreate(info: ConnectInfo): Client {
        return clientMap[info.token] ?: Client(info).also {
            clientMap[info.token] = it
        }
    }

    fun findClient(token: String): Client? {
        return clientMap[token]
    }

    fun removeClient(token: String) {
        clientMap.remove(token)
    }

    private fun doAsync(operation: () -> Unit) {
        executor.execute {
            try {
                operation()
            } catch (e: Throwable) {
                // 最后的兜底
                Log.e("FtpManager", "doAsync: ", e)
            }
        }
    }

    private fun onUI(operation: () -> Unit) {
        mainThread.post {
            try {
                operation()
            } catch (e: Throwable) {
                // 最后的兜底
                Log.e("FtpManager", "onUI: ", e)
            }
        }
    }

    class Client(
        val info: ConnectInfo
    ) {

        val impl = FTPClient()
        private val listenerManager = ListenerManager<FtpStateListener>()

        fun addListener(listener: FtpStateListener) {
            listenerManager.add(listener)
        }

        fun removeListener(listener: FtpStateListener) {
            listenerManager.remove(listener)
        }

        private fun notifyConnectResult(result: RequestResult<Array<String>>) {
            listenerManager.invoke {
                it.onConnectResult(result)
            }
        }

        private fun notifyLoginResult(result: RequestResult<Boolean>) {
            listenerManager.invoke {
                it.onLoginResult(result)
            }
        }

        private fun notifyOperationFails(e: Throwable) {
            listenerManager.invoke {
                it.onOperationFails(e)
            }
        }

        private inline fun <reified T : Any> tryOperation(operation: () -> T): RequestResult<T> {
            try {
                return RequestResult.success(operation())
            } catch (e: Throwable) {
                notifyOperationFails(e)
                return RequestResult.failure(e)
            }
        }

        private inline fun <reified T : Any> tryRequest(
            callback: RequestCallback<T>,
            crossinline operation: () -> T
        ) {
            doAsync {
                val result = tryOperation(operation)
                onUI {
                    callback.onResult(result)
                }
            }
        }

        /**
         * 连接并且登录FTP服务器
         */
        fun connect(callback: RequestCallback<Boolean>) {
            tryRequest(callback) {
                var connectResult = false
                var loginResult = false
                try {
                    val result = impl.connect(info.host, info.port)
                    notifyConnectResult(RequestResult.success(result))
                    connectResult = true
                } catch (e: Throwable) {
                    notifyConnectResult(RequestResult.failure(e))
                }
                if (connectResult) {
                    try {
                        impl.login(info.username, info.password)
                        notifyLoginResult(RequestResult.success(true))
                        loginResult = true
                    } catch (e: Throwable) {
                        notifyLoginResult(RequestResult.failure(e))
                    }
                }
                return@tryRequest connectResult && loginResult
            }
        }

        /**
         * 当前目录的路径
         */
        fun currentDirectory(callback: RequestCallback<String>) {
            tryRequest(callback) { impl.currentDirectory() }
        }

        /**
         * 指定路径下的文件的大小
         */
        fun fileSize(filePath: String, callback: RequestCallback<Long>) {
            tryRequest(callback) { impl.fileSize(filePath) }
        }

        /**
         * 删除指定路径下的文件
         */
        fun deleteFile(filePath: String, callback: RequestCallback<Boolean>) {
            tryRequest(callback) {
                impl.deleteFile(filePath)
                true
            }
        }

        /**
         * 删除指定路径下的文件夹
         */
        fun deleteDirectory(dirPath: String, callback: RequestCallback<Boolean>) {
            tryRequest(callback) {
                impl.deleteDirectory(dirPath)
                true
            }
        }

        /**
         * 获取当前路径下的文件列表
         */
        fun list(callback: RequestCallback<Array<FTPFile>>) {
            tryRequest(callback) { impl.list() }
        }

        /**
         * 获取当前路径下的文件列表的名称
         */
        fun listNames(callback: RequestCallback<Array<String>>) {
            tryRequest(callback) { impl.listNames() }
        }

        /**
         * 上传文件
         */
        fun upload(
            file: File,
            listener: FTPDataTransferListener,
            callback: RequestCallback<Boolean>
        ) {
            tryRequest(callback) {
                impl.upload(file, OnUiFTPDataTransferListener(mainThread, listener))
                true
            }
        }

        /**
         * 下载文件
         */
        fun download(
            remoteFileName: String,
            localFile: File,
            listener: FTPDataTransferListener,
            callback: RequestCallback<Boolean>
        ) {
            tryRequest(callback) {
                impl.download(
                    remoteFileName,
                    localFile,
                    OnUiFTPDataTransferListener(mainThread, listener)
                )
                true
            }
        }

        /**
         * 中断文件传输任务
         * @param sendAbortCommand 是否发送ABORT命令通知FTP服务器
         */
        fun abortCurrentDataTransfer(
            sendAbortCommand: Boolean = true,
            callback: RequestCallback<Boolean>
        ) {
            tryRequest(callback) {
                impl.abortCurrentDataTransfer(sendAbortCommand)
                true
            }
        }

    }

    private class OnUiFTPDataTransferListener(
        val handler: Handler,
        val impl: FTPDataTransferListener
    ) : FTPDataTransferListener {

        private var lastTransferredInvokeTime = 0L

        override fun started() {
            handler.post {
                impl.started()
            }
        }

        override fun transferred(length: Int) {
            val now = System.currentTimeMillis()
            if (now - lastTransferredInvokeTime < 100) {
                // 设置最小间隔，避免高频调用，阻塞UI线程
                return
            }
            lastTransferredInvokeTime = now
            handler.post {
                impl.transferred(length)
            }
        }

        override fun completed() {
            handler.post {
                impl.completed()
            }
        }

        override fun aborted() {
            handler.post {
                impl.aborted()
            }
        }

        override fun failed() {
            handler.post {
                impl.failed()
            }
        }

    }

    fun interface RequestCallback<T : Any> {
        fun onResult(result: RequestResult<T>)
    }

}