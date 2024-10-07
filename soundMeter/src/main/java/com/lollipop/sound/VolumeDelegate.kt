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


class VolumeDelegate(
    private val activity: Activity,
    private val volumeCallback: (dB: Double) -> Unit
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

        private const val SAMPLE_RATE_IN_HZ = 8000;

    }

    private var audioRecord: AudioRecord? = null

    private val asyncHandler by lazy {
        android.os.Handler(asyncThread.looper)
    }
    private val mainHandler by lazy {
        android.os.Handler(Looper.getMainLooper())
    }

    private fun hasPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private var isActive = false
    private var isEnable = true

    private val recordTask = Runnable {
        getNoiseLevel()
    }

    private fun postNextRecordTask() {
        asyncHandler.removeCallbacks(recordTask)
        asyncHandler.postDelayed(recordTask, 100)
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
        postNextRecordTask()
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

    private fun getMinBufferSize(): Int {
        return AudioRecord.getMinBufferSize(
            SAMPLE_RATE_IN_HZ,
            AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT
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
        return newRecord
    }

    private fun postMain(runnable: Runnable) {
        mainHandler.post(runnable)
    }

    private fun getNoiseLevel() {
        if (!isEnable || !isActive) {
            return
        }
        val record = getAudioRecord() ?: return
        if (record.state != AudioRecord.STATE_INITIALIZED) {
            postNextRecordTask()
            return
        }
        if (record.recordingState != AudioRecord.RECORDSTATE_RECORDING) {
            try {
                record.startRecording()
                postNextRecordTask()
            } catch (e: Throwable) {
                logE(e)
            }
            return
        }
        val bufferSize = getMinBufferSize()
        val buffer = ShortArray(bufferSize)
        //r是实际读取的数据长度，一般而言r会小于bufferSize
        val r: Int = record.read(buffer, 0, bufferSize)
        var v: Long = 0
        // 将 buffer 内容取出，进行平方和运算
        for (value in buffer) {
            v += (value * value).toLong()
        }
        // 平方和除以数据总长度，得到音量大小。
        val mean = v / r.toDouble()
        val volume = 10 * log10(mean)
        postMain {
            volumeCallback(volume)
        }
        if (isEnable && isActive) {
            postNextRecordTask()
        }
    }

}