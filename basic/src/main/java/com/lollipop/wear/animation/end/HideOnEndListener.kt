package com.lollipop.wear.animation.end

import androidx.core.view.isInvisible
import com.lollipop.wear.animation.AnimationHelper.Companion.PROGRESS_MIN
import com.lollipop.wear.animation.AnimationHelper.Companion.similar
import com.lollipop.wear.animation.ViewOnEndListener
import com.lollipop.wear.animation.dsl.AnimationHelperViewDelegate

class HideOnCloseListener : ViewOnEndListener() {
    override fun onEnd(progress: Float) {
        if (similar(progress, PROGRESS_MIN)) {
            view?.isInvisible = true
        }
    }
}

fun AnimationHelperViewDelegate.HideOnClose() {
    addOnEndListener(HideOnCloseListener())
}
