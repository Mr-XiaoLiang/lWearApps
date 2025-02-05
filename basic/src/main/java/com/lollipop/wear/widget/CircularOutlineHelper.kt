package com.lollipop.wear.widget

import android.graphics.Outline
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

class CircularOutlineHelper(
    private val maxRadius: Int = NONE_MAX_RADIUS
) : android.view.ViewOutlineProvider() {

    companion object {

        const val NONE_MAX_RADIUS = 0

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
            view.outlineProvider = CircularOutlineHelper(maxRadius)
        }
    }

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