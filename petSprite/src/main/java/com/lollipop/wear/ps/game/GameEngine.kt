package com.lollipop.wear.ps.game

import android.os.Handler
import android.os.HandlerThread
import android.os.SystemClock
import com.lollipop.wear.basic.ListenerManager

class GameEngine {

    private val gameThread by lazy {
        HandlerThread("GameEngine@${System.identityHashCode(this)}").apply { start() }
    }

    private val gameHandler by lazy {
        Handler(gameThread.looper)
    }

    // 帧率默认是60，但是为了避免误差，所以向上激进一点
    var fps = 65

    private val frameCallbackList = ListenerManager<FrameCallback>()

    var gameState = GameState.Pause
        private set

    private val frameTask = Runnable {
        onNextFrame()
    }

    fun addFrameCallback(callback: FrameCallback) {
        frameCallbackList.add(callback)
    }

    fun removeFrameCallback(callback: FrameCallback) {
        frameCallbackList.remove(callback)
    }

    fun resume() {
        changedState(GameState.Running)
        gameHandler.post(frameTask)
    }

    fun pause() {
        changedState(GameState.Pause)
        gameHandler.removeCallbacks(frameTask)
    }

    fun destroy() {
        changedState(GameState.Destroy)
        gameHandler.removeCallbacks(frameTask)
        gameThread.quitSafely()
    }

    private fun onNextFrame() {
        val interval = frameInterval()
        val frameStart = now()
        frameCallbackList.invoke { it.onNewFrame() }
        // 控制帧率
        gameHandler.postDelayed(frameTask, interval - (now() - frameStart))
    }

    private fun frameInterval(): Long {
        return 1000L / fps
    }

    private fun now(): Long {
        return SystemClock.uptimeMillis()
    }

    private fun changedState(state: GameState) {
        gameState = state
    }

    enum class GameState {
        Running,
        Pause,
        Destroy
    }

    interface FrameCallback {
        fun onNewFrame()
    }

}