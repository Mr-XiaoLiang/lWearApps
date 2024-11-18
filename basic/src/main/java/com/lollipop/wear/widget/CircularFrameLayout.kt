package com.lollipop.wear.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

class CircularFrameLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : FrameLayout(context, attributeSet) {

    init {
        CircularOutlineHelper.bind(
            this,
            CircularOutlineHelper.NONE_MAX_RADIUS
        )
    }

}