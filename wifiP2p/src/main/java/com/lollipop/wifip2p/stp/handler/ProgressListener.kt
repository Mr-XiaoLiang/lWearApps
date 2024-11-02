package com.lollipop.wifip2p.stp.handler

interface ProgressListener {

    fun onProgress(progress: Float, total: Long, current: Long)

}