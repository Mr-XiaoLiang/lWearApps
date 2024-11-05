package com.lollipop.wear.widget

import android.content.Context
import android.util.AttributeSet
import com.lollipop.wear.basic.R

open class ArcLinearLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : ArcLayout(context, attributeSet) {

    var startAngle: Float = 0F
    var edgeMargin: Float = 0F
    var intervalAngle: Float = 10F
    var radius: Float = 0F
    var radiusMode: RadiusMode = RadiusMode.MinEdge

    init {
        attributeSet?.let { attr ->
            val typedArray = context.obtainStyledAttributes(
                attr, R.styleable.ArcLinearLayout
            )
            startAngle = typedArray.getFloat(
                R.styleable.ArcLinearLayout_arc_startAngle,
                0f
            )
            edgeMargin = typedArray.getDimension(
                R.styleable.ArcLinearLayout_arc_margin,
                0F
            )
            intervalAngle = typedArray.getFloat(
                R.styleable.ArcLinearLayout_arc_intervalAngle,
                10F
            )
            radius = typedArray.getDimension(
                R.styleable.ArcLinearLayout_arc_radius,
                0f
            )
            radiusMode = RadiusMode.MinEdge
            val mode = typedArray.getInt(
                R.styleable.ArcLinearLayout_arc_mode, RadiusMode.MinEdge.declare
            )
            RadiusMode.entries.forEach {
                if (it.declare == mode) {
                    radiusMode = it
                }
            }
            typedArray.recycle()
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val count = childCount
        val start = startAngle
        val interval = intervalAngle
        val r = radius
        val mode = radiusMode
        for (i in 0 until count) {
            val child = getChildAt(i)
            if (child.visibility == GONE) {
                continue
            }
            val lp = child.layoutParams as ArcLayoutParams
            lp.angle = interval * i + start
            lp.edgeMargin = edgeMargin
            if (r > 1) {
                lp.radius = r
            }
            lp.radiusMode = mode
        }
        super.onLayout(changed, left, top, right, bottom)
    }

}