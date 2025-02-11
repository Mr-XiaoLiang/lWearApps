package com.lollipop.file.sender.ftp

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import it.sauronsoftware.ftp4j.FTPAbortedException
import it.sauronsoftware.ftp4j.FTPDataTransferException
import it.sauronsoftware.ftp4j.FTPDataTransferListener
import java.io.File
import java.util.LinkedList
import java.util.concurrent.Executors

object FileTransferStation {

    private const val PROGRESS_MAX = 1F
    private const val PROGRESS_MIN = 0F

    private val fileList = LinkedList<FtsTarget>()

    private val flowList = LinkedList<Options>()

    private var cacheDir: File? = null

    private val executor by lazy {
        Executors.newSingleThreadExecutor()
    }

    private val mainThread by lazy {
        Handler(Looper.getMainLooper())
    }

    var localFileCount = 0
        private set
    var remoteFileCount = 0
        private set

    var pending: Pending = Pending.None

    val allFiles: List<FtsTarget>
        get() {
            return fileList
        }

    val allFlows: List<Options>
        get() {
            return flowList
        }

    fun init(context: Context) {
        cacheDir = context.cacheDir
    }

    fun remoteFiles(): List<FtsTarget.Remote> {
        return fileList.filterIsInstance<FtsTarget.Remote>()
    }

    fun localFiles(): List<FtsTarget.Local> {
        return fileList.filterIsInstance<FtsTarget.Local>()
    }

    /**
     * 暂存远程文件
     */
    fun holdRemote(ftpPath: String, isDir: Boolean) {
        if (ftpPath.isEmpty()) {
            return
        }
        remoteFileCount++
        fileList.addLast(FtsTarget.Remote(ftpPath, isDir))
    }

    /**
     * 暂存本地文件
     */
    fun holdLocal(fileUri: Uri) {
        if (fileUri == Uri.EMPTY || fileUri.path.isNullOrEmpty()) {
            return
        }
        localFileCount++
        fileList.addLast(FtsTarget.Local(fileUri))
    }

    /**
     * 释放文件
     */
    fun release(file: FtsTarget) {
        if (fileList.remove(file)) {
            if (file is FtsTarget.Local) {
                localFileCount--
            } else if (file is FtsTarget.Remote) {
                remoteFileCount--
            }
        }
    }

    /**
     * 上传本地文件到FTP
     */
    fun upload(dirUri: String, files: List<FtsTarget.Local>) {
        if (files.isEmpty()) {
            return
        }
        if (dirUri.isEmpty()) {
            return
        }
        files.forEach {
            flowList.add(Options.Transmission(it, FtsTarget.Remote(dirUri, isDir = true)))
        }
    }

    /**
     * 下载FTP文件到本地
     */
    fun download(dirUri: Uri, files: List<FtsTarget.Remote>) {
        if (files.isEmpty()) {
            return
        }
        if (dirUri == Uri.EMPTY) {
            return
        }
        files.forEach {
            flowList.add(Options.Transmission(it, FtsTarget.Local(dirUri)))
        }
    }

    /**
     * 复制FTP之间的文件
     */
    fun copy(dirUri: String, files: List<FtsTarget.Remote>) {
        if (dirUri.isEmpty()) {
            return
        }
        if (files.isEmpty()) {
            return
        }
        val tempDir = File(cacheDir, createFileName())
        files.forEach { file ->
            val fileName = findFileName(file.path)
            // 暂存到自己的目录
            val cacheFile = File(tempDir, fileName)
            flowList.add(Options.Transmission(file, FtsTarget.Cache(cacheFile)))
            // 上传到目标目录
            flowList.add(
                Options.Transmission(
                    FtsTarget.Cache(cacheFile),
                    FtsTarget.Remote(dirUri, isDir = true)
                )
            )
        }
    }

