package com.lollipop.wear.ps.game

import android.content.Context
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.FrameLayout
import com.lollipop.wear.ps.game.GamePainter.Painter

class GamePlayer @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : FrameLayout(context, attributeSet) {

    private val gameEngine = GameEngine()
    private val surfaceView = SurfaceView(context)
    private val gamePainter = GamePainter()

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
            return gamePainter.frameIndex
        }
    val frameLoopCount: Int
        get() {
            return gamePainter.frameLoopCount
        }

    init {
        addView(surfaceView)
        surfaceView.holder.addCallback(surfaceCallback)
        surfaceView.holder.addCallback(gamePainter)
        gameEngine.addFrameCallback(gamePainter)
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

    fun setBackgroundPainter(painter: Painter?) {
        gamePainter.setBackgroundPainter(painter)
    }

    fun setSpritePainter(painter: Painter?) {
        gamePainter.setSpritePainter(painter)
    }

    fun setForegroundPainter(painter: Painter?) {
        gamePainter.setForegroundPainter(painter)
    }

}