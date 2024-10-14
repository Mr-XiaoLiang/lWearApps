package com.lollipop.wear.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.lollipop.wear.basic.R
import kotlin.math.max
import kotlin.math.min

class RectProgressView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : AppCompatImageView(context, attributeSet) {

    var orientation: Orientation = Orientation.HORIZONTAL
        set(value) {
            field = value
            invalidate()
        }

    var progress: Float = 0F
        set(value) {
            field = min(1F, max(0F, value))
            invalidate()
        }

    init {
        attributeSet?.let { attr ->
            val typedArray = context.obtainStyledAttributes(
                attr, R.styleable.RectProgressView
            )
            orientation = Orientation.findByCode(
                typedArray.getInt(
                    R.styleable.RectProgressView_rectMode,
                    Orientation.HORIZONTAL.code
                ),
                Orientation.HORIZONTAL
            )
            progress = typedArray.getFloat(R.styleable.RectProgressView_rectProgress, 0F)
            typedArray.recycle()
        }
    }

    override fun onDraw(canvas: Canvas) {
        clipCanvas(canvas) {
            super.onDraw(canvas)
        }
    }

    private fun clipCanvas(canvas: Canvas, callback: () -> Unit) {
        val saveCount = canvas.save()
        when (orientation) {
            Orientation.HORIZONTAL -> {
                canvas.clipRect(0F, 0F, width * progress, height.toFloat())
            }

            Orientation.VERTICAL -> {
                canvas.clipRect(0F, 0F, width.toFloat(), height * progress)
            }

            Orientation.HORIZONTAL_REVERSAL -> {
                canvas.clipRect(width * (1 - progress), 0F, width.toFloat(), height.toFloat())
            }

            Orientation.VERTICAL_REVERSAL -> {
                canvas.clipRect(0F, height * (1 - progress), width.toFloat(), height.toFloat())
            }
        }
        callback()
        canvas.restoreToCount(saveCount)
    }

    private class ProgressDrawable : Drawable() {


        private val paint = Paint().apply {
            isDither = true
            isAntiAlias = true
        }


        var color: Int
            get() {
                return paint.color
            }
            set(value) {
                paint.color = value
                invalidateSelf()
            }

        override fun draw(canvas: Canvas) {

        }

        override fun setAlpha(alpha: Int) {

        }

        override fun setColorFilter(colorFilter: ColorFilter?) {

        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSPARENT
        }

    }

    enum class Orientation(val code: Int) {
        HORIZONTAL(0),
        VERTICAL(1),
        HORIZONTAL_REVERSAL(2),
        VERTICAL_REVERSAL(3);

        companion object {
            fun findByCode(code: Int, def: Orientation): Orientation {
                return entries.find { it.code == code } ?: def
            }
        }

    }

}