    /**
     * 移动FTP之间的文件
     */
    fun move(dirUri: String, files: List<FtsTarget.Remote>) {
        if (dirUri.isEmpty()) {
            return
        }
        if (files.isEmpty()) {
            return
        }
        files.forEach { srcFile ->
            rename(srcFile.path, getFilePath(dirUri, findFileName(srcFile.path)), srcFile.isDir)
        }
    }

    /**
     * 重命名FTP文件
     */
    fun rename(target: String, newName: String, isDir: Boolean) {
        flowList.add(
            Options.Rename(
                FtsTarget.Remote(target, isDir),
                FtsTarget.Remote(newName, isDir)
            )
        )
    }

    /**
     * 执行FTP操作
     */
    fun executeOptions(client: FtpManager.Client, list: List<Options>, callback: ExecuteCallback) {
        onUI {
            callback.onStart()
        }
        if (list.isEmpty()) {
            // 如果为空，那么就给他们1轮时间来结束
            onUI {
                callback.onEnd(emptyList())
            }
            return
        }
        doAsync {
            val pendingList = ArrayList<Options>()
            pendingList.addAll(list)
            val count = pendingList.size
            val resultList = ArrayList<ExecuteResult>()
            for (index in pendingList.indices) {
                val option = pendingList[index]
                try {
                    when (option) {
                        is Options.Delete -> {
                            deleteFile(client, option.target) { progress ->
                                callback.onProgress(count, index, option, progress)
                            }
                        }

                        is Options.Transmission -> {
                            transmission(client, option.from, option.target) { progress ->
                                callback.onProgress(count, index, option, progress)
                            }
                        }

                        is Options.Rename -> {
                            renameRemote(client, option) { progress ->
                                callback.onProgress(count, index, option, progress)
                            }
                        }
                    }
                } catch (e: Throwable) {
                    resultList.add(ExecuteResult.Failed(option, e))
                    Log.e("FileTransferStation", "executeOptions: $index/$count", e)
                }
            }
        }
    }

    private fun transmission(
        client: FtpManager.Client,
        from: FtsTarget,
        to: FtsTarget,
        callback: (Float) -> Unit
    ) {
        when (to) {
            is FtsTarget.Cache -> {
                transmissionToCache(client, from, to, callback)
            }

            is FtsTarget.Local -> {
                transmissionToLocal(client, from, to, callback)
            }

            is FtsTarget.Remote -> {
                when (from) {
                    is FtsTarget.Cache -> {
                        transmissionToRemote(client, from, to, callback)
                    }

                    is FtsTarget.Local -> {
                        throw IllegalArgumentException("from is local, not support")
                    }

                    is FtsTarget.Remote -> {
                        throw IllegalArgumentException("from is remote, not support")
                    }
                }
            }
        }
    }

    private fun transmissionToLocal(
        client: FtpManager.Client,
        from: FtsTarget,
        to: FtsTarget.Local,
        callback: (Float) -> Unit
    ) {
        callback(PROGRESS_MIN)
        // TODO
    }

    private fun transmissionToRemote(
        client: FtpManager.Client,
        from: FtsTarget.Cache,
        to: FtsTarget.Remote,
        callback: (Float) -> Unit
    ) {
        when (val changeDirResult = client.changeDirectorySync(to.path)) {
            is RequestResult.Success -> {
                callback(PROGRESS_MIN)
                val fromFile = from.file
                val fileLength = fromFile.length()
                client.uploadSync(fromFile, object : FTPDataTransferListener {
                    override fun started() {}

                    override fun transferred(length: Int) {
                        callback(length.toFloat() / fileLength)
                    }

                    override fun completed() {
                        callback(PROGRESS_MAX)
                    }

                    override fun aborted() {
                        throw FTPAbortedException()
                    }

                    override fun failed() {
                        throw FTPDataTransferException()
                    }
                })
            }

            is RequestResult.Failure -> {
                throw changeDirResult.error
            }
        }
    }

