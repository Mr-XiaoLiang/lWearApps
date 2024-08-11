package com.lollipop.wear.widget

import android.content.Context
import android.graphics.Outline
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView

class CircularImageView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : AppCompatImageView(context, attributeSet) {

    init {
        clipToOutline = true
        outlineProvider = object : android.view.ViewOutlineProvider() {
            override fun getOutline(view: View?, outline: Outline?) {
                if (view != null && outline != null) {
                    outline.setOval(0, 0, view.width, view.height)
                }
            }
        }
    }

}