package com.lollipop.wear.widget

import android.content.Context
import android.graphics.Outline
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import kotlin.math.min

class CircularTextView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : AppCompatTextView(context, attributeSet) {

    init {
        clipToOutline = true
        outlineProvider = object : android.view.ViewOutlineProvider() {
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
    }

}