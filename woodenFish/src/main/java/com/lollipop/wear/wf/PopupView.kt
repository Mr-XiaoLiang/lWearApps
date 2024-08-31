package com.lollipop.wear.wf

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.PorterDuff
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

    fun setPopupDrawable(index: IconIndex, drawable: Drawable?) {
        popupCanvasDrawable.setPopupDrawable(index, drawable)
    }

    fun addPopup(x: Int, y: Int, index: IconIndex) {
        popupCanvasDrawable.addPopup(x, y, index)
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

        @SuppressLint("ClickableViewAccessibility")
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

        private val iconMap = IconMap()

        private val popupSize = Rect()
        private val tempBounds = Rect()

        private var baseAlpha = MAX_ALPHA
        private val popupList = ArrayList<Popup>()

        var popupDuration = 800L
        var popupOffsetLength = 0

        private val paint = Paint()

        fun setPopupSize(width: Int, height: Int) {
            popupSize.set(0, 0, width, height)
            iconMap.setIconSize(width, height)
        }

        fun setPopupDrawable(index: IconIndex, drawable: Drawable?) {
            iconMap.setIcon(index, drawable)
        }

        fun addPopup(x: Int, y: Int, iconIndex: IconIndex) {
            val popup = Popup(x, y, iconIndex)
            popupList.add(popup)
            requestInvalidate()
        }

        override fun draw(canvas: Canvas) {
            val popWidthHalf = popupSize.width() / 2
            val popHeightHalf = popupSize.height() / 2
            tempBounds.set(popupSize)
            for (pop in popupList) {
                val progress = pop.getProgress(popupDuration)
                if (progress > MAX_PROGRESS) {
                    continue
                }
                val iconCache = iconMap.getIcon(pop.iconIndex)
                val bitmap = iconCache.bitmap ?: continue
                val offset = (popupOffsetLength * progress).toInt()
                tempBounds.offsetTo(pop.x - popWidthHalf, pop.y - popHeightHalf - offset)
                var popAlpha = (baseAlpha * (1 - progress)).toInt()
                if (popAlpha > MAX_ALPHA) {
                    popAlpha = MAX_ALPHA
                }
                if (popAlpha < 0) {
                    popAlpha = 0
                }
                paint.alpha = popAlpha
                val saveCount = canvas.save()
                canvas.translate(tempBounds.left.toFloat(), tempBounds.top.toFloat())
                canvas.clipRect(popupSize)
                canvas.drawBitmap(bitmap, 0F, 0F, paint)
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
            iconMap.setColorFilter(colorFilter)
        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSPARENT
        }

    }

    private class Popup(
        val x: Int,
        val y: Int,
        val iconIndex: IconIndex
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

    enum class IconIndex {
        One,
        Two
    }

    private class IconMap {

        private val iconArray = Array(IconIndex.entries.size) { IconCache() }

        fun getIcon(index: IconIndex): IconCache {
            return iconArray[index.ordinal]
        }

        fun setIconSize(width: Int, height: Int) {
            iconArray.forEach {
                it.setSize(width, height)
            }
        }

        fun setIcon(index: IconIndex, drawable: Drawable?) {
            getIcon(index).setIcon(drawable)
        }

        fun setColorFilter(colorFilter: ColorFilter?) {
            iconArray.forEach {
                it.setColorFilter(colorFilter)
            }
        }

    }

    private class IconCache {
        private var drawable: Drawable? = null
        var bitmap: Bitmap? = null
            private set

        private val iconSize = Rect()

        fun setSize(width: Int, height: Int) {
            iconSize.set(0, 0, width, height)
            drawable?.bounds = iconSize
            bitmap?.let {
                if (it.width != width || it.height != height) {
                    it.recycle()
                    bitmap = null
                }
            }
            if (bitmap == null) {
                val newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                bitmap = newBitmap
                updateBitmap()
            }
        }

        fun setColorFilter(colorFilter: ColorFilter?) {
            drawable?.colorFilter = colorFilter
            updateBitmap()
        }

        fun setIcon(drawable: Drawable?) {
            this.drawable = drawable
            drawable?.bounds = iconSize
            updateBitmap()
        }

        private fun updateBitmap() {
            val b = bitmap ?: return
            val canvas = Canvas(b)
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            drawable?.draw(canvas)
        }

    }

}