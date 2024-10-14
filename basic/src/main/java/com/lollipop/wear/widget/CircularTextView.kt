package com.lollipop.wear.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import com.lollipop.wear.basic.R

class CircularTextView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : AppCompatTextView(context, attributeSet) {

    init {
        CircularOutlineHelper.bind(
            this,
            attributeSet,
            R.styleable.CircularTextView,
            R.styleable.CircularTextView_circularMaxRadius
        )
    }

}