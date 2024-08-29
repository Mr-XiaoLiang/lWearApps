package com.lollipop.wear.wf

import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.SystemClock
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatImageView

class PopupView @JvmOverloads constructor(
    context: Context, attributeSet: AttributeSet? = null
) : AppCompatImageView(
    context, attributeSet
) {

    companion object {
        private const val MAX_PROGRESS = 1F
        private const val MAX_ALPHA = 255
        private fun now(): Long {
            return SystemClock.uptimeMillis()
        }
    }

    private val popupCanvasDrawable = PopupDrawable(::postInvalidateSelf)

    var popupDuration: Long
        get() {
            return popupCanvasDrawable.popupDuration
        }
        set(value) {
            popupCanvasDrawable.popupDuration = value
        }
    var popupOffsetLength: Int
        get() {
            return popupCanvasDrawable.popupOffsetLength
        }
        set(value) {
            popupCanvasDrawable.popupOffsetLength = value
        }

    init {
        setImageDrawable(popupCanvasDrawable)
    }

    fun setPopupSize(width: Int, height: Int) {
        popupCanvasDrawable.setPopupSize(width, height)
    }

    fun setPopupDrawable(drawable: Drawable?) {
        popupCanvasDrawable.setPopupDrawable(drawable)
    }

    fun addPopup(x: Int, y: Int) {
        popupCanvasDrawable.addPopup(x, y)
    }

    fun bindClickListener(onClick: (x: Int, y: Int) -> Unit) {
        val locationHelper = ClickWithLocationHelper(onClick)
        setOnClickListener(locationHelper)
        setOnTouchListener(locationHelper)
    }

    private fun postInvalidateSelf() {
        postInvalidateOnAnimation()
    }

    class ClickWithLocationHelper(
        val onClick: (x: Int, y: Int) -> Unit
    ) : OnClickListener, OnTouchListener {

        private var lastX = 0
        private var lastY = 0

        override fun onClick(v: View?) {
            onClick(lastX, lastY)
        }

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            event ?: return false
            lastX = event.x.toInt()
            lastY = event.y.toInt()
            return false
        }

    }

    private class PopupDrawable(
        val requestInvalidate: () -> Unit
    ) : Drawable() {

        private var popupDrawable: Drawable? = null

        private val popupSize = Rect()
        private val tempBounds = Rect()

        private var baseAlpha = MAX_ALPHA
        private val popupList = ArrayList<Popup>()

        var popupDuration = 800L
        var popupOffsetLength = 0

        fun setPopupSize(width: Int, height: Int) {
            popupSize.set(0, 0, width, height)
            popupDrawable?.bounds = popupSize
        }

        fun setPopupDrawable(drawable: Drawable?) {
            popupDrawable = drawable
            drawable?.bounds = popupSize
        }

        fun addPopup(x: Int, y: Int) {
            val popup = Popup(x, y)
            popupList.add(popup)
            requestInvalidate()
        }

        override fun draw(canvas: Canvas) {
            val drawable = popupDrawable ?: return
            val popWidthHalf = popupSize.width() / 2
            val popHeightHalf = popupSize.height() / 2
            tempBounds.set(popupSize)
            for (pop in popupList) {
                val progress = pop.getProgress(popupDuration)
                if (progress > MAX_PROGRESS) {
                    continue
                }
                val offset = (popupOffsetLength * progress).toInt()
                tempBounds.offsetTo(pop.x - popWidthHalf, pop.y - popHeightHalf - offset)
                var popAlpha = (baseAlpha * (1 - progress)).toInt()
                if (popAlpha > MAX_ALPHA) {
                    popAlpha = MAX_ALPHA
                }
                if (popAlpha < 0) {
                    popAlpha = 0
                }
                drawable.alpha = popAlpha
                val saveCount = canvas.save()
                canvas.translate(tempBounds.left.toFloat(), tempBounds.top.toFloat())
                canvas.clipRect(popupSize)
                drawable.draw(canvas)
                canvas.restoreToCount(saveCount)
            }
            val now = now()
            popupList.removeIf { it.isTimeout(now, popupDuration) }
            if (popupList.isNotEmpty()) {
                requestInvalidate()
            }
        }

        override fun setAlpha(alpha: Int) {
            baseAlpha = alpha
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            popupDrawable?.colorFilter = colorFilter
        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSPARENT
        }

    }

    private class Popup(
        val x: Int,
        val y: Int,
    ) {
        val startTime: Long = now()

        fun getProgress(duration: Long): Float {
            val now = now()
            return (now - startTime) * 1F / duration
        }

        fun isTimeout(now: Long, duration: Long): Boolean {
            return (now - startTime) > duration
        }

    }

}