    private fun transmissionToCache(
        client: FtpManager.Client,
        from: FtsTarget,
        to: FtsTarget.Cache,
        callback: (Float) -> Unit
    ) {
        callback(PROGRESS_MIN)
        // TODO
    }

    private fun renameRemote(
        client: FtpManager.Client,
        option: Options.Rename,
        callback: (Float) -> Unit
    ) {
        callback(PROGRESS_MIN)
        client.rename(option.from.path, option.target.path) {
            callback(PROGRESS_MAX)
        }
    }

    private fun deleteFile(
        client: FtpManager.Client,
        target: FtsTarget,
        callback: (Float) -> Unit
    ) {
        when (target) {
            is FtsTarget.Cache -> {
                val cacheFile = target.file
                cacheFile.delete()
                callback(PROGRESS_MAX)
            }

            is FtsTarget.Local -> {
                // 本地的远程URI不能被删除
                callback(PROGRESS_MAX)
            }

            is FtsTarget.Remote -> {
                callback(PROGRESS_MIN)
                if (target.isDir) {
                    client.deleteDirectorySync(target.path)
                    callback(PROGRESS_MAX)
                } else {
                    client.deleteFileSync(target.path)
                    callback(PROGRESS_MAX)
                }
            }
        }
    }

    private fun doAsync(callback: () -> Unit) {
        executor.execute {
            try {
                callback()
            } catch (e: Throwable) {
                Log.e("FileTransferStation", "doAsync: ", e)
            }
        }
    }

    private fun onUI(callback: () -> Unit) {
        mainThread.post {
            try {
                callback()
            } catch (e: Throwable) {
                Log.e("FileTransferStation", "onUI: ", e)
            }
        }
    }

    private fun createFileName(): String {
        return "temp_${System.currentTimeMillis()}"
    }

    fun findFileName(path: String): String {
        val index = path.lastIndexOf("/")
        if (index < 0) {
            return path
        }
        return path.substring(index + 1)
    }

    fun getFilePath(dir: String, fileName: String): String {
        return "$dir/$fileName"
    }

    fun delete(path: String, isDir: Boolean) {
        flowList.add(Options.Delete(FtsTarget.Remote(path, isDir)))
    }

    fun removeFlowOption(it: Options) {
        flowList.remove(it)
    }

    sealed class FtsTarget {

        class Local(val uri: Uri) : FtsTarget()

        class Remote(val path: String, val isDir: Boolean) : FtsTarget()

        class Cache(val file: File) : FtsTarget()

    }

    sealed class Options {

        class Delete(val target: FtsTarget) : Options()

        class Transmission(val from: FtsTarget, val target: FtsTarget) : Options()

        class Rename(val from: FtsTarget.Remote, val target: FtsTarget.Remote) : Options()

    }

    enum class Pending {
        None,
        Download,
        Upload,
        Copy,
        Move;

        fun oneOf(vararg pending: Pending): Boolean {
            return pending.any { it == this }
        }

    }

    class ExecuteCallbackWrapper(
        val onStartCallback: () -> Unit,
        val onProgressCallback: (count: Int, index: Int, option: Options, progress: Float) -> Unit,
        val onEndCallback: (list: List<ExecuteResult>) -> Unit
    ) : ExecuteCallback {
        override fun onStart() {
            onStartCallback()
        }

        override fun onProgress(count: Int, index: Int, option: Options, progress: Float) {
            onProgressCallback(count, index, option, progress)
        }

        override fun onEnd(list: List<ExecuteResult>) {
            onEndCallback(list)
        }
    }

    interface ExecuteCallback {
        fun onStart()
        fun onProgress(count: Int, index: Int, option: Options, progress: Float)
        fun onEnd(list: List<ExecuteResult>)
    }

    sealed class ExecuteResult {

        class Success(val option: Options) : ExecuteResult()
        class Failed(val option: Options, val error: Throwable) : ExecuteResult()

    }

}