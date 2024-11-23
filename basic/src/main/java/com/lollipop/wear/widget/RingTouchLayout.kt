package com.lollipop.wear.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.graphics.PointF
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.FrameLayout
import com.lollipop.wear.basic.R
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

class RingTouchLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : FrameLayout(context, attributeSet) {

    private val restrictedRect = RestrictedRect()
    private val lastTouchLocation = PointF()

    init {
        attributeSet?.let { attr ->
            val typedArray = context.obtainStyledAttributes(
                attr, R.styleable.RingTouchLayout
            )
            val ringWidth = typedArray.getDimensionPixelSize(
                R.styleable.RingTouchLayout_ring_width,
                0
            )
            val ringMargin = typedArray.getDimensionPixelSize(
                R.styleable.RingTouchLayout_ring_margin,
                0
            )
            restrictedRect.setRingInfo(ringWidth, ringMargin)
            typedArray.recycle()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        restrictedRect.set(width, height)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        updateTouchLocation(event)
        if (!isInRing()) {
            if (event?.actionMasked == MotionEvent.ACTION_DOWN) {
                // 如果是点击就放弃掉
                return false
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        updateTouchLocation(ev)
        if (!isInRing()) {
            return true
        }
        return super.onInterceptTouchEvent(ev)
    }

    private fun updateTouchLocation(ev: MotionEvent?) {
        if (ev == null) {
            lastTouchLocation.set(0F, 0F)
            return
        }
        lastTouchLocation.set(ev.x, ev.y)
    }

    private fun isInRing(): Boolean {
        return restrictedRect.isInRing(lastTouchLocation.x.toInt(), lastTouchLocation.y.toInt())
    }

    override fun performClick(): Boolean {
        if (isInRing()) {
            return super.performClick()
        }
        return false
    }

    private class RestrictedRect {

        private val bounds = Rect()
        private var ringWidth = 0
        private var ringMargin = 0
        private val circleCenter = Point()
        private var outerRadius = 0
        private var innerRadius = 0

        fun setRingInfo(ringWidth: Int, ringMargin: Int) {
            this.ringWidth = ringWidth
            this.ringMargin = ringMargin
            updateCircleCenter()
        }

        fun set(width: Int, height: Int) {
            bounds.set(0, 0, width, height)
            updateCircleCenter()
        }

        private fun updateCircleCenter() {
            circleCenter.set(bounds.centerX(), bounds.centerY())
            outerRadius = min(bounds.width() / 2, bounds.height() / 2) - ringMargin
            innerRadius = outerRadius - ringWidth
        }

        fun isEmpty(): Boolean {
            return bounds.isEmpty || ringWidth <= 0
        }

        fun isInRing(x: Int, y: Int): Boolean {
            if (isEmpty()) {
                return false
            }
            val radius = getRadius(x, y)
            return radius >= innerRadius && radius <= outerRadius
        }

        private fun getRadius(x: Int, y: Int): Int {
            return calculateDistance(
                circleCenter.x.toDouble(),
                circleCenter.y.toDouble(),
                x.toDouble(),
                y.toDouble()
            ).toInt()

        }

        /**
         * 计算两个点之间的距离。
         *
         * @param x1 第一个点的 x 坐标
         * @param y1 第一个点的 y 坐标
         * @param x2 第二个点的 x 坐标
         * @param y2 第二个点的 y 坐标
         * @return 两点之间的距离
         */
        private fun calculateDistance(x1: Double, y1: Double, x2: Double, y2: Double): Double {
            return sqrt((x2 - x1).pow(2.0) + (y2 - y1).pow(2.0))
        }

    }

}