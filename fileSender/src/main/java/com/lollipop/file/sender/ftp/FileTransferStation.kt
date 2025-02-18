package com.lollipop.file.sender.ftp

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.lollipop.file.sender.ftp.fts.FTSContextProvider
import com.lollipop.file.sender.ftp.fts.FTSOption
import com.lollipop.file.sender.ftp.fts.FTSTarget
import com.lollipop.file.sender.ftp.fts.FTSTask
import java.io.File
import java.util.LinkedList
import java.util.concurrent.Executors

object FileTransferStation {

    const val PROGRESS_MAX = 1F
    const val PROGRESS_MIN = 0F
    const val PROGRESS_INDEFINITE = -1F

    private val fileList = LinkedList<FTSTarget>()

    private val flowList = LinkedList<FTSOption>()

    private val taskList = ArrayList<FTSTask>()

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

    var currentClientToken: String = ""
        private set

    val allFiles: List<FTSTarget>
        get() {
            return fileList
        }

    val allFlows: List<FTSOption>
        get() {
            return flowList
        }

    val allTasks: List<FTSTask>
        get() {
            return taskList
        }

    fun init(context: Context) {
        cacheDir = context.cacheDir
    }

    fun updateCurrentClient(client: FtpManager.Client?) {
        currentClientToken = client?.info?.token ?: ""
    }

    fun remoteFiles(): List<FTSTarget.Remote> {
        return fileList.filterIsInstance<FTSTarget.Remote>()
    }

    fun localFiles(): List<FTSTarget.Local> {
        return fileList.filterIsInstance<FTSTarget.Local>()
    }

    /**
     * 暂存远程文件
     */
    fun holdRemote(ftpPath: String, isDir: Boolean) {
        if (ftpPath.isEmpty()) {
            return
        }
        remoteFileCount++
        fileList.addLast(FTSTarget.Remote(ftpPath, isDir))
    }

    /**
     * 暂存本地文件
     */
    fun holdLocal(fileUri: Uri) {
        if (fileUri == Uri.EMPTY || fileUri.path.isNullOrEmpty()) {
            return
        }
        localFileCount++
        fileList.addLast(FTSTarget.Local(fileUri))
    }

    /**
     * 释放文件
     */
    fun release(file: FTSTarget) {
        if (fileList.remove(file)) {
            if (file is FTSTarget.Local) {
                localFileCount--
            } else if (file is FTSTarget.Remote) {
                remoteFileCount--
            }
        }
    }

    /**
     * 上传本地文件到FTP
     */
    fun upload(dirUri: String, files: List<FTSTarget.Local>) {
        if (files.isEmpty()) {
            return
        }
        if (dirUri.isEmpty()) {
            return
        }
        val tempDir = cacheDir ?: return
        files.forEach {
            val tempFile = File(tempDir, createFileName())
            flowList.add(FTSOption.Cache(it, FTSTarget.Cache(tempFile)))
            flowList.add(
                FTSOption.Upload(
                    FTSTarget.Cache(tempFile),
                    FTSTarget.Remote(dirUri, isDir = true)
                )
            )
            flowList.add(FTSOption.Delete(FTSTarget.Cache(tempFile)))
        }
    }

    /**
     * 下载FTP文件到本地
     */
    fun download(dirUri: Uri, files: List<FTSTarget.Remote>) {
        if (files.isEmpty()) {
            return
        }
        if (dirUri == Uri.EMPTY) {
            return
        }
        val tempDir = cacheDir ?: return
        files.forEach {
            val tempFile = File(tempDir, createFileName())

            flowList.add(FTSOption.Download(it, FTSTarget.Cache(tempFile)))
            flowList.add(FTSOption.Save(FTSTarget.Cache(tempFile), FTSTarget.Local(dirUri)))
        }
    }

    /**
     * 复制FTP之间的文件
     */
    fun copy(dirUri: String, files: List<FTSTarget.Remote>) {
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
            flowList.add(FTSOption.Download(file, FTSTarget.Cache(cacheFile)))
            // 上传到目标目录
            flowList.add(
                FTSOption.Upload(
                    FTSTarget.Cache(cacheFile),
                    FTSTarget.Remote(dirUri, isDir = true)
                )
            )
        }
    }

    /**
     * 移动FTP之间的文件
     */
    fun move(dirUri: String, files: List<FTSTarget.Remote>) {
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
            FTSOption.Rename(
                FTSTarget.Remote(target, isDir),
                FTSTarget.Remote(newName, isDir)
            )
        )
    }

    /**
     * 执行FTP操作
     */
    fun executeOptions(
        client: FtpManager.Client,
        list: List<FTSOption>,
        contextProvider: FTSContextProvider,
    ): FTSTask {
        val task = FTSTask(
            client.info.token,
            list.toTypedArray(),
            contextProvider,
        )
        taskList.add(task)
        executor.execute(task)
        return task
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

    fun findDirPath(path: String): String {
        val index = path.lastIndexOf("/")
        if (index < 0) {
            return path
        }
        return path.substring(0, index)
    }

    fun getFilePath(dir: String, fileName: String): String {
        return "$dir/$fileName"
    }

    fun delete(path: String, isDir: Boolean) {
        flowList.add(FTSOption.Delete(FTSTarget.Remote(path, isDir)))
    }

    fun removeFlowOption(it: FTSOption) {
        flowList.remove(it)
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

}