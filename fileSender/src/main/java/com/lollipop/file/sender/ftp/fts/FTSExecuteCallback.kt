package com.lollipop.file.sender.ftp.fts

import android.os.Handler
import androidx.collection.ArraySet
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

interface FTSExecuteCallback {
    fun onStart()
    fun onProgress(count: Int, index: Int, option: FTSOption, progress: Float)
    fun onEnd(list: List<ExecuteResult>)
}

interface FTSExecuteDispatcher {
    fun add(callback: FTSExecuteCallback)
    fun remove(callback: FTSExecuteCallback)
}

class FTSExecuteCallbackSimpleWrapper(
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

class FTSExecuteCallbackCacheWrapper(
    private val impl: FTSExecuteCallback
) : FTSExecuteCallback {

    var isStart = false
        private set

    var isEnd = false
        private set

    var count = 0
        private set
    var index = 0
        private set
    var currentOption: FTSOption? = null
        private set
    var currentProgress = 0F
        private set

    override fun onStart() {
        isStart = true
        impl.onStart()
    }

    override fun onProgress(count: Int, index: Int, option: FTSOption, progress: Float) {
        this.count = count
        this.index = index
        this.currentOption = option
        this.currentProgress = progress
        impl.onProgress(count, index, option, progress)
    }

    override fun onEnd(list: List<ExecuteResult>) {
        isEnd
        impl.onEnd(list)
    }

}

class FTSExecuteCallbackDispatcher : FTSExecuteCallback, FTSExecuteDispatcher {
    private val callbackList = ArraySet<FTSExecuteCallback>()

    override fun add(callback: FTSExecuteCallback) {
        callbackList.add(callback)
    }

    override fun remove(callback: FTSExecuteCallback) {
        callbackList.remove(callback)
    }

    override fun onStart() {
        callbackList.forEach {
            it.onStart()
        }
    }

    override fun onProgress(count: Int, index: Int, option: FTSOption, progress: Float) {
        callbackList.forEach {
            it.onProgress(count, index, option, progress)
        }
    }

    override fun onEnd(list: List<ExecuteResult>) {
        callbackList.forEach {
            it.onEnd(list)
        }
    }
}

class FTSExecuteCallbackWithLifecycle(
    private val lifecycle: Lifecycle,
    private val dispatcher: FTSExecuteDispatcher,
    private val cancelOn: Lifecycle.Event = Lifecycle.Event.ON_DESTROY,
    private val callback: FTSExecuteCallback
) : FTSExecuteCallback, LifecycleEventObserver {

    init {
        dispatcher.add(this)
        lifecycle.addObserver(this)
    }

    override fun onStart() {
        callback.onStart()
    }

    override fun onProgress(count: Int, index: Int, option: FTSOption, progress: Float) {
        callback.onProgress(count, index, option, progress)
    }

    override fun onEnd(list: List<ExecuteResult>) {
        callback.onEnd(list)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == cancelOn) {
            dispatcher.remove(this)
            lifecycle.removeObserver(this)
        }
    }
}

class FTSExecuteCallbackHandlerWrapper(
    private val handler: Handler,
    private val limitTime: Long,
    private val callback: FTSExecuteCallback
) : FTSExecuteCallback {

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
