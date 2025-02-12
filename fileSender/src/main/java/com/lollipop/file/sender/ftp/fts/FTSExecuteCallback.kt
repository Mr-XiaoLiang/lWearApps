package com.lollipop.file.sender.ftp.fts

import android.os.Handler

interface FTSExecuteCallback {
    fun onStart()
    fun onProgress(count: Int, index: Int, option: FTSOption, progress: Float)
    fun onEnd(list: List<ExecuteResult>)
}

class ExecuteCallbackSimpleWrapper(
    val onStartCallback: () -> Unit,
    val onProgressCallback: (count: Int, index: Int, option: FTSOption, progress: Float) -> Unit,
    val onEndCallback: (list: List<ExecuteResult>) -> Unit
) : FTSExecuteCallback {
    override fun onStart() {
        onStartCallback()
    }

    override fun onProgress(count: Int, index: Int, option: FTSOption, progress: Float) {
        onProgressCallback(count, index, option, progress)
    }

    override fun onEnd(list: List<ExecuteResult>) {
        onEndCallback(list)
    }
}

class ExecuteCallbackHandlerWrapper(
    private val handler: Handler,
    private val limitTime: Long,
    private val callback: FTSExecuteCallback
) :
    FTSExecuteCallback {

    private var currentCount = 0
    private var currentIndex = 0
    private var currentOption: FTSOption? = null
    private var currentProgress = 0f
    private var lastUpdateTime = 0L
    private var isEnd = false

    private fun now(): Long {
        return System.currentTimeMillis()
    }

    override fun onStart() {
        handler.post {
            callback.onStart()
        }
    }

    override fun onProgress(count: Int, index: Int, option: FTSOption, progress: Float) {
        currentCount = count
        currentIndex = index
        currentOption = option
        currentProgress = progress
        val now = now()
        if (now - lastUpdateTime >= limitTime) {
            lastUpdateTime = now
            update()
        }
    }

    private fun update() {
        if (isEnd) {
            return
        }
        val count = currentCount
        val index = currentIndex
        val option = currentOption
        val progress = currentProgress
        handler.post {
            callback.onProgress(count, index, option!!, progress)
        }
    }

    override fun onEnd(list: List<ExecuteResult>) {
        isEnd = true
        handler.post {
            callback.onEnd(list)
        }
    }

}
