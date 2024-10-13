package com.lollipop.wear.widget

import android.content.Context
import android.graphics.Outline
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import kotlin.math.min

class CircularImageView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : AppCompatImageView(context, attributeSet) {

    init {
        CircularOutlineHelper.bind(this)
    }

}