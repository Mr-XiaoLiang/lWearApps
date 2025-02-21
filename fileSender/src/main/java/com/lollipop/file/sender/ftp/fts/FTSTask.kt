package com.lollipop.file.sender.ftp.fts

import androidx.lifecycle.Lifecycle
import com.lollipop.file.sender.ftp.FileTransferStation
import com.lollipop.file.sender.ftp.FtpManager
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class FTSTask(
    val taskId: String,
    val ftpToken: String,
    val optionArray: Array<FTSOption>,
    val contextProvider: FTSContextProvider,
) : Runnable, FTSExecuteDispatcher {

    private val resultList = CopyOnWriteArrayList<ExecuteResult>()
    private val resultOptionMap = ConcurrentHashMap<Int, ExecuteResult>()

    private val dispatcher = FTSExecuteCallbackDispatcher()
    private val callback = FTSExecuteCallbackCacheWrapper(dispatcher)

    val isStart: Boolean
        get() = callback.isStart
    val isEnd: Boolean
        get() = callback.isEnd
    val count: Int
        get() = callback.count
    val index: Int
        get() = callback.index
    val currentOption: FTSOption?
        get() = callback.currentOption
    val currentProgress: Float
        get() = callback.currentProgress

    val result: List<ExecuteResult>
        get() {
            return resultList
        }

    fun findResult(id: Int): ExecuteResult? {
        return resultOptionMap[id]
    }

    override fun add(callback: FTSExecuteCallback) {
        dispatcher.add(callback)
    }

    override fun remove(callback: FTSExecuteCallback) {
        dispatcher.remove(callback)
    }

    fun addWithLifecycle(
        lifecycle: Lifecycle,
        cancelOn: Lifecycle.Event = Lifecycle.Event.ON_DESTROY,
        callbackImpl: FTSExecuteCallback
    ): FTSExecuteCallbackWithLifecycle {
        val impl = FTSExecuteCallbackWithLifecycle(lifecycle, this, cancelOn, callbackImpl)
        add(impl)
        return impl
    }

    private fun addResult(result: ExecuteResult) {
        resultOptionMap[result.optionId] = result
        resultList.add(result)
    }

    override fun run() {
        callback.onStart()
        val client = FtpManager.findClient(ftpToken)
        if (client == null) {
            optionArray.forEach {
                addResult(ExecuteResult.Failed(it, Throwable("Client is not found")))
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
                            FTSCacheExecutor.execute(client, option, contextProvider, stepCallback)
                        }

                        is FTSOption.Delete -> {
                            FTSDeleteExecutor.execute(client, option, contextProvider, stepCallback)
                        }

                        is FTSOption.Download -> {
                            FTSDownloadExecutor.execute(
                                client,
                                option,
                                contextProvider,
                                stepCallback
                            )
                        }

                        is FTSOption.Rename -> {
                            FTSRenameExecutor.execute(client, option, contextProvider, stepCallback)
                        }

                        is FTSOption.Save -> {
                            FTSSaveExecutor.execute(client, option, contextProvider, stepCallback)
                        }

                        is FTSOption.Upload -> {
                            FTSUploadExecutor.execute(client, option, contextProvider, stepCallback)
                        }
                    }
                } catch (e: Throwable) {
                    FTSExecutorResult.Fail(e)
                }
                stepCallback.onProgress(FileTransferStation.PROGRESS_MAX)
                when (result) {
                    is FTSExecutorResult.Fail -> {
                        addResult(ExecuteResult.Failed(option, result.error))
                    }

                    FTSExecutorResult.Success -> {
                        addResult(ExecuteResult.Success(option))
                    }
                }
            } catch (e: Throwable) {
                addResult(ExecuteResult.Failed(option, e))
            }
        }
        callback.onEnd(resultList)
    }

}

