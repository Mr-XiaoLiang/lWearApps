package com.lollipop.wear.ttt.view

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import kotlin.math.min

class BoardBackgroundDrawable : Drawable() {

    private val boardPath = Path()

    private val paint = Paint().apply {
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.ROUND
    }

    var color: Int
        get() {
            return paint.color
        }
        set(value) {
            paint.color = value
            invalidateSelf()
        }

    var strokeWidth: Float
        get() {
            return paint.strokeWidth
        }
        set(value) {
            paint.strokeWidth = value
            buildPath()
        }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        buildPath()
    }

    private fun buildPath() {
        boardPath.reset()
        val capSize = strokeWidth / 2
        val width = bounds.width().toFloat()
        val height = bounds.height().toFloat()
        val left = bounds.left.toFloat()
        val top = bounds.top.toFloat()
        val size = min(width, height)
        val gridSize = size / 3
        // 格子只需要画中间2条线，总计4条线就行了。
        // 因此从1开始，2结束
        for (i in 1..2) {
            // 横线
            boardPath.moveTo(left + capSize, top + gridSize * i)
            boardPath.lineTo(left + width - capSize, top + gridSize * i)
            // 竖线
            boardPath.moveTo(left + gridSize * i, top + capSize)
            boardPath.lineTo(left + gridSize * i, top + height - capSize)
        }
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)
        canvas.drawPath(boardPath, paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }
}