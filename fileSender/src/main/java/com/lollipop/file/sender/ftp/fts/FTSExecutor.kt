package com.lollipop.file.sender.ftp.fts

import android.webkit.MimeTypeMap
import androidx.documentfile.provider.DocumentFile
import com.lollipop.file.sender.ftp.FileTransferStation
import com.lollipop.file.sender.ftp.FileTransferStation.PROGRESS_MAX
import com.lollipop.file.sender.ftp.FileTransferStation.PROGRESS_MIN
import com.lollipop.file.sender.ftp.FtpManager
import com.lollipop.file.sender.ftp.RequestResult
import it.sauronsoftware.ftp4j.FTPAbortedException
import it.sauronsoftware.ftp4j.FTPDataTransferException
import it.sauronsoftware.ftp4j.FTPDataTransferListener
import java.io.File
import java.io.InputStream
import java.io.OutputStream

interface FTSExecutor<O : FTSOption> {

    fun execute(
        client: FtpManager.Client,
        option: O,
        contextProvider: FTSContextProvider,
        callback: FTSOptionCallback
    ): FTSExecutorResult

}

abstract class BasicFTSExecutor<O : FTSOption> : FTSExecutor<O> {

    fun illegalArgument(msg: String): FTSExecutorResult.Fail {
        return FTSExecutorResult.illegalArgument(msg)
    }

    protected fun copyFile(
        inputStream: InputStream,
        outputStream: OutputStream,
        callback: (Long) -> Unit
    ) {
        val buffer = ByteArray(1024 * 4)
        var writeCount = 0L
        do {
            val readCount = inputStream.read(buffer)
            if (readCount < 0) {
                break
            }
            outputStream.write(buffer, 0, readCount)
            writeCount += readCount
            callback(writeCount)
        } while (true)
        outputStream.flush()
    }

}

object FTSCacheExecutor : BasicFTSExecutor<FTSOption.Cache>() {
    override fun execute(
        client: FtpManager.Client,
        option: FTSOption.Cache,
        contextProvider: FTSContextProvider,
        callback: FTSOptionCallback
    ): FTSExecutorResult {
        val fromUri = option.from.uri
        val targetFile = option.target.file
        val appContext =
            contextProvider.getAppContext() ?: return illegalArgument("AppContext is null")
        val contentResolver = appContext.contentResolver
            ?: return illegalArgument("ContentResolver is null")
        val inputStream = contentResolver.openInputStream(fromUri)
            ?: return illegalArgument("InputStream is null")
        val allCount = DocumentFile.fromSingleUri(appContext, fromUri)?.length() ?: 0
        inputStream.use { i ->
            targetFile.outputStream().use { o ->
                copyFile(i, o) { writeCount ->
                    callback.onProgress(writeCount.toFloat() / allCount)
                }
            }
        }
        return FTSExecutorResult.Success
    }
}

object FTSDeleteExecutor : BasicFTSExecutor<FTSOption.Delete>() {
    override fun execute(
        client: FtpManager.Client,
        option: FTSOption.Delete,
        contextProvider: FTSContextProvider,
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
                    illegalArgument("File delete failed")
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

object FTSDownloadExecutor : BasicFTSExecutor<FTSOption.Download>() {
    override fun execute(
        client: FtpManager.Client,
        option: FTSOption.Download,
        contextProvider: FTSContextProvider,
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

object FTSRenameExecutor : BasicFTSExecutor<FTSOption.Rename>() {
    override fun execute(
        client: FtpManager.Client,
        option: FTSOption.Rename,
        contextProvider: FTSContextProvider,
        callback: FTSOptionCallback
    ): FTSExecutorResult {
        val result = client.renameSync(option.from.path, option.target.path)
        callback.onProgress(PROGRESS_MAX)
        return FTSExecutorResult.from(result)
    }
}

object FTSSaveExecutor : BasicFTSExecutor<FTSOption.Save>() {
    override fun execute(
        client: FtpManager.Client,
        option: FTSOption.Save,
        contextProvider: FTSContextProvider,
        callback: FTSOptionCallback
    ): FTSExecutorResult {
        val from = option.from
        val fromFile = from.file
        val target = option.target
        val appContext = contextProvider.getAppContext()
            ?: return illegalArgument("App context is null")
        val outDir = DocumentFile.fromTreeUri(appContext, target.uri)
            ?: return illegalArgument("Document file is null")
        val targetName = findNewName(outDir.listFiles(), fromFile.name)
        val targetFile = outDir.createFile(getFileMimeType(fromFile), targetName)
            ?: return illegalArgument("Target file is null")
        val outSteam = appContext.contentResolver.openOutputStream(targetFile.uri)
            ?: return illegalArgument("Output stream is null")
        val allCount = fromFile.length()
        outSteam.use { o ->
            fromFile.inputStream().use { i ->
                copyFile(i, o) { writeCount ->
                    callback.onProgress(writeCount.toFloat() / allCount)
                }
            }
        }
        return FTSExecutorResult.Success
    }

    private fun getFileMimeType(file: File): String {
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
            file.extension.lowercase()
        )
        return mimeType ?: "*/*"
    }

    private fun createNewName(name: String): String {
        val content = name.substringBeforeLast(".")
        val extension = name.substringAfterLast(".")
        return "${content}_1.${extension}"
    }

    private fun findNewName(files: Array<DocumentFile>, name: String): String {
        var newName = name
        while (true) {
            var pass = true
            for (file in files) {
                if (file.name == newName) {
                    newName = createNewName(newName)
                    pass = false
                    break
                }
            }
            if (pass) {
                return newName
            }
        }
    }

}

object FTSUploadExecutor : BasicFTSExecutor<FTSOption.Upload>() {
    override fun execute(
        client: FtpManager.Client,
        option: FTSOption.Upload,
        contextProvider: FTSContextProvider,
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

        fun illegalArgument(msg: String): Fail {
            return Fail(IllegalArgumentException(msg))
        }

    }

}
