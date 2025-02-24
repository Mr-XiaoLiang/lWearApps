package com.lollipop.file.sender

import android.util.Log

class FTPLog(val prefix: String) {

    companion object {
        private const val TAG = "FTP"

        fun with(any: Any): FTPLog {
            val className = any::class.java.simpleName
            val hashCode = System.identityHashCode(any).toString(16).uppercase()
            return FTPLog("${className}@${hashCode}")
        }

    }

    private fun buildMsg(msg: String): String {
        return "$prefix: $msg"
    }

    private fun buildMsg(msg: String, e: Throwable): String {
        return "$prefix: $msg: ${e.message}"
    }

    fun d(msg: String) {
        Log.d(TAG, buildMsg(msg))
    }

    fun e(msg: String, e: Throwable? = null) {
        if (e == null) {
            Log.e(TAG, buildMsg(msg))
        } else {
            Log.e(TAG, buildMsg(msg, e), e)
        }
    }

    fun i(msg: String) {
        Log.i(TAG, buildMsg(msg))
    }

    fun w(msg: String) {
        Log.w(TAG, buildMsg(msg))
    }

}