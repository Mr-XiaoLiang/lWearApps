package com.lollipop.wear.animation.start

import androidx.core.view.isVisible
import com.lollipop.wear.animation.ViewOnStartListener
import com.lollipop.wear.animation.dsl.AnimationHelperViewDelegate


class ShowOnStartListener : ViewOnStartListener() {
    override fun onStart(progress: Float) {
        view?.isVisible = true
    }
}

fun AnimationHelperViewDelegate.ShowOnStart() {
    addOnStartListener(ShowOnStartListener())
}

