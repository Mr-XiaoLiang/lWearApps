package com.lollipop.wear.devices

import android.os.Handler
import android.os.Looper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TimeViewDelegate(
    private val mainHandler: Handler = Handler(Looper.getMainLooper()),
    private val dateFormat: SimpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault()),
    private val onTimeChanged: (String) -> Unit
) {

    private val minuteTimer by lazy {
        MinuteTimer(mainHandler) {
            updateTime()
        }
    }

    private fun updateTime() {
        onTimeChanged(dateFormat.format(Date(System.currentTimeMillis())))
    }

    fun onResume() {
        updateTime()
        minuteTimer.onResume()
    }

    fun onPause() {
        minuteTimer.onPause()
    }

    private class MinuteTimer(
        private val handler: Handler,
        private val callback: () -> Unit
    ) {

        companion object {
            private const val MINUTE = 60 * 1000L
        }

        private val updateTask = Runnable {
            next()
            callback()
        }

        fun onResume() {
            next()
        }

        private fun next() {
            val now = now()
            val offset = now % MINUTE
            handler.removeCallbacks(updateTask)
            handler.postDelayed(updateTask, MINUTE - offset)
        }

        fun onPause() {
            handler.removeCallbacks(updateTask)
        }

        private fun now(): Long {
            return System.currentTimeMillis()
        }

    }

}