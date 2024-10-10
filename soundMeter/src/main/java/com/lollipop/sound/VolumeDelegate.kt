package com.lollipop.sound

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min


class VolumeDelegate(
    private val activity: Activity,
    private val volumeCallback: VolumeCallback
) {

    companion object {
        private val asyncThread by lazy {
            HandlerThread("VolumeDelegate").apply {
                start()
            }
        }

        private fun logE(e: Throwable) {
            Log.e("VolumeDelegate", e.message, e)
        }

        private const val SAMPLE_RATE_IN_HZ = 4000

        /**
         * 10 * log10(MAX_PCM_MEAN.toDouble())
         */
        private const val MAX_VOLUME = 90F

        private const val UPDATE_HZ = 20
        private const val TASK_INTERVAL = 1000L / UPDATE_HZ

        fun getVolumeProgress(volume: Float): Float {
            return max(0f, min(1f, volume / MAX_VOLUME))
        }

    }

    private var audioRecord: AudioRecord? = null
    private var bufferSize = SAMPLE_RATE_IN_HZ
    private var bufferWeight = 1

    private val asyncHandler by lazy {
        android.os.Handler(asyncThread.looper)
    }
    private val mainHandler by lazy {
        android.os.Handler(Looper.getMainLooper())
    }

    fun hasPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private var isActive = false
    private var isEnable = true

    private val recordTask = Runnable {
        getNoiseLevelAsync()
    }

    private fun postNextRecordTask(delay: Long) {
        asyncHandler.removeCallbacks(recordTask)
        asyncHandler.postDelayed(recordTask, delay)
    }

    private fun getNoiseLevelAsync() {

        if (!isEnable || !isActive) {
            return
        }
        val record = getAudioRecord() ?: return
        if (record.state != AudioRecord.STATE_INITIALIZED) {
            postNextRecordTask(TASK_INTERVAL)
            return
        }
        if (record.recordingState != AudioRecord.RECORDSTATE_RECORDING) {
            try {
                record.startRecording()
                postNextRecordTask(TASK_INTERVAL)
            } catch (e: Throwable) {
                logE(e)
            }
            return
        }

        val begin = System.currentTimeMillis()

        val buffer = ShortArray(bufferSize)
        //r是实际读取的数据长度，一般而言r会小于bufferSize
        val requestLength = bufferSize / bufferWeight
        val readLength = record.read(buffer, 0, requestLength)

        if (readLength > 0) {
            var v: Long = 0
            // 读到多少算多少
            val max = min(readLength, bufferSize)
            // 将 buffer 内容取出，进行平方和运算
            for (index in 0 until max) {
                val value = buffer[index].toLong()
                v += value * value
            }
            // 平方和除以数据总长度，得到音量大小。
            val mean = v / (max.toDouble())
            val volume = (10 * log10(mean)).toFloat()
            val pcm = mean.toFloat()
            postMain {
                volumeCallback.onVolumeUpdate(max(0F, pcm), max(0F, volume))
            }
        }

        if (isEnable && isActive) {
            val now = System.currentTimeMillis()

            val delay = now - begin
            if (delay > TASK_INTERVAL) {
                bufferWeight++
                if (bufferWeight > bufferSize) {
                    bufferWeight = bufferSize
                }
                postNextRecordTask(0)
            } else {
                postNextRecordTask(TASK_INTERVAL - delay)
            }
        }
    }

    fun setEnable(enable: Boolean) {
        this.isEnable = enable
    }

    fun onStart() {
        if (!hasPermission()) {
            return
        }
        isActive = true
        try {
            getAudioRecord()?.let {
                if (it.state == AudioRecord.STATE_INITIALIZED
                    && it.recordingState == AudioRecord.RECORDSTATE_STOPPED
                ) {
                    it.startRecording()
                }
            }
        } catch (e: Throwable) {
            logE(e)
        }
        postNextRecordTask(TASK_INTERVAL)
    }

    fun onStop() {
        if (!hasPermission()) {
            return
        }
        isActive = false
        try {
            getAudioRecord()?.let {
                if (it.state == AudioRecord.STATE_INITIALIZED
                    && it.recordingState == AudioRecord.RECORDSTATE_RECORDING
                ) {
                    it.stop()
                }
            }
        } catch (e: Throwable) {
            logE(e)
        }
        asyncHandler.removeCallbacks(recordTask)
    }

    fun onDestroy() {
        asyncHandler.removeCallbacksAndMessages(null)
        audioRecord?.release()
        audioRecord = null
    }

    private fun getMinBufferSize(): Int {
        return AudioRecord.getMinBufferSize(
            SAMPLE_RATE_IN_HZ,
            AudioFormat.CHANNEL_IN_DEFAULT,
            AudioFormat.ENCODING_PCM_16BIT
        )
    }

    @SuppressLint("MissingPermission")
    private fun getAudioRecord(): AudioRecord? {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }
        val record = audioRecord
        if (record != null) {
            return record
        }

        val newRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE_IN_HZ,
            AudioFormat.CHANNEL_IN_DEFAULT,
            AudioFormat.ENCODING_PCM_16BIT,
            getMinBufferSize()
        )
        audioRecord = newRecord
        bufferSize = getMinBufferSize()
        return newRecord
    }

    private fun postMain(runnable: Runnable) {
        mainHandler.post(runnable)
    }

    fun interface VolumeCallback {
        fun onVolumeUpdate(pcm: Float, volume: Float)
    }

}