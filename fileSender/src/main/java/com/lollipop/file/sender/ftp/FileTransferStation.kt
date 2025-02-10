package com.lollipop.file.sender.ftp

import android.net.Uri
import java.util.LinkedList

object FileTransferStation {

    private val fileList = LinkedList<FtsTarget>()

    private val flowList = LinkedList<Options>()

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

    fun remoteFiles(): List<FtsTarget.Remote> {
        return fileList.filterIsInstance<FtsTarget.Remote>()
    }

    fun localFiles(): List<FtsTarget.Local> {
        return fileList.filterIsInstance<FtsTarget.Local>()
    }

    /**
     * 暂存远程文件
     */
    fun holdRemote(ftpPath: String) {
        if (ftpPath.isEmpty()) {
            return
        }
        remoteFileCount++
        fileList.addLast(FtsTarget.Remote(ftpPath))
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
            flowList.add(Options.Transmission(it, FtsTarget.Remote(dirUri)))
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
        files.forEach {
            val temp = getFileName()
            // 暂存到自己的目录
            flowList.add(Options.Transmission(it, FtsTarget.Cache(temp)))
            // 上传到目标目录
            flowList.add(Options.Transmission(FtsTarget.Cache(temp), FtsTarget.Remote(dirUri)))
        }
    }

    fun move(dirUri: String, files: List<FtsTarget.Remote>) {
        if (dirUri.isEmpty()) {
            return
        }
        if (files.isEmpty()) {
            return
        }
        files.forEach {
            val target = FtsTarget.Remote(getFilePath(dirUri, findFileName(it.path)))
            flowList.add(Options.Rename(it, target))
        }
    }

    fun rename(target: String, newName: String) {
        flowList.add(Options.Rename(FtsTarget.Remote(target), FtsTarget.Remote(newName)))
    }

    private fun getFileName(): String {
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

    fun delete(path: String) {
        flowList.add(Options.Delete(FtsTarget.Remote(path)))
    }

    fun removeFlowOption(it: Options) {
        flowList.remove(it)
    }

    sealed class FtsTarget {

        class Local(val uri: Uri) : FtsTarget()

        class Remote(val path: String) : FtsTarget()

        class Cache(val name: String) : FtsTarget()

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

}