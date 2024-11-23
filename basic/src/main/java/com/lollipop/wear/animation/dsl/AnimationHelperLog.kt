package com.lollipop.wear.animation.dsl

import android.content.Context
import android.content.pm.ApplicationInfo
import android.util.Log
import com.lollipop.wear.animation.AnimationConverter
import com.lollipop.wear.animation.OnAnimationEndListener
import com.lollipop.wear.animation.OnAnimationStartListener

class AnimationHelperLog(
    private val tag: String
) : AnimationConverter, OnAnimationStartListener, OnAnimationEndListener {
    override fun onUpdate(progress: Float) {
        Log.d(tag, "onUpdate: $progress")
    }

    override fun onStart(progress: Float) {
        Log.d(tag, "onStart: $progress")
    }

    override fun onEnd(progress: Float) {
        Log.d(tag, "onEnd: $progress")
    }
}

fun AnimationHelperDslBuilder.log(tag: String = "AnimationHelperLog") {
    val helperLog = AnimationHelperLog(tag)
    helper.addConverter(helperLog)
    helper.addOnEndListener(helperLog)
    helper.addOnStartListener(helperLog)
}

fun AnimationHelperDslBuilder.logOnDebug(context: Context, tag: String = "AnimationHelperLog") {
    val flags = context.applicationInfo.flags
    if (flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
        log(tag)
    }
}
