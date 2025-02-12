package com.lollipop.file.sender.ftp.fts

import com.lollipop.file.sender.ftp.FileTransferStation
import com.lollipop.file.sender.ftp.FtpManager

class FTSTask(
    val ftpToken: String,
    val optionArray: Array<FTSOption>,
    val callback: FTSExecuteCallback
) : Runnable {

    private val resultList = ArrayList<ExecuteResult>()

    override fun run() {
        callback.onStart()
        val client = FtpManager.findClient(ftpToken)
        if (client == null) {
            optionArray.forEach {
                resultList.add(ExecuteResult.Failed(it, Throwable("Client is not found")))
            }
            callback.onEnd(resultList)
            return
        }
        for (index in optionArray.indices) {
            val option = optionArray[index]
            val allCount = optionArray.size
            try {
                val stepCallback = FTSOptionStepCallback(allCount, index, option, callback)
                stepCallback.onProgress(FileTransferStation.PROGRESS_INDEFINITE)
                val result = try {
                    when (option) {
                        is FTSOption.Cache -> {
                            FTSCacheExecutor.execute(client, option, stepCallback)
                        }

                        is FTSOption.Delete -> {
                            FTSDeleteExecutor.execute(client, option, stepCallback)
                        }

                        is FTSOption.Download -> {
                            FTSDownloadExecutor.execute(client, option, stepCallback)
                        }

                        is FTSOption.Rename -> {
                            FTSRenameExecutor.execute(client, option, stepCallback)
                        }

                        is FTSOption.Save -> {
                            FTSSaveExecutor.execute(client, option, stepCallback)
                        }

                        is FTSOption.Upload -> {
                            FTSUploadExecutor.execute(client, option, stepCallback)
                        }
                    }
                } catch (e: Throwable) {
                    FTSExecutorResult.Fail(e)
                }
                stepCallback.onProgress(FileTransferStation.PROGRESS_MAX)
                when (result) {
                    is FTSExecutorResult.Fail -> {
                        resultList.add(ExecuteResult.Failed(option, result.error))
                    }

                    FTSExecutorResult.Success -> {
                        resultList.add(ExecuteResult.Success(option))
                    }
                }
            } catch (e: Throwable) {
                resultList.add(ExecuteResult.Failed(option, e))
            }
        }
        callback.onEnd(resultList)
    }

}

