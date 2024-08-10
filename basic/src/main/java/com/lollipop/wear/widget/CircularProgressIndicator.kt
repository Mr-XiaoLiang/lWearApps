package com.lollipop.wear.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.lollipop.wear.basic.R
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

        private val activePath = Path()
        private val inactivePath = Path()

        private val padding = Rect()
        private val boundsF = RectF()

        var progress = 0f
            set(value) {
                field = max(0F, min(1F, value))
                updatePath()
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
                updatePath()
            }

        var sweepAngle = 360F
            set(value) {
                field = value
                updatePath()
            }

        var interval = 1F
            set(value) {
                field = value
                updatePath()
            }

        var direction = Direction.Clockwise
            set(value) {
                field = value
                updatePath()
            }

        var strokeWidth: Float
            get() {
                return activePaint.strokeWidth
            }
            set(value) {
                activePaint.strokeWidth = value
                inactivePaint.strokeWidth = value
                updatePath()
            }

//        fun setPadding(padding: Int) {
//            setPadding(padding, padding, padding, padding)
//        }
//
//        fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
//            padding.set(left, top, right, bottom)
//            updatePath()
//        }

        override fun onBoundsChange(bounds: Rect) {
            super.onBoundsChange(bounds)
//            boundsF.set(
//                (bounds.left + padding.left).toFloat(),
//                (bounds.top + padding.top).toFloat(),
//                (bounds.right - padding.right).toFloat(),
//                (bounds.bottom - padding.bottom).toFloat()
//            )
            boundsF.set(bounds)
            updatePath()
        }

        private fun updatePath() {
            activePath.reset()
            inactivePath.reset()
            if (bounds.isEmpty) {
                return
            }
            val radius = min(boundsF.width(), boundsF.height()) / 2
            val intervalAngle = arcDimenToAngle(radius, interval + strokeWidth)
            var activeSweep = sweepAngle * progress
            if ((360 - activeSweep) < intervalAngle) {
                activeSweep = 360 - intervalAngle
            }
            val activeEnable = progress >= 0.009
            val inactiveEnable = progress <= 0.991
            when (direction) {
                Direction.Clockwise -> {
                    if (activeEnable) {
                        activePath.addArc(boundsF, startAngle, activeSweep)
                    }
                    var inactiveSweep = sweepAngle - activeSweep
                    if ((360 - inactiveSweep) < intervalAngle) {
                        inactiveSweep = 360 - intervalAngle
                    }
                    var inactiveStart = startAngle + activeSweep
                    if (activeEnable) {
                        // 如果上面还有，那就再减一次，因为有2个断口
                        inactiveStart += intervalAngle
                    }
                    if (inactiveEnable) {
                        if ((360 - sweepAngle) < intervalAngle && activeEnable) {
                            inactiveSweep -= intervalAngle
                            inactiveSweep -= intervalAngle
                        }
                        inactivePath.addArc(boundsF, inactiveStart, inactiveSweep)
                    }
                }

                Direction.AntiClockwise -> {
                    if (activeEnable) {
                        val activeStart = startAngle + sweepAngle - activeSweep
                        activePath.addArc(boundsF, activeStart, activeSweep)
                    }
                    var inactiveSweep = sweepAngle - activeSweep
                    if ((360 - inactiveSweep) < intervalAngle) {
                        inactiveSweep = 360 - intervalAngle
                    }
                    if (activeEnable) {
                        inactiveSweep -= intervalAngle
                    }
                    var inactiveStart = startAngle
                    if (inactiveEnable) {
                        if ((360 - sweepAngle) < intervalAngle && activeEnable) {
                            inactiveStart += intervalAngle
                            inactiveSweep -= intervalAngle
                        }
                        inactivePath.addArc(boundsF, inactiveStart, inactiveSweep)
                    }
                }
            }
            invalidateSelf()
        }

        private fun arcDimenToAngle(radius: Float, arcDimen: Float): Float {
            // 角度计算长度：2 * PI * radius * (angle / 360)
            // 反过来，通过长度尺寸换算角度
            return (arcDimen / (2 * Math.PI * radius) * 360).toFloat()
        }

        override fun draw(canvas: Canvas) {
            if (!activePath.isEmpty) {
                canvas.drawPath(activePath, activePaint)
            }
            if (!inactivePath.isEmpty) {
                canvas.drawPath(inactivePath, inactivePaint)
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
        Clockwise,
        AntiClockwise
    }

}

