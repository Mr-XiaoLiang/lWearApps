package com.lollipop.wear.animation.converter

import com.lollipop.wear.animation.AnimationHelper.Companion.PROGRESS_MAX
import com.lollipop.wear.animation.ViewConverter
import kotlin.math.max
import kotlin.math.min

abstract class BasicConverter : ViewConverter() {

    protected fun Float.weight(value: Float): Float {
        return this * value
    }

    protected fun Float.limit(min: Float, max: Float): Float {
        return min(max(this, min), max)
    }

    protected fun Float.invert(max: Float = PROGRESS_MAX): Float {
        return max - this
    }

}