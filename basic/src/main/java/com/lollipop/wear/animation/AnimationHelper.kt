package com.lollipop.wear.animation

import android.animation.Animator
import android.animation.ValueAnimator
import com.lollipop.wear.animation.dsl.AnimationHelperDslBuilder
import kotlin.math.abs

class AnimationHelper(
    private val duration: Long = DEFAULT_DURATION,
) : ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {

    companion object {
        const val DEFAULT_DURATION = 300L

        const val PROGRESS_MIN = 0F

        const val PROGRESS_MAX = 1F

        fun similar(a: Float, b: Float): Boolean {
            return abs(a - b) < 0.01F
        }

    }

    private val animator = ValueAnimator.ofFloat(PROGRESS_MIN, PROGRESS_MAX)

    private val converterList = ArrayList<AnimationConverter>()
    private val onEndListenerList = ArrayList<OnAnimationEndListener>()
    private val onStartListenerList = ArrayList<OnAnimationStartListener>()

    private var progress = 0F

    init {
        animator.duration = duration
        animator.addUpdateListener(this)
        animator.addListener(this)
    }

    fun expand() {
        startAnimation(PROGRESS_MAX)
    }

    fun close() {
        startAnimation(PROGRESS_MIN)
    }

    private fun startAnimation(target: Float) {
        animator.cancel()
        val d = duration * (abs(target - progress) / (PROGRESS_MAX - PROGRESS_MIN)).toLong()
        animator.duration = d
        animator.setFloatValues(progress, target)
        animator.start()
    }

    private fun onAnimationUpdate(value: Float) {
        progress = value
        converterList.forEach { it.onUpdate(value) }
    }

    override fun onAnimationUpdate(animation: ValueAnimator) {
        if (animation == animator) {
            val value = animation.animatedValue
            if (value is Float) {
                onAnimationUpdate(value)
            }
        }
    }

    override fun onAnimationStart(animation: Animator) {
        if (animation == animator) {
            onStartListenerList.forEach { it.onStart(progress) }
        }
    }

    override fun onAnimationEnd(animation: Animator) {
        if (animation == animator) {
            onEndListenerList.forEach { it.onEnd(progress) }
        }
    }

    override fun onAnimationCancel(animation: Animator) {
    }

    override fun onAnimationRepeat(animation: Animator) {
    }

    fun addConverter(converter: AnimationConverter) {
        converterList.add(converter)
    }

    fun removeConverter(converter: AnimationConverter) {
        converterList.remove(converter)
    }

    fun addOnEndListener(listener: OnAnimationEndListener) {
        onEndListenerList.add(listener)
    }

    fun removeOnEndListener(listener: OnAnimationEndListener) {
        onEndListenerList.remove(listener)
    }

    fun addOnStartListener(listener: OnAnimationStartListener) {
        onStartListenerList.add(listener)
    }

    fun removeOnStartListener(listener: OnAnimationStartListener) {
        onStartListenerList.remove(listener)
    }

    fun build(block: AnimationHelperDslBuilder.() -> Unit): AnimationHelperDslBuilder {
        return AnimationHelperDslBuilder(this).apply(block)
    }

}