package com.lollipop.wear.basic

import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.concurrent.Executors

object ThreadHelper {

    private val executor by lazy {
        Executors.newCachedThreadPool()
    }

    private val uiThread by lazy {
        Handler(Looper.getMainLooper())
    }

    private fun logError(e: Throwable) {
        Log.e("ThreadHelper", "work error", e)
    }

    fun doAsync(
        errorCallback: OnErrorCallback,
        block: () -> Unit
    ) {
        executor.submit {
            try {
                block()
            } catch (e: Throwable) {
                errorCallback.onError(e)
            }
        }
    }

    fun onUI(
        errorCallback: OnErrorCallback,
        block: () -> Unit
    ) {
        uiThread.post {
            try {
                block()
            } catch (e: Throwable) {
                errorCallback.onError(e)
            }
        }
    }

    fun interface OnErrorCallback {
        fun onError(e: Throwable)
    }

    object EmptyErrorCallback : OnErrorCallback {
        override fun onError(e: Throwable) {
            logError(e)
        }
    }

}

inline fun <reified T> T.doAsync(
    onError: ThreadHelper.OnErrorCallback = ThreadHelper.EmptyErrorCallback,
    crossinline block: T.() -> Unit
) {
    val target = this
    ThreadHelper.doAsync(onError) {
        block.invoke(target)
    }
}

inline fun <reified T> T.onUI(
    onError: ThreadHelper.OnErrorCallback = ThreadHelper.EmptyErrorCallback,
    crossinline block: T.() -> Unit
) {
    val target = this
    ThreadHelper.onUI(onError) {
        block.invoke(target)
    }
}
