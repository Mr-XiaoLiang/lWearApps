package com.lollipop.wear.ttt.view

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
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

    private val padding = Rect()
    private var insets = RectF()

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

    fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        padding.set(left, top, right, bottom)
        buildPath()
    }

    fun setInsets(left: Float, top: Float, right: Float, bottom: Float) {
        insets.set(left, top, right, bottom)
        buildPath()
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        buildPath()
    }

    private fun buildPath() {
        boardPath.reset()
        if (bounds.isEmpty) {
            return
        }
        val capSize = strokeWidth / 2
        val width = bounds.width().toFloat() - padding.left - padding.right
        val height = bounds.height().toFloat() - padding.top - padding.bottom
        val left = bounds.left.toFloat() + padding.left
        val top = bounds.top.toFloat() + padding.top
        val size = min(width, height)
        val gridSize = size / 3

        val leftInsets = insets.left * gridSize
        val topInsets = insets.top * gridSize
        val rightInsets = insets.right * gridSize
        val bottomInsets = insets.bottom * gridSize

        // 格子只需要画中间2条线，总计4条线就行了。
        // 因此从1开始，2结束
        for (i in 1..2) {
            // 横线
            boardPath.moveTo(left + capSize + leftInsets, top + gridSize * i)
            boardPath.lineTo(left + width - capSize - rightInsets, top + gridSize * i)
            // 竖线
            boardPath.moveTo(left + gridSize * i, top + capSize + topInsets)
            boardPath.lineTo(left + gridSize * i, top + height - capSize - bottomInsets)
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