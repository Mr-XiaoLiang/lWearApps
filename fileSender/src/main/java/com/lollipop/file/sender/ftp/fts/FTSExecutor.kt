package com.lollipop.file.sender.ftp.fts

import com.lollipop.file.sender.ftp.FileTransferStation
import com.lollipop.file.sender.ftp.FileTransferStation.PROGRESS_MAX
import com.lollipop.file.sender.ftp.FileTransferStation.PROGRESS_MIN
import com.lollipop.file.sender.ftp.FtpManager
import com.lollipop.file.sender.ftp.RequestResult
import it.sauronsoftware.ftp4j.FTPAbortedException
import it.sauronsoftware.ftp4j.FTPDataTransferException
import it.sauronsoftware.ftp4j.FTPDataTransferListener

interface FTSExecutor<O : FTSOption> {

    fun execute(
        client: FtpManager.Client,
        option: O,
        callback: FTSOptionCallback
    ): FTSExecutorResult

}


object FTSCacheExecutor : FTSExecutor<FTSOption.Cache> {
    override fun execute(
        client: FtpManager.Client,
        option: FTSOption.Cache,
        callback: FTSOptionCallback
    ): FTSExecutorResult {
        TODO("Not yet implemented")
    }
}

object FTSDeleteExecutor : FTSExecutor<FTSOption.Delete> {
    override fun execute(
        client: FtpManager.Client,
        option: FTSOption.Delete,
        callback: FTSOptionCallback
    ): FTSExecutorResult {
        when (val target = option.target) {
            is FtsTarget.Cache -> {
                val cacheFile = target.file
                val result = cacheFile.delete()
                callback.onProgress(PROGRESS_MAX)
                return if (result) {
                    FTSExecutorResult.Success
                } else {
                    FTSExecutorResult.Fail(Throwable("File delete failed"))
                }
            }

            is FtsTarget.Local -> {
                // 本地的远程URI不能被删除
                callback.onProgress(PROGRESS_MAX)
                return FTSExecutorResult.Success
            }

            is FtsTarget.Remote -> {
                callback.onProgress(PROGRESS_MIN)
                if (target.isDir) {
                    val result = client.deleteDirectorySync(target.path)
                    callback.onProgress(PROGRESS_MAX)
                    return FTSExecutorResult.from(result)
                } else {
                    val result = client.deleteFileSync(target.path)
                    callback.onProgress(PROGRESS_MAX)
                    return FTSExecutorResult.from(result)
                }
            }
        }
    }
}

object FTSDownloadExecutor : FTSExecutor<FTSOption.Download> {
    override fun execute(
        client: FtpManager.Client,
        option: FTSOption.Download,
        callback: FTSOptionCallback
    ): FTSExecutorResult {
        val from = option.from
        val target = option.target
        val fromDir = FileTransferStation.findDirPath(from.path)
        val fromFile = FileTransferStation.findFileName(from.path)
        val cdResult = client.changeDirectorySync(fromDir)
        if (cdResult is RequestResult.Failure) {
            return FTSExecutorResult.from(cdResult)
        }
        val sizeResult = client.fileSizeSync(fromFile)
        val fileLength: Long
        if (sizeResult is RequestResult.Success) {
            fileLength = sizeResult.data
        } else {
            return FTSExecutorResult.from(sizeResult)
        }
        client.downloadSync(fromFile, target.file, object : FTPDataTransferListener {
                override fun started() {
                    callback.onProgress(PROGRESS_MIN)
                }

                override fun transferred(length: Int) {
                    callback.onProgress(length.toFloat() / fileLength)
                }

                override fun completed() {
                    callback.onProgress(PROGRESS_MAX)
                }

                override fun aborted() {
                    throw FTPAbortedException()
                }

                override fun failed() {
                    throw FTPDataTransferException()
                }
            })
        return FTSExecutorResult.Success
    }
}

object FTSRenameExecutor : FTSExecutor<FTSOption.Rename> {
    override fun execute(
        client: FtpManager.Client,
        option: FTSOption.Rename,
        callback: FTSOptionCallback
    ): FTSExecutorResult {
        val result = client.renameSync(option.from.path, option.target.path)
        callback.onProgress(PROGRESS_MAX)
        return FTSExecutorResult.from(result)
    }
}

object FTSSaveExecutor : FTSExecutor<FTSOption.Save> {
    override fun execute(
        client: FtpManager.Client,
        option: FTSOption.Save,
        callback: FTSOptionCallback
    ): FTSExecutorResult {
        TODO("Not yet implemented")
    }
}

object FTSUploadExecutor : FTSExecutor<FTSOption.Upload> {
    override fun execute(
        client: FtpManager.Client,
        option: FTSOption.Upload,
        callback: FTSOptionCallback
    ): FTSExecutorResult {
        val from = option.from
        val target = option.target
        when (val changeDirResult = client.changeDirectorySync(target.path)) {
            is RequestResult.Success -> {
                val fromFile = from.file
                val fileLength = fromFile.length()
                client.uploadSync(fromFile, object : FTPDataTransferListener {
                    override fun started() {
                        callback.onProgress(PROGRESS_MIN)
                    }

                    override fun transferred(length: Int) {
                        callback.onProgress(length.toFloat() / fileLength)
                    }

                    override fun completed() {
                        callback.onProgress(PROGRESS_MAX)
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
        return FTSExecutorResult.Success
    }
}


sealed class FTSExecutorResult {

    data object Success : FTSExecutorResult()

    class Fail(val error: Throwable) : FTSExecutorResult()

    companion object {
        fun from(result: RequestResult<*>): FTSExecutorResult {
            return when (result) {
                is RequestResult.Success -> {
                    Success
                }

                is RequestResult.Failure -> {
                    Fail(result.error)
                }
            }
        }
    }

}
