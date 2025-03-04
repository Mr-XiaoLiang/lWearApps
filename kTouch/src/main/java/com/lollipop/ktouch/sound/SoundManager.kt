package com.lollipop.ktouch.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.util.Log
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

object SoundManager {

    private val soundIdMap = ConcurrentHashMap<SoundKey, Int>()

    private val soundPool by lazy {
        createSoundPool()
    }

    private val executor by lazy {
        Executors.newSingleThreadExecutor()
    }

    private fun createSoundPool(): SoundPool {
        val builder = SoundPool.Builder()
        builder.setMaxStreams(SoundKey.entries.size)
        val attrBuilder = AudioAttributes.Builder()
        attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC)
        builder.setAudioAttributes(attrBuilder.build())
        val pool = builder.build()
        return pool
    }

    fun load(context: Context, soundKey: SoundKey) {
        if (soundIdMap.containsKey(soundKey)) {
            return
        }
        executor.execute {
            loadSync(context, soundKey)
        }
    }

    fun loadSync(context: Context, soundKey: SoundKey): Int {
        try {
            if (soundIdMap.containsKey(soundKey)) {
                return soundIdMap[soundKey] ?: 0
            }
            val id = soundPool.load(context, soundKey.resId, 1)
            soundIdMap[soundKey] = id
        } catch (e: Exception) {
            Log.e("SoundManager", "load sound error", e)
        }
        return 0
    }

    fun play(soundKey: SoundKey) {
        val id = soundIdMap[soundKey] ?: return
        soundPool.play(id, 1f, 1f, 1, 0, 1f)
    }

    fun preload(context: Context, array: Array<SoundKey>) {
        array.forEach {
            load(context, it)
        }
    }

}