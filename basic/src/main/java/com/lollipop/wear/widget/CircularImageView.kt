package com.lollipop.wear.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.lollipop.wear.basic.R

class CircularImageView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : AppCompatImageView(context, attributeSet) {

    init {
        CircularOutlineHelper.bind(
            this,
            attributeSet,
            R.styleable.CircularImageView,
            R.styleable.CircularImageView_circularMaxRadius
        )
    }

}