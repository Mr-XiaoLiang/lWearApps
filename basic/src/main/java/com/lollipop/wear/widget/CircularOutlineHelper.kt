package com.lollipop.wear.widget

import android.graphics.Outline
import android.graphics.Path
import android.os.Build
import android.util.AttributeSet
import android.view.View
import kotlin.math.max
import kotlin.math.min

class CircularOutlineHelper(
    private val leftTop: RadiusMode = DEFAULT_RADIUS,
    private val rightTop: RadiusMode = DEFAULT_RADIUS,
    private val rightBottom: RadiusMode = DEFAULT_RADIUS,
    private val leftBottom: RadiusMode = DEFAULT_RADIUS,
) : android.view.ViewOutlineProvider() {

    companion object {

        val DEFAULT_RADIUS = RadiusMode.EdgeMin(0.5F)

        const val NONE_MAX_RADIUS = -1

        fun bind(
            view: View,
            attributeSet: AttributeSet? = null,
            styleableId: IntArray,
            styleableItem: Int
        ) {
            var maxRadius = NONE_MAX_RADIUS
            attributeSet?.let { attr ->
                val typedArray = view.context.obtainStyledAttributes(
                    attr, styleableId
                )
                maxRadius = typedArray.getDimensionPixelSize(
                    styleableItem,
                    NONE_MAX_RADIUS
                )
                typedArray.recycle()
            }
            bind(view, maxRadius)
        }

        fun bind(view: View, maxRadius: Int = NONE_MAX_RADIUS) {
            view.clipToOutline = true
            view.outlineProvider = object : android.view.ViewOutlineProvider() {
                override fun getOutline(view: View?, outline: Outline?) {
                    if (view != null && outline != null) {
                        val radius = if (maxRadius == NONE_MAX_RADIUS) {
                            min(view.width, view.height) * 0.5F
                        } else {
                            min(min(view.width, view.height) * 0.5F, maxRadius.toFloat())
                        }
                        outline.setRoundRect(
                            0,
                            0,
                            view.width,
                            view.height,
                            radius
                        )
                    }
                }
            }
        }

        fun bind(
            view: View,
            leftTop: RadiusMode = DEFAULT_RADIUS,
            rightTop: RadiusMode = DEFAULT_RADIUS,
            rightBottom: RadiusMode = DEFAULT_RADIUS,
            leftBottom: RadiusMode = DEFAULT_RADIUS,
        ) {
            view.clipToOutline = true
            view.outlineProvider = CircularOutlineHelper(
                leftTop,
                rightTop,
                rightBottom,
                leftBottom,
            )
        }

    }

    override fun getOutline(view: View?, outline: Outline?) {
        if (view != null && outline != null) {
            setOutlinePath(view, outline)
        }
    }

    private fun setOutlinePath(view: View, outline: Outline) {
        val path = Path()
        // 左上角的圆角开始计算
        var ltx = leftTop.getRadius(view)
        var lty = ltx
        var rtx = rightTop.getRadius(view)
        var rty = rtx
        var rbx = rightBottom.getRadius(view)
        var rby = rbx
        var lbx = leftBottom.getRadius(view)
        var lby = lbx
        val viewWidth = view.width.toFloat()
        val viewHeight = view.height.toFloat()
        // 左侧的Y轴相加是否大于View的高度，那么我们按权重计算
        if ((lty + lby) > viewHeight) {
            val ltw = lty / (lty + lby)
            lty = viewHeight * ltw
            lby = viewHeight - lty
        }
        // 右侧的Y轴相加是否大于View的高度，那么我们按权重计算
        if ((rty + rby) > viewHeight) {
            val rtw = rty / (rty + rby)
            rty = viewHeight * rtw
            rby = viewHeight - rty
        }
        // 顶部的X轴相加是否大于View的宽度，那么我们按权重计算
        if ((ltx + rtx) > viewWidth) {
            val ltw = ltx / (ltx + rtx)
            ltx = viewWidth * ltw
            rtx = viewWidth - ltx
        }
        // 底部的X轴相加是否大于View的宽度，那么我们按权重计算
        if ((lbx + rbx) > viewWidth) {
            val ltw = lbx / (lbx + rbx)
            lbx = viewWidth * ltw
            rbx = viewWidth - lbx
        }

        path.moveTo(0F, lty)
        path.cubicTo(0F, lty * 0.5F, ltx * 0.5F, 0F, ltx, 0F)
        path.lineTo(viewWidth - rtx, 0F)
        path.cubicTo(
            viewWidth - (rtx * 0.5F),
            0F,
            viewWidth,
            rty * 0.5F,
            viewWidth,
            rty
        )
        path.lineTo(viewWidth, viewHeight - rby)
        path.cubicTo(
            viewWidth,
            viewHeight - (rby * 0.5F),
            viewWidth - (rbx * 0.5F),
            viewHeight,
            viewWidth - rbx,
            viewHeight
        )
        path.lineTo(lbx, viewHeight)
        path.cubicTo(
            lbx * 0.5F,
            viewHeight,
            0F,
            viewHeight - (lby * 0.5F),
            0F,
            viewHeight - lby
        )
        path.close()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            outline.setPath(path)
        } else {
            outline.setConvexPath(path)
        }
    }

    sealed class RadiusMode {

        abstract fun getRadius(view: View): Float

        class LimitMax(private val maxValue: Int) : RadiusMode() {
            override fun getRadius(view: View): Float {
                var value = min(view.width, view.height) * 0.5F
                if (maxValue != NONE_MAX_RADIUS && value > maxValue) {
                    value = maxValue.toFloat()
                }
                return value
            }
        }

        class EdgeMin(private val weight: Float) : RadiusMode() {
            override fun getRadius(view: View): Float {
                return min(view.width, view.height) * weight
            }
        }

        class EdgeMax(private val weight: Float) : RadiusMode() {
            override fun getRadius(view: View): Float {
                return max(view.width, view.height) * weight
            }
        }

        class Height(private val weight: Float) : RadiusMode() {
            override fun getRadius(view: View): Float {
                return view.height * weight
            }
        }

        class Width(private val weight: Float) : RadiusMode() {
            override fun getRadius(view: View): Float {
                return view.width * weight
            }
        }

        class Absolute(val value: Int) : RadiusMode() {
            override fun getRadius(view: View): Float {
                return value.toFloat()
            }
        }

    }

}