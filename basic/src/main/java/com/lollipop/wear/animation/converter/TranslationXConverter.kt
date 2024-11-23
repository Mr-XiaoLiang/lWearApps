package com.lollipop.wear.animation.converter

import com.lollipop.wear.animation.AnimationHelper.Companion.PROGRESS_MAX
import com.lollipop.wear.animation.AnimationHelper.Companion.PROGRESS_MIN
import com.lollipop.wear.animation.dsl.AnimationHelperViewDelegate

class TranslationXConverter(
    private val weight: Float,
    private val invert: Boolean
) : BasicConverter() {
    override fun onUpdate(progress: Float) {
        view?.let {
            var p = progress
            if (invert) {
                p = p.invert()
            }
            p = p.weight(weight).limit(PROGRESS_MIN, PROGRESS_MAX)
            it.translationX = it.width * p
        }
    }
}

fun AnimationHelperViewDelegate.TranslationX(weight: Float = 1F, invert: Boolean = true) {
    addConverter(TranslationXConverter(weight, invert))
}
