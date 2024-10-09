package com.lollipop.wear.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.lollipop.wear.basic.R
import com.lollipop.wear.devices.DeviceHelper
import kotlin.math.max
import kotlin.math.min

class CircularProgressIndicator @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : AppCompatImageView(context, attributeSet) {

    private val progressDrawable = CircularProgressDrawable()

    var progress: Float
        set(value) {
            progressDrawable.progress = value
        }
        get() {
            return progressDrawable.progress
        }

    var activeColor: Int
        set(value) {
            progressDrawable.activeColor = value
        }
        get() {
            return progressDrawable.activeColor
        }

    var inactiveColor: Int
        set(value) {
            progressDrawable.inactiveColor = value
        }
        get() {
            return progressDrawable.inactiveColor
        }

    var startAngle: Float
        set(value) {
            progressDrawable.startAngle = value
        }
        get() {
            return progressDrawable.startAngle
        }

    var sweepAngle: Float
        set(value) {
            progressDrawable.sweepAngle = value
        }
        get() {
            return progressDrawable.sweepAngle
        }

    var interval: Float
        set(value) {
            progressDrawable.interval = value
        }
        get() {
            return progressDrawable.interval
        }

    var direction: Direction
        set(value) {
            progressDrawable.direction = value
        }
        get() {
            return progressDrawable.direction
        }

    var strokeWidth: Float
        set(value) {
            progressDrawable.strokeWidth = value
        }
        get() {
            return progressDrawable.strokeWidth
        }

    init {
        setImageDrawable(progressDrawable)
        attributeSet?.let { attr ->
            val typedArray = context.obtainStyledAttributes(
                attr, R.styleable.CircularProgressIndicator
            )
            progress = typedArray.getFloat(
                R.styleable.CircularProgressIndicator_indicatorProgress,
                0f
            )
            activeColor = typedArray.getColor(
                R.styleable.CircularProgressIndicator_activeColor,
                Color.WHITE
            )
            inactiveColor = typedArray.getColor(
                R.styleable.CircularProgressIndicator_inactiveColor,
                Color.GRAY
            )
            startAngle = typedArray.getFloat(
                R.styleable.CircularProgressIndicator_startAngle,
                0f
            )
            sweepAngle = typedArray.getFloat(
                R.styleable.CircularProgressIndicator_sweepAngle,
                360f
            )
            interval = typedArray.getDimensionPixelSize(
                R.styleable.CircularProgressIndicator_interval,
                0
            ).toFloat()
            strokeWidth = typedArray.getDimensionPixelSize(
                R.styleable.CircularProgressIndicator_strokeWidth,
                4
            ).toFloat()
            val isAntiClockwise = typedArray.getBoolean(
                R.styleable.CircularProgressIndicator_antiClockwise,
                false
            )
            direction = if (isAntiClockwise) {
                Direction.AntiClockwise
            } else {
                Direction.Clockwise
            }
            typedArray.recycle()
        }
    }

    fun setActiveColorResource(resId: Int) {
        activeColor = ContextCompat.getColor(context, resId)
    }

    class CircularProgressDrawable : Drawable() {

        private val activePaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            isDither = true
            strokeCap = Paint.Cap.ROUND
        }

        private val inactivePaint = Paint().apply {
            isAntiAlias = true
            style = Paint.Style.STROKE
            isDither = true
            strokeCap = Paint.Cap.ROUND
        }

        private val boundsF = RectF()

        var progress = 0f
            set(value) {
                field = max(0F, min(1F, value))
                invalidateSelf()
            }

        var activeColor: Int
            set(value) {
                activePaint.color = value
                invalidateSelf()
            }
            get() {
                return activePaint.color
            }

        var inactiveColor: Int
            set(value) {
                inactivePaint.color = value
                invalidateSelf()
            }
            get() {
                return inactivePaint.color
            }

        var startAngle = 0F
            set(value) {
                field = value
                invalidateSelf()
            }

        var sweepAngle = 360F
            set(value) {
                field = value
                invalidateSelf()
            }

        var interval = 1F
            set(value) {
                field = value
                invalidateSelf()
            }

        var direction = Direction.Clockwise
            set(value) {
                field = value
                invalidateSelf()
            }

        var strokeWidth: Float
            get() {
                return activePaint.strokeWidth
            }
            set(value) {
                activePaint.strokeWidth = value
                inactivePaint.strokeWidth = value
                invalidateSelf()
            }

        override fun onBoundsChange(bounds: Rect) {
            super.onBoundsChange(bounds)
            boundsF.set(bounds)
            invalidateSelf()
        }

        private fun arcDimenToAngle(radius: Float, arcDimen: Float): Float {
            return DeviceHelper.arcDimenToAngle(radius, arcDimen)
        }

        override fun draw(canvas: Canvas) {
            if (bounds.isEmpty) {
                return
            }
            val radius = min(boundsF.width(), boundsF.height()) / 2
            val intervalAngle = arcDimenToAngle(radius, interval)
            val capAngle = arcDimenToAngle(radius, strokeWidth / 2)

            var beforeSweep: Float
            val beforePaint: Paint
            val afterPaint: Paint
            val arcStart: Float

            val endSpace = 360F - sweepAngle
            val maxSweep: Float = if (endSpace < intervalAngle) {
                sweepAngle - intervalAngle + endSpace
            } else {
                sweepAngle
            }

            when (direction) {
                Direction.Clockwise -> {
                    beforeSweep = maxSweep * progress
                    if (maxSweep - beforeSweep < intervalAngle) {
                        beforeSweep = maxSweep
                    } else if (beforeSweep < intervalAngle) {
                        beforeSweep = 0F
                    }
                    beforePaint = activePaint
                    afterPaint = inactivePaint
                    arcStart = startAngle
                }

                Direction.AntiClockwise -> {
                    var afterSweep = maxSweep * progress
                    if (maxSweep - afterSweep < intervalAngle) {
                        afterSweep = maxSweep
                    } else if (afterSweep < intervalAngle) {
                        afterSweep = 0F
                    }
                    beforeSweep = maxSweep - afterSweep
                    if (afterSweep >= 1) {
                        beforeSweep -= intervalAngle
                    }
                    beforePaint = inactivePaint
                    afterPaint = activePaint
                    val offset = sweepAngle - maxSweep
                    arcStart = (startAngle - sweepAngle) + offset
                }
            }

            beforeSweep -= (capAngle * 2)
            val beforeEnable = beforeSweep >= 1
            if (beforeEnable) {
                canvas.drawArc(boundsF, arcStart + capAngle, beforeSweep, false, beforePaint)
            }
            var afterOffset = 0F
            if (beforeEnable) {
                // 上一个的圆角
                afterOffset += (capAngle * 2)
                // 上一个的实际扇形
                afterOffset += beforeSweep
                // 两个之间的间距
                afterOffset += intervalAngle
            }
            val afterSweep = maxSweep - afterOffset - (capAngle * 2)
            if (afterSweep >= 1) {
                val afterStart = arcStart + afterOffset + capAngle
                canvas.drawArc(boundsF, afterStart, afterSweep, false, afterPaint)
            }
        }

        override fun setAlpha(alpha: Int) {
            activePaint.alpha = alpha
            inactivePaint.alpha = alpha
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            activePaint.colorFilter = colorFilter
            inactivePaint.colorFilter = colorFilter
        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSPARENT
        }

    }

    enum class Direction {
        /**
         * 顺时针
         */
        Clockwise,

        /**
         * 逆时针
         */
        AntiClockwise
    }

}

