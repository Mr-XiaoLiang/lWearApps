package com.lollipop.file.sender.ftp

import android.net.Uri
import java.util.LinkedList

object FileTransferStation {

    val fileList = LinkedList<FtsTarget>()

    val flowList = LinkedList<Options>()

    var pending: Pending = Pending.None

    fun remoteFiles(): List<FtsTarget.Remote> {
        return fileList.filterIsInstance<FtsTarget.Remote>().toList()
    }

    fun localFiles(): List<FtsTarget.Local> {
        return fileList.filterIsInstance<FtsTarget.Local>().toList()
    }

    /**
     * 暂存远程文件
     */
    fun stashRemote(ftpPath: String) {
        if (ftpPath.isEmpty()) {
            return
        }
        fileList.addLast(FtsTarget.Remote(ftpPath))
    }

    /**
     * 暂存本地文件
     */
    fun stashLocal(fileUri: Uri) {
        fileList.addLast(FtsTarget.Local(fileUri))
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

    sealed class FtsTarget {

        class Local(val uri: Uri) : FtsTarget()

        class Remote(val path: String) : FtsTarget()

        class Cache(val name: String) : FtsTarget()

    }

    sealed class Options {

        class Delete(val target: FtsTarget) : Options()

        class Transmission(val from: FtsTarget, val to: FtsTarget) : Options()

        class Rename(val target: FtsTarget.Remote, val to: FtsTarget.Remote) : Options()

    }

    enum class Pending {
        None,
        Download,
        Upload,
        Copy,
        Move,
    }

}