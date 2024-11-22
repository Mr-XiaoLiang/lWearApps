package com.lollipop.wear.widget

import android.animation.Animator
import android.animation.ValueAnimator
import android.view.View
import androidx.core.view.isVisible
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class TransformAnimator(
    val targetA: View,
    val targetB: View,
    val duration: Long
) : ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {

    companion object {
        private const val PROGRESS_A = 0F
        private const val PROGRESS_B = 2F

        private const val HALF_PROGRESS = (PROGRESS_A + PROGRESS_B) / 2

        private const val PROGRESS_MAX = 1F
        private const val PROGRESS_MIN = 0F
    }

    private var currentProgress = PROGRESS_A

    private val valueAnimator by lazy {
        ValueAnimator().apply {
            addListener(this@TransformAnimator)
            addUpdateListener(this@TransformAnimator)
        }
    }

    fun toTargetA(animation: Boolean) {
        if (!animation) {
            updateByProgress(PROGRESS_A)
            onProgressEnd()
            return
        }
        startAnimation(PROGRESS_A)
    }

    fun toTargetB(animation: Boolean) {
        if (!animation) {
            updateByProgress(PROGRESS_B)
            onProgressEnd()
            return
        }
        startAnimation(PROGRESS_B)
    }

    private fun startAnimation(endProgress: Float) {
        valueAnimator.cancel()
        val weight = abs(endProgress - currentProgress) / abs(PROGRESS_B - PROGRESS_A)
        val finalDuration = max(min((duration * weight).toLong(), duration), 0L)
        if (finalDuration < 10L) {
            updateByProgress(endProgress)
            onProgressEnd()
            return
        }
        valueAnimator.setFloatValues(currentProgress, endProgress)
        valueAnimator.setDuration(finalDuration)
        valueAnimator.start()
    }

    private fun updateByProgress(value: Float) {
        this.currentProgress = value

        // 因为我们的A的Progress是小于B的，所以我们认为小的那一半是A的进度，大的那一半是B的进度
        val progressA = getProgressA(value)
        val progressB = getProgressB(value)
        // 分开并包装View的具体变化逻辑
        updateTargetA(progressA)
        updateTargetB(progressB)
    }

    private fun updateTargetA(progress: Float) {
        targetA.alpha = progress
        // view的缩放，只要缩放到两者的中间尺寸就行了
        val left = targetB.left - targetA.left
        val top = targetB.top - targetA.top
        val right = targetB.right - targetA.right
        val bottom = targetB.bottom - targetA.bottom
        targetA.width * 1F / targetB.width * progress
        // 20 / 5 / 2 = 2
    }

    private fun updateTargetB(progress: Float) {
        targetB.alpha = progress
    }

    private fun getProgressA(value: Float): Float {
        var progress = PROGRESS_MAX - (min(value, HALF_PROGRESS) / HALF_PROGRESS)
        if (progress < PROGRESS_MIN) {
            progress = PROGRESS_MIN
        }
        if (progress > PROGRESS_MAX) {
            progress = PROGRESS_MAX
        }
        return progress
    }

    private fun getProgressB(value: Float): Float {
        var progress = (value - HALF_PROGRESS) / HALF_PROGRESS
        if (progress < PROGRESS_MIN) {
            progress = PROGRESS_MIN
        }
        if (progress > PROGRESS_MAX) {
            progress = PROGRESS_MAX
        }
        return progress
    }

    private fun onProgressEnd() {
        TODO("Not yet implemented")
    }

    override fun onAnimationUpdate(animation: ValueAnimator) {
        if (animation === valueAnimator) {
            val value = animation.animatedValue
            if (value is Float) {
                updateByProgress(value)
            }
        }
    }

    override fun onAnimationStart(animation: Animator) {
        // 开始的时候都显示，不然没法整
        targetA.isVisible = true
        targetB.isVisible = true
    }

    override fun onAnimationEnd(animation: Animator) {
        onProgressEnd()
    }

    override fun onAnimationCancel(animation: Animator) {
    }

    override fun onAnimationRepeat(animation: Animator) {
    }


}