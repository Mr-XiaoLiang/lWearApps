package com.lollipop.wear.widget

import android.graphics.Outline
import android.view.View
import kotlin.math.min

class CircularOutlineHelper : android.view.ViewOutlineProvider() {

    companion object {
        fun bind(view: View) {
            view.clipToOutline = true
            view.outlineProvider = CircularOutlineHelper()
        }
    }

    override fun getOutline(view: View?, outline: Outline?) {
        if (view != null && outline != null) {
            outline.setRoundRect(
                0,
                0,
                view.width,
                view.height,
                min(view.width, view.height) * 0.5F
            )
        }
    }

}