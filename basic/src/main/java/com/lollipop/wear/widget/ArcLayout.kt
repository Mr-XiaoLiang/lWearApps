package com.lollipop.wear.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import com.lollipop.wear.basic.R
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class ArcLayout @JvmOverloads constructor(
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

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        layoutChildren(left, top, right, bottom)
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

    private fun layoutChildren(left: Int, top: Int, right: Int, bottom: Int) {
        val count = childCount

        val parentLeft = paddingLeft
        val parentTop = paddingTop

        val parentWidth = right - left - paddingLeft - paddingRight
        val parentHeight = bottom - top - paddingTop - paddingBottom

        val parentRadius = min(parentWidth, parentHeight) / 2F
        val centerX = parentLeft + parentRadius
        val centerY = parentTop + parentRadius
        debugInfoList.clear()
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility == GONE) {
                continue
            }
            val lp = child.layoutParams as ArcLayoutParams

            val childWidth = child.measuredWidth
            val childHeight = child.measuredHeight

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

        constructor(width: Int, height: Int) : super(width, height)
        constructor(source: ArcLayoutParams) : super(source) {
            angle = source.angle
            edgeMargin = source.edgeMargin
        }

        constructor(source: ViewGroup.LayoutParams) : super(source)
        constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
            val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.ArcLayout_Layout)
            angle = a.getFloat(R.styleable.ArcLayout_Layout_layout_arc_angle, 0F)
            edgeMargin = a.getDimension(R.styleable.ArcLayout_Layout_layout_arc_margin, 0F)
            radius = a.getDimension(R.styleable.ArcLayout_Layout_layout_arc_radius, 0F)
            a.recycle()
        }

        fun optRadius(def: Float): Float {
            if (radius.toInt() > 0) {
                return radius
            }
            return def
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

}