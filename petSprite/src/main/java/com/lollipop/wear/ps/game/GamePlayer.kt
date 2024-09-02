package com.lollipop.wear.ps.game

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.FrameLayout

class GamePlayer @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : FrameLayout(context, attributeSet) {

    private val gameEngine = GameEngine()
    private val surfaceView = SurfaceView(context)
    private val gameCanvas = GameCanvas()

    private val surfaceCallback = object : SurfaceHolder.Callback2 {
        override fun surfaceCreated(holder: SurfaceHolder) {
            TODO("Not yet implemented")
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            TODO("Not yet implemented")
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            TODO("Not yet implemented")
        }

        override fun surfaceRedrawNeeded(holder: SurfaceHolder) {
            TODO("Not yet implemented")
        }

    }

    val frameIndex: Int
        get() {
            return gameCanvas.frameIndex
        }
    val frameLoopCount: Int
        get() {
            return gameCanvas.frameLoopCount
        }

    init {
        addView(surfaceView)
        surfaceView.holder.addCallback(surfaceCallback)
        surfaceView.holder.addCallback(gameCanvas)
        gameEngine.addFrameCallback(gameCanvas)
    }

    fun setFPS(fps: Int) {
        gameEngine.fps = fps
    }

    fun addFrameCallback(callback: GameEngine.FrameCallback) {
        gameEngine.addFrameCallback(callback)
    }

    fun removeFrameCallback(callback: GameEngine.FrameCallback) {
        gameEngine.removeFrameCallback(callback)
    }

    fun onResume() {
        gameEngine.resume()
    }

    fun onPause() {
        gameEngine.pause()
    }

    fun onDestroy() {
        gameEngine.destroy()
    }

    fun setBackgroundPainter(painter: GameCanvas.Painter?) {
        gameCanvas.setBackgroundPainter(painter)
    }

    fun setSpritePainter(painter: GameCanvas.Painter?) {
        gameCanvas.setSpritePainter(painter)
    }

    fun setForegroundPainter(painter: GameCanvas.Painter?) {
        gameCanvas.setForegroundPainter(painter)
    }

}