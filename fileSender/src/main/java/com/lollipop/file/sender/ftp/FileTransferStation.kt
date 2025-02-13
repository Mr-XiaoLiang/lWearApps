package com.lollipop.file.sender.ftp

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.lollipop.file.sender.ftp.fts.ExecuteCallbackHandlerWrapper
import com.lollipop.file.sender.ftp.fts.FTSContextProvider
import com.lollipop.file.sender.ftp.fts.FTSExecuteCallback
import com.lollipop.file.sender.ftp.fts.FTSOption
import com.lollipop.file.sender.ftp.fts.FTSTask
import com.lollipop.file.sender.ftp.fts.FtsTarget
import java.io.File
import java.util.LinkedList
import java.util.concurrent.Executors

object FileTransferStation {

    const val PROGRESS_MAX = 1F
    const val PROGRESS_MIN = 0F
    const val PROGRESS_INDEFINITE = -1F

    private val fileList = LinkedList<FtsTarget>()

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

    val allFiles: List<FtsTarget>
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
        val tempDir = cacheDir ?: return
        files.forEach {
            val tempFile = File(tempDir, createFileName())
            flowList.add(FTSOption.Cache(it, FtsTarget.Cache(tempFile)))
            flowList.add(
                FTSOption.Upload(
                    FtsTarget.Cache(tempFile),
                    FtsTarget.Remote(dirUri, isDir = true)
                )
            )
            flowList.add(FTSOption.Delete(FtsTarget.Cache(tempFile)))
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
        val tempDir = cacheDir ?: return
        files.forEach {
            val tempFile = File(tempDir, createFileName())

            flowList.add(FTSOption.Download(it, FtsTarget.Cache(tempFile)))
            flowList.add(FTSOption.Save(FtsTarget.Cache(tempFile), FtsTarget.Local(dirUri)))
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
            flowList.add(FTSOption.Download(file, FtsTarget.Cache(cacheFile)))
            // 上传到目标目录
            flowList.add(
                FTSOption.Upload(
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
            FTSOption.Rename(
                FtsTarget.Remote(target, isDir),
                FtsTarget.Remote(newName, isDir)
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
        callback: FTSExecuteCallback
    ) {
        val task = FTSTask(
            client.info.token,
            list.toTypedArray(),
            ExecuteCallbackHandlerWrapper(mainThread, 50, callback)
        )
        taskList.add(task)
        executor.execute(task)
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
        flowList.add(FTSOption.Delete(FtsTarget.Remote(path, isDir)))
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