package com.lollipop.wear.animation.dsl

import android.view.View
import com.lollipop.wear.animation.AnimationHelper

@AnimationHelperDsl
class AnimationHelperDslBuilder(val helper: AnimationHelper)

fun AnimationHelperDslBuilder.withView(
    view: View,
    block: AnimationHelperViewDelegate.() -> Unit
) {
    val viewDelegate = AnimationHelperViewDelegate(helper, view)
    block.invoke(viewDelegate)
}