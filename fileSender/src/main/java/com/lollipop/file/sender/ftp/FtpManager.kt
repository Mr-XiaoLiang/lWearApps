package com.lollipop.file.sender.ftp

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.lollipop.file.sender.FTPLog
import com.lollipop.wear.basic.ListenerManager
import com.lollipop.wear.data.FileHelper
import com.lollipop.wear.data.PreferenceHelper
import it.sauronsoftware.ftp4j.FTPClient
import it.sauronsoftware.ftp4j.FTPDataTransferListener
import it.sauronsoftware.ftp4j.FTPFile
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.Date
import java.util.concurrent.Executors

object FtpManager {

    private const val SP_FILE = "ftp.lp"

    private const val KEY_LIST = "connectList"
    private const val KEY_USERNAME = "username"
    private const val KEY_PASSWORD = "password"
    private const val KEY_HOST = "host"
    private const val KEY_PORT = "port"

    private val clientMap = HashMap<String, Client>()

    var currentClientToken: String = ""
        private set

    private val executor by lazy {
        Executors.newCachedThreadPool()
    }

    private val mainThread by lazy {
        Handler(Looper.getMainLooper())
    }

    fun updateCurrentClient(client: Client?) {
        currentClientToken = client?.info?.token ?: ""
    }

    fun getOrCreate(info: ConnectInfo): Client {
        return clientMap[info.token] ?: Client(info).also {
            clientMap[info.token] = it
        }
    }

    fun findClient(token: String): Client? {
        return clientMap[token]
    }

    fun currentClient(): Client? {
        return findClient(currentClientToken)
    }

    fun remove(token: String) {
        clientMap.remove(token)
    }

    fun list(): List<Client> {
        return clientMap.values.toList()
    }

    fun connectList(): List<ConnectInfo> {
        return clientMap.values.map { it.info }
    }

    fun read(context: Context) {
        PreferenceHelper.from(context, SP_FILE).readJson { result ->
            when (result) {
                is FileHelper.FileResult.Failure -> {
                    // 发生异常就不处理了
                }

                is FileHelper.FileResult.Success -> {
                    val json = result.data
                    val connectArray = json.optJSONArray(KEY_LIST)
                    if (connectArray != null && connectArray.length() > 0) {
                        for (index in 0 until connectArray.length()) {
                            val item = connectArray.optJSONObject(index)
                            val connectInfo = ConnectInfo(
                                host = item.optString(KEY_HOST),
                                port = item.optInt(KEY_PORT),
                                username = item.optString(KEY_USERNAME),
                                password = item.optString(KEY_PASSWORD)
                            )
                            if (connectInfo.host.isNotEmpty() && connectInfo.port > 0) {
                                getOrCreate(connectInfo)
                            }
                        }
                    }
                }
            }
        }
    }

    fun save(context: Context) {
        PreferenceHelper.from(context, SP_FILE).saveJson { json ->
            val connectArray = JSONArray()
            val list = connectList()
            list.forEach { info ->
                connectArray.put(
                    JSONObject()
                        .put(KEY_HOST, info.host)
                        .put(KEY_PORT, info.port)
                        .put(KEY_USERNAME, info.username)
                        .put(KEY_PASSWORD, info.password)
                )
            }
            json.put(KEY_LIST, connectArray)
        }
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

        private val impl by lazy {
            FTPClient()
        }

        private val log by lazy {
            FTPLog.with(this)
        }

        val ftpClient: FTPClient
            get() = impl

        var connectStateCache: Boolean = false
            private set

        private var rootPath = ""

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
            log.d("connect")
            tryRequest(callback) {
                var connectResult = false
                var loginResult = false
                try {
                    log.d("connect.connect")
                    val result = impl.connect(info.host, info.port)
                    notifyConnectResult(RequestResult.success(result))
                    connectResult = true
                } catch (e: Throwable) {
                    log.e("connect.connect.error", e)
                    notifyConnectResult(RequestResult.failure(e))
                }
                if (connectResult) {
                    try {
                        log.d("connect.login")
                        impl.login(info.username, info.password)
                        notifyLoginResult(RequestResult.success(true))
                        loginResult = true
                    } catch (e: Throwable) {
                        log.e("connect.login.error", e)
                        notifyLoginResult(RequestResult.failure(e))
                    }
                }
                connectStateCache = connectResult && loginResult
                return@tryRequest connectStateCache
            }
        }

