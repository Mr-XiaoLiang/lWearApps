package com.lollipop.wear.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.lollipop.wear.basic.R
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

open class ArcLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : FrameLayout(context, attributeSet) {

    private val debugInfoList = mutableListOf<DebugInfo>()
    private val debugPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
    }

    private class DebugInfo(
        val childX: Int,
        val childY: Int,
        val centerX: Float,
        val centerY: Float
    )

    override fun addView(child: View?) {
        super.addView(child)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        layoutChildren(left, top, right, bottom)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val childParentWidth = widthSize - paddingLeft - paddingRight
        val childParentHeight = heightSize - paddingTop - paddingBottom

        val count = childCount
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility == GONE) {
                continue
            }
            val lp = child.layoutParams as ArcLayoutParams

            val childWidthMeasureSpec = lp.getWidthLayoutParam(childParentWidth, childParentHeight)
            val childHeightMeasureSpec =
                lp.getHeightLayoutParam(childParentWidth, childParentHeight)
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
        }

        setMeasuredDimension(widthSize, heightSize)
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (isInEditMode) {
            for (info in debugInfoList) {
                canvas.drawCircle(info.centerX, info.centerY, 10F, debugPaint)
                canvas.drawLine(
                    info.centerX,
                    info.centerY,
                    info.childX.toFloat(),
                    info.childY.toFloat(),
                    debugPaint
                )
            }
        }
    }

    private fun getParentRadius(
        mode: RadiusMode,
        parentWidth: Int,
        parentHeight: Int,
        childWidth: Int,
        childHeight: Int,
        fixEdge: Boolean
    ): Float {
        val offsetSize = if (fixEdge) {
            max(childWidth, childHeight) / 2F
        } else {
            0F
        }
        var radius = when (mode) {
            RadiusMode.MinEdge -> min(parentWidth, parentHeight) / 2F
            RadiusMode.MaxEdge -> max(parentWidth, parentHeight) / 2F
            RadiusMode.Height -> parentHeight / 2F
            RadiusMode.Width -> parentWidth / 2F
        }
        radius -= offsetSize
        return radius
    }

    private fun layoutChildren(left: Int, top: Int, right: Int, bottom: Int) {
        val count = childCount

        val parentLeft = paddingLeft
        val parentTop = paddingTop

        val parentWidth = right - left - paddingLeft - paddingRight
        val parentHeight = bottom - top - paddingTop - paddingBottom

        debugInfoList.clear()
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility == GONE) {
                continue
            }

            val lp = child.layoutParams as ArcLayoutParams

            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight

            val circleCenter = lp.getCircleCenter(parentWidth, parentHeight)
            val parentRadius = getParentRadius(
                lp.radiusMode,
                parentWidth,
                parentHeight,
                childWidth,
                childHeight,
                lp.fixEdge
            )
            val centerX = parentLeft + circleCenter[0]
            val centerY = parentTop + circleCenter[1]

            val angle = lp.angle

            val childRadius = lp.optRadius(parentRadius)

            val point = getPoint(
                radio = childRadius - lp.edgeMargin,
                angle = angle,
                centerX = centerX,
                centerY = centerY
            )
            val childX = point[0].toInt()
            val childY = point[1].toInt()

            val childLeft = childX - (childWidth / 2)
            val childTop = childY - (childHeight / 2)

            child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight)
            if (isInEditMode) {
                debugInfoList.add(DebugInfo(childX, childY, centerX, centerY))
            }
        }
    }

    private fun getPoint(radio: Float, angle: Float, centerX: Float, centerY: Float): FloatArray {
        // x1   =   x0   +   r   *   cos(a   *   PI   /180   )
        // y1   =   y0   +   r   *   sin(a   *   PI  /180   )
        return floatArrayOf(
            (centerX + radio * cos(angle * Math.PI / 180)).toFloat(),
            (centerY + radio * sin(angle * Math.PI / 180)).toFloat()
        )
    }

    class ArcLayoutParams : LayoutParams {

        var angle: Float = 0F
        var edgeMargin: Float = 0F
        var radius: Float = 0F
        var radiusPercent: Float = 1F
        var radiusMode: RadiusMode = RadiusMode.MinEdge
        var sizeMode: SizeMode = SizeMode.None
        var widthPercent: Float = 0F
        var heightPercent: Float = 0F
        var fixEdge: Boolean = false

        constructor(width: Int, height: Int) : super(width, height)
        constructor(source: ArcLayoutParams) : super(source) {
            angle = source.angle
            edgeMargin = source.edgeMargin
            radius = source.radius
            radiusMode = source.radiusMode
            sizeMode = source.sizeMode
            widthPercent = source.widthPercent
            heightPercent = source.heightPercent
            fixEdge = source.fixEdge
        }

        constructor(source: ViewGroup.LayoutParams) : super(source)
        constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
            val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.ArcLayout_Layout)
            angle = a.getFloat(R.styleable.ArcLayout_Layout_layout_arc_angle, 0F)
            edgeMargin = a.getDimension(R.styleable.ArcLayout_Layout_layout_arc_margin, 0F)
            radius = a.getDimension(R.styleable.ArcLayout_Layout_layout_arc_radius, 0F)
            radiusPercent = a.getFloat(R.styleable.ArcLayout_Layout_layout_arc_radiusPercent, 1F)
            widthPercent = a.getFloat(R.styleable.ArcLayout_Layout_layout_arc_widthPercent, 0F)
            heightPercent = a.getFloat(R.styleable.ArcLayout_Layout_layout_arc_heightPercent, 0F)
            fixEdge = a.getBoolean(R.styleable.ArcLayout_Layout_layout_arc_fixEdge, false)

            val radiusModeValue = a.getInt(
                R.styleable.ArcLayout_Layout_layout_arc_mode, RadiusMode.MinEdge.declare
            )
            radiusMode = RadiusMode.find(radiusModeValue) ?: RadiusMode.MinEdge
            sizeMode = SizeMode.None
            val sizeModeValue = a.getInt(
                R.styleable.ArcLayout_Layout_layout_arc_sizeMode, SizeMode.None.declare
            )
            sizeMode = SizeMode.find(sizeModeValue) ?: SizeMode.None

            a.recycle()
        }

        private fun makeMeasureSpec(size: Int, mode: Int): Int {
            return MeasureSpec.makeMeasureSpec(size, mode)
        }

        private fun getDefaultMeasureSpec(layoutSize: Int, parentSize: Int): Int {
            return when (layoutSize) {
                ViewGroup.LayoutParams.MATCH_PARENT -> {
                    makeMeasureSpec(parentSize, MeasureSpec.EXACTLY)
                }

                ViewGroup.LayoutParams.WRAP_CONTENT -> {
                    makeMeasureSpec(parentSize, MeasureSpec.AT_MOST)
                }

                else -> {
                    makeMeasureSpec(layoutSize, MeasureSpec.EXACTLY)
                }
            }
        }

        fun getWidthLayoutParam(parentWidth: Int, parentHeight: Int): Int {
            when (sizeMode) {
                SizeMode.None -> {
                    return getDefaultMeasureSpec(width, parentWidth)
                }

                SizeMode.MinEdge -> {
                    return makeMeasureSpec(
                        (min(parentWidth, parentHeight) * widthPercent).toInt(),
                        MeasureSpec.EXACTLY
                    )
                }

                SizeMode.MaxEdge -> {
                    return makeMeasureSpec(
                        (max(parentWidth, parentHeight) * widthPercent).toInt(),
                        MeasureSpec.EXACTLY
                    )
                }

                SizeMode.Height -> {
                    return makeMeasureSpec(
                        (parentHeight * widthPercent).toInt(),
                        MeasureSpec.EXACTLY
                    )
                }

                SizeMode.Width -> {
                    return makeMeasureSpec(
                        (parentWidth * widthPercent).toInt(),
                        MeasureSpec.EXACTLY
                    )
                }

                SizeMode.Weight -> {
                    return makeMeasureSpec(
                        (parentWidth * widthPercent).toInt(),
                        MeasureSpec.EXACTLY
                    )
                }
            }
        }

        fun getHeightLayoutParam(parentWidth: Int, parentHeight: Int): Int {
            when (sizeMode) {
                SizeMode.None -> {
                    return getDefaultMeasureSpec(height, parentHeight)
                }

                SizeMode.MinEdge -> {
                    return makeMeasureSpec(
                        (min(parentWidth, parentHeight) * heightPercent).toInt(),
                        MeasureSpec.EXACTLY
                    )
                }

                SizeMode.MaxEdge -> {
                    return makeMeasureSpec(
                        (max(parentWidth, parentHeight) * heightPercent).toInt(),
                        MeasureSpec.EXACTLY
                    )
                }

                SizeMode.Height -> {
                    return makeMeasureSpec(
                        (parentHeight * heightPercent).toInt(),
                        MeasureSpec.EXACTLY
                    )
                }

                SizeMode.Width -> {
                    return makeMeasureSpec(
                        (parentWidth * heightPercent).toInt(),
                        MeasureSpec.EXACTLY
                    )
                }

                SizeMode.Weight -> {
                    return makeMeasureSpec(
                        (parentHeight * heightPercent).toInt(),
                        MeasureSpec.EXACTLY
                    )
                }
            }
        }

        fun getCircleCenter(parentWidth: Int, parentHeight: Int): FloatArray {
            var childGravity = gravity
            if (childGravity == -1) {
                childGravity = Gravity.CENTER
            }
            val absoluteGravity = Gravity.getAbsoluteGravity(childGravity, layoutDirection)
            val x = when (absoluteGravity and Gravity.HORIZONTAL_GRAVITY_MASK) {
                Gravity.RIGHT -> {
                    parentWidth.toFloat()
                }

                Gravity.LEFT -> {
                    0F
                }

                else -> {
                    parentWidth * 0.5F
                }
            }

            val y = when (childGravity and Gravity.VERTICAL_GRAVITY_MASK) {
                Gravity.TOP -> {
                    0F
                }

                Gravity.BOTTOM -> {
                    parentHeight.toFloat()
                }

                else -> {
                    parentHeight * 0.5F
                }
            }
            return floatArrayOf(x, y)
        }

        fun optRadius(parentRadius: Float): Float {
            if (radius.toInt() > 0) {
                return radius
            }
            return parentRadius * radiusPercent
        }

    }

    override fun generateDefaultLayoutParams(): ArcLayoutParams {
        return ArcLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun generateLayoutParams(attrs: AttributeSet?): ArcLayoutParams {
        if (attrs == null) {
            return generateDefaultLayoutParams()
        }
        return ArcLayoutParams(context, attrs)
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams?): Boolean {
        return p is ArcLayoutParams
    }

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams?): ViewGroup.LayoutParams {
        if (lp == null) {
            return generateDefaultLayoutParams()
        }
        if (lp is ArcLayoutParams) {
            return ArcLayoutParams(lp)
        }
        return ArcLayoutParams(lp)
    }

    enum class RadiusMode(val declare: Int) {
        MinEdge(0),
        MaxEdge(1),
        Height(2),
        Width(3);

        companion object {
            fun find(value: Int): RadiusMode? {
                return entries.find { it.declare == value }
            }
        }

    }

    enum class SizeMode(val declare: Int) {
        None(-1),
        MinEdge(0),
        MaxEdge(1),
        Height(2),
        Width(3),
        Weight(4);

        companion object {
            fun find(value: Int): SizeMode? {
                return entries.find { it.declare == value }
            }
        }
    }

}