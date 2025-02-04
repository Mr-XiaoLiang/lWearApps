package com.lollipop.wear.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout

class CircularOutlineLinearGroup @JvmOverloads constructor(
    context: Context, attr: AttributeSet? = null
) : LinearLayout(context, attr) {

    private var maxRadius = CircularOutlineHelper.NONE_MAX_RADIUS
    private var gapRadius = CircularOutlineHelper.NONE_MAX_RADIUS

    private val tempViewList = ArrayList<View>()

    init {
        // 发生了布局变化，重新计算
        updateOutline()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        // 发生了布局变化，重新计算
        updateOutline()
    }

    private fun updateOutline() {
        val viewList = findVisibleChild()
        // 没有可见的子View
        if (viewList.isEmpty()) {
            return
        }
        // 只有一个可见的子View，那么就直接设置为外框形状
        if (viewList.size == 1) {
            val view = viewList[0]
            CircularOutlineHelper.bind(
                view,
                CircularOutlineHelper.RadiusMode.LimitMax(maxRadius),
                CircularOutlineHelper.RadiusMode.LimitMax(maxRadius),
                CircularOutlineHelper.RadiusMode.LimitMax(maxRadius),
                CircularOutlineHelper.RadiusMode.LimitMax(maxRadius)
            )
            return
        }
        if (orientation == HORIZONTAL) {
            updateOutlineByHorizontal(viewList)
        } else {
            updateOutlineByVertical(viewList)
        }
    }

    private fun findVisibleChild(): List<View> {
        tempViewList.clear()
        for (index in 0 until childCount) {
            val child = getChildAt(index)
            if (child.visibility == View.VISIBLE) {
                tempViewList.add(child)
            }
        }
        if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            tempViewList.reverse()
        }
        return tempViewList
    }

    private fun updateOutlineByHorizontal(viewList: List<View>) {
        for (index in viewList.indices) {
            val view = viewList[index]
            val leftTop: CircularOutlineHelper.RadiusMode.LimitMax
            val rightTop: CircularOutlineHelper.RadiusMode.LimitMax
            val rightBottom: CircularOutlineHelper.RadiusMode.LimitMax
            val leftBottom: CircularOutlineHelper.RadiusMode.LimitMax
            if (index == 0) {
                leftTop = CircularOutlineHelper.RadiusMode.LimitMax(maxRadius)
                leftBottom = CircularOutlineHelper.RadiusMode.LimitMax(maxRadius)
            } else {
                leftTop = CircularOutlineHelper.RadiusMode.LimitMax(gapRadius)
                leftBottom = CircularOutlineHelper.RadiusMode.LimitMax(gapRadius)
            }
            if (index == viewList.size - 1) {
                rightTop = CircularOutlineHelper.RadiusMode.LimitMax(maxRadius)
                rightBottom = CircularOutlineHelper.RadiusMode.LimitMax(maxRadius)
            } else {
                rightTop = CircularOutlineHelper.RadiusMode.LimitMax(gapRadius)
                rightBottom = CircularOutlineHelper.RadiusMode.LimitMax(gapRadius)
            }
            CircularOutlineHelper.bind(
                view,
                leftTop,
                rightTop,
                rightBottom,
                leftBottom
            )
        }
    }

    private fun updateOutlineByVertical(viewList: List<View>) {
        for (index in viewList.indices) {
            val view = viewList[index]
            val leftTop: CircularOutlineHelper.RadiusMode.LimitMax
            val rightTop: CircularOutlineHelper.RadiusMode.LimitMax
            val rightBottom: CircularOutlineHelper.RadiusMode.LimitMax
            val leftBottom: CircularOutlineHelper.RadiusMode.LimitMax
            if (index == 0) {
                leftTop = CircularOutlineHelper.RadiusMode.LimitMax(maxRadius)
                rightTop = CircularOutlineHelper.RadiusMode.LimitMax(maxRadius)
            } else {
                leftTop = CircularOutlineHelper.RadiusMode.LimitMax(gapRadius)
                rightTop = CircularOutlineHelper.RadiusMode.LimitMax(gapRadius)
            }
            if (index == viewList.size - 1) {
                leftBottom = CircularOutlineHelper.RadiusMode.LimitMax(maxRadius)
                rightBottom = CircularOutlineHelper.RadiusMode.LimitMax(maxRadius)
            } else {
                leftBottom = CircularOutlineHelper.RadiusMode.LimitMax(gapRadius)
                rightBottom = CircularOutlineHelper.RadiusMode.LimitMax(gapRadius)
            }
            CircularOutlineHelper.bind(
                view,
                leftTop,
                rightTop,
                rightBottom,
                leftBottom
            )
        }
    }

}