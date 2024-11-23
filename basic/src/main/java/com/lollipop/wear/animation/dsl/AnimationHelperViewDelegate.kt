package com.lollipop.wear.animation.dsl

import android.view.View
import com.lollipop.wear.animation.AnimationHelper
import com.lollipop.wear.animation.ViewConverter
import com.lollipop.wear.animation.ViewOnEndListener
import com.lollipop.wear.animation.ViewOnStartListener

@AnimationHelperDsl
class AnimationHelperViewDelegate(private val helper: AnimationHelper, private val view: View) {

    fun addConverter(converter: ViewConverter) {
        converter.setView(view)
        helper.addConverter(converter)
    }

    fun addOnEndListener(listener: ViewOnEndListener) {
        listener.setView(view)
        helper.addOnEndListener(listener)
    }

    fun addOnStartListener(listener: ViewOnStartListener) {
        listener.setView(view)
        helper.addOnStartListener(listener)
    }

}