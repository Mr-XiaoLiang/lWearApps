package com.lollipop.file.sender.ftp.fts

interface FTSOptionCallback {
    fun onProgress(progress: Float)
}

class FTSOptionStepCallback(
    val count: Int,
    val index: Int,
    val option: FTSOption,
    val callback: FTSExecuteCallback
) : FTSOptionCallback {
    override fun onProgress(progress: Float) {
        callback.onProgress(count, index, option, progress)
    }
}
