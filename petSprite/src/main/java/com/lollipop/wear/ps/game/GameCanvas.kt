package com.lollipop.wear.ps.game

import android.graphics.Canvas
import android.graphics.Point
import android.view.SurfaceHolder

class GameCanvas : SurfaceHolder.Callback2, GameEngine.FrameCallback {

    private var currentHolder: SurfaceHolder? = null
    var frameIndex = 0
        private set
    var frameLoopCount = 0
        private set

    private var backgroundPainter: Painter? = null
    private var spritePainter: Painter? = null
    private var foregroundPainter: Painter? = null
    private val canvasSize = Point(0, 0)

    override fun surfaceCreated(holder: SurfaceHolder) {
        currentHolder = holder
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        currentHolder = holder
        canvasSize.set(width, height)
        updateSize(width, height)
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        if (currentHolder === holder) {
            currentHolder = null
        }
    }

    override fun surfaceRedrawNeeded(holder: SurfaceHolder) {
        currentHolder = holder
    }

    override fun onNewFrame() {
        updateFrameCount()
        val holder = currentHolder ?: return
        val canvas = holder.lockCanvas()
        try {
            canvas.drawColor(0xFF000000.toInt())
            draw(backgroundPainter, canvas)
            draw(spritePainter, canvas)
            draw(foregroundPainter, canvas)
        } finally {
            holder.unlockCanvasAndPost(canvas)
        }
    }

    private fun draw(painter: Painter?, canvas: Canvas) {
        painter ?: return
        if (painter.isReady) {
            painter.onDraw(canvas)
        }
    }

    fun setBackgroundPainter(painter: Painter?) {
        backgroundPainter = painter
        painter?.onSizeChanged(canvasSize.x, canvasSize.y)
    }

    fun setSpritePainter(painter: Painter?) {
        spritePainter = painter
        painter?.onSizeChanged(canvasSize.x, canvasSize.y)
    }

    fun setForegroundPainter(painter: Painter?) {
        foregroundPainter = painter
        painter?.onSizeChanged(canvasSize.x, canvasSize.y)
    }

    private fun updateSize(width: Int, height: Int) {
        backgroundPainter?.onSizeChanged(width, height)
        spritePainter?.onSizeChanged(width, height)
        foregroundPainter?.onSizeChanged(width, height)
    }

    private fun updateFrameCount() {
        frameIndex++
        if (frameIndex >= Int.MAX_VALUE) {
            frameIndex = 0
            frameLoopCount++
        }
    }

    interface Painter {

        val isReady: Boolean

        fun onDraw(canvas: Canvas)

        fun onSizeChanged(width: Int, height: Int)

        fun onGroundEdgeChanged(groundTopEdge: Float, groundBottomEdge: Float)

    }

}