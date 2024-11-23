package com.lollipop.wear.animation

import android.view.View


fun interface AnimationConverter {
    fun onUpdate(progress: Float)
}

abstract class ViewConverter : AnimationConverter {

    protected var view: View? = null
        private set

    fun setView(view: View) {
        this.view = view
    }

}

fun interface OnAnimationEndListener {
    fun onEnd(progress: Float)
}

fun interface OnAnimationStartListener {
    fun onStart(progress: Float)
}

abstract class ViewOnEndListener : OnAnimationEndListener {

    protected var view: View? = null
        private set

    fun setView(view: View) {
        this.view = view
    }

}

abstract class ViewOnStartListener : OnAnimationStartListener {

    protected var view: View? = null
        private set

    fun setView(view: View) {
        this.view = view
    }

}