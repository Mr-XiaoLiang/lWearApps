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
        CircularOutlineHelper.bind(this)
    }

}