        /**
         * 是否已经连接并且登录FTP服务器
         */
        fun isConnected(callback: RequestCallback<Boolean>) {
            tryRequest(callback) {
                val result = impl.isConnected && impl.isAuthenticated
                connectStateCache = result
                result
            }
        }

        /**
         * 断开连接
         * 会尝试在断开连接之前注销登录
         */
        fun disconnect(callback: RequestCallback<Boolean>) {
            tryRequest(callback) {
                if (impl.isConnected) {
                    try {
                        if (impl.isAuthenticated) {
                            impl.logout()
                        }
                    } catch (_: Throwable) {
                    }
                    impl.disconnect(true)
                }
                connectStateCache = impl.isConnected && impl.isAuthenticated
                true
            }
        }

        fun rootPath(callback: RequestCallback<String>) {
            tryRequest(callback) {
                if (rootPath.isEmpty()) {
                    rootPath = impl.currentDirectory()
                }
                rootPath
            }
        }

        /**
         * 重命名
         */
        fun rename(srcPath: String, newPath: String, callback: RequestCallback<Boolean>) {
            tryRequest(callback) {
                impl.rename(srcPath, newPath)
                true
            }
        }

        /**
         * 重命名
         */
        fun renameSync(srcPath: String, newPath: String): RequestResult<Boolean> {
            return tryOperation {
                impl.rename(srcPath, newPath)
                true
            }
        }

        /**
         * 创建文件夹
         */
        fun createDirectory(dirName: String, callback: RequestCallback<Boolean>) {
            tryRequest(callback) {
                impl.createDirectory(dirName)
                true
            }
        }

        /**
         * 创建文件夹
         */
        fun createDirectorySync(dirName: String): RequestResult<Boolean> {
            return tryOperation {
                impl.createDirectory(dirName)
                true
            }
        }

        /**
         * 当前目录的路径
         */
        fun currentDirectory(callback: RequestCallback<String>) {
            tryRequest(callback) {
                impl.currentDirectory().also {
                    if (rootPath.isEmpty()) {
                        rootPath = it
                    }
                }
            }
        }

        /**
         * 当前目录的路径
         */
        fun currentDirectorySync(): RequestResult<String> {
            return tryOperation {
                impl.currentDirectory().also {
                    if (rootPath.isEmpty()) {
                        rootPath = it
                    }
                }
            }
        }

        /**
         * 修改文件夹
         */
        fun changeDirectory(dirPath: String, callback: RequestCallback<Boolean>) {
            tryRequest(callback) {
                impl.changeDirectory(dirPath)
                true
            }
        }

        /**
         * 修改文件夹
         */
        fun changeDirectorySync(dirPath: String): RequestResult<Boolean> {
            return tryOperation {
                impl.changeDirectory(dirPath)
                true
            }
        }

        /**
         * 修改时间
         */
        fun modifiedDate(filePath: String, callback: RequestCallback<Date>) {
            tryRequest(callback) { impl.modifiedDate(filePath) }
        }

        /**
         * 修改时间
         */
        fun modifiedDateSync(filePath: String): RequestResult<Date> {
            return tryOperation { impl.modifiedDate(filePath) }
        }

        /**
         * 指定路径下的文件的大小
         */
        fun fileSize(filePath: String, callback: RequestCallback<Long>) {
            tryRequest(callback) { impl.fileSize(filePath) }
        }

        /**
         * 指定路径下的文件的大小
         */
        fun fileSizeSync(filePath: String): RequestResult<Long> {
            return tryOperation { impl.fileSize(filePath) }
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
         * 删除指定路径下的文件
         */
        fun deleteFileSync(filePath: String): RequestResult<Boolean> {
            return tryOperation {
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
         * 删除指定路径下的文件夹
         */
        fun deleteDirectorySync(dirPath: String): RequestResult<Boolean> {
            return tryOperation {
                impl.deleteDirectory(dirPath)
                true
            }
        }

        /**
         * 切换路径并获取当前路径下的文件列表
         */
        fun cdAndList(
            dirPath: String,
            callback: RequestCallback<Array<FTPFile>>
        ) {
            tryRequest(callback) {
                impl.changeDirectory(dirPath)
                impl.list()
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
         * 上传文件
         */
        fun uploadSync(
            file: File,
            listener: FTPDataTransferListener
        ) {
            tryOperation {
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
         * 下载文件
         */
        fun downloadSync(
            remoteFileName: String,
            localFile: File,
            listener: FTPDataTransferListener
        ) {
            tryOperation {
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