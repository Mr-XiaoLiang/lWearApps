package com.lollipop.sound

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.max
import kotlin.math.min

class VolumeSpectrumView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : AppCompatImageView(context, attributeSet) {

    private val volumeDrawable = VolumeDrawable()

    init {
        setImageDrawable(volumeDrawable)
    }

    fun push(value: Float) {
        post {
            volumeDrawable.addNode(max(0F, min(1F, value)))
        }
    }

    fun setLineInfo(lineWidth: Float, interval: Float) {
        volumeDrawable.setLineInfo(lineWidth, interval)
    }

    fun setColor(color: Int) {
        volumeDrawable.setColor(color)
    }

    fun setShader(shader: Shader?) {
        volumeDrawable.setShader(shader)
    }

    private class VolumeDrawable : Drawable() {

        private val paint = Paint().apply {
            isAntiAlias = true
            isDither = true
            strokeCap = Paint.Cap.ROUND
        }
        private var lineWidth: Float = 1F
        private var interval: Float = 1F
        private var maxCount = 0
        private var volumeNode: VolumeNode? = null

        fun addNode(value: Float) {
            val node = VolumeNode(value)
            node.next = volumeNode
            volumeNode = node
            invalidateSelf()
        }

        fun setLineInfo(lineWidth: Float, interval: Float) {
            this.lineWidth = lineWidth
            this.interval = interval
            buildParams()
        }

        fun setColor(color: Int) {
            paint.color = color
        }

        fun setShader(shader: Shader?) {
            paint.shader = shader
        }

        override fun onBoundsChange(bounds: Rect) {
            super.onBoundsChange(bounds)
            buildParams()
        }

        private fun buildParams() {
            if (bounds.isEmpty) {
                maxCount = 0
            } else {
                maxCount = (bounds.width() / (lineWidth + interval)).toInt()
                maxCount += 1
            }
            paint.strokeWidth = lineWidth
            invalidateSelf()
        }

        private fun traverse(maxCount: Int, callback: (VolumeNode) -> Unit) {
            var index = 0
            var node = volumeNode
            while (node != null) {
                callback(node)
                if (index >= maxCount) {
                    // 贯穿并且断开
                    node.next = null
                    break
                } else {
                    node = node.next
                }
                index++
            }
        }

        override fun draw(canvas: Canvas) {
            if (maxCount < 1) {
                return
            }
            val top = bounds.top.toFloat()
            val height = bounds.height()
            val capOffset = lineWidth * 0.5F
            var x = bounds.right - capOffset
            val step = lineWidth + interval
            traverse(maxCount) { node ->
                val value = node.value
                val h = height * value
                val t = top + capOffset + ((height - h) * 0.5F)
                canvas.drawLine(x, t, x, t + h, paint)
                x -= step
            }
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

    private class VolumeNode(
        val value: Float
    ) {
        var next: VolumeNode? = null
    }

}