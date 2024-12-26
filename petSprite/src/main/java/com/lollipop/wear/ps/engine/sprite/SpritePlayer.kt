package com.lollipop.wear.ps.engine.sprite

import android.content.Context
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import android.os.Build
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Log
import android.view.View

class SpritePlayer @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : View(context, attributeSet) {

    var spriteInfo: SpriteInfo = SpriteInfo.None
        private set

    var spriteFrame: SpriteFrame = SpriteFrame.None

    var spriteToward: SpriteToward = SpriteToward.Down
        private set

    var isRunning: Boolean = false
        private set

    private var fromIndex = 0

    var frameInterval = 1000L / 10

    private var lastFrameTime = 0L

    private val paint = Paint().apply {
        isAntiAlias = true
        isDither = true
    }

    fun setFps(fps: Int) {
        frameInterval = 1000L / fps
    }

    fun changedToward(toward: SpriteToward, run: Boolean) {
        this.isRunning = run
        checkSupportedToward(toward)
        onStateChanged()
    }

    private fun checkSupportedToward(expectant: SpriteToward) {
        var frame = spriteInfo.getFrame(expectant)
        if (frame != SpriteFrame.None) {
            // 如果支持新的方向，那么我们旋转方向
            if (spriteToward != expectant) {
                resetFrame()
            }
            spriteToward = expectant
            spriteFrame = frame
            return
        }
        if (spriteToward != expectant) {
            frame = spriteInfo.getFrame(spriteToward)
            if (frame != SpriteFrame.None) {
                // 如果不支持，当时当前的方向是支持的，那么就保持不变
                spriteFrame = frame
                return
            }
        }
        // 否则的情况下，我们选择一个支持的方向
        SpriteToward.entries.forEach {
            frame = spriteInfo.getFrame(it)
            if (frame != SpriteFrame.None) {
                spriteFrame = frame
                spriteToward = it
                resetFrame()
                return
            }
        }
    }

    fun setSpriteInfo(info: SpriteInfo) {
        if (info.isSame(spriteInfo)) {
            return
        }
        spriteInfo = info
        updatePaint()
        checkSupportedToward(spriteToward)
        onStateChanged()
    }

    private fun updatePaint() {
        val bitmap = spriteInfo.loadBitmap(context)
        if (bitmap != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                paint.shader = BitmapShader(bitmap, Shader.TileMode.DECAL, Shader.TileMode.DECAL)
            } else {
                paint.shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            }
        } else {
            paint.shader = null
        }
    }

    private fun onStateChanged() {
        postInvalidateOnAnimation()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val frame = spriteFrame
        if (frame is SpriteFrame.Line) {
            onDrawFrame(frame, canvas)
        }
        if (isRunning) {
            postInvalidateOnAnimation()
        }
    }

    private fun onDrawFrame(frame: SpriteFrame.Line, canvas: Canvas) {
        try {
            val frameHeight: Int = frame.frameHeight
            val frameWidth: Int = frame.frameWidth
            val index = getFrameIndex()
            val viewHeight: Int = height
            val viewWidth: Int = width
            val scale: Float = viewHeight * 1F / frameHeight
            val saveCount = canvas.save()
            canvas.scale(scale, scale)
            val offsetX = (frame.left + frameWidth * index).toFloat()
            val offsetY = frame.top.toFloat()
            canvas.translate(offsetX * -1, offsetY * -1)
            canvas.drawRect(
                offsetX,
                offsetY,
                offsetX + frameWidth,
                offsetY + frameHeight,
                paint
            )
            canvas.restoreToCount(saveCount)
        } catch (e: Throwable) {
            Log.e("SpritePlayer", "onDrawFrame", e)
        }
    }

    private fun getFrameIndex(): Int {
        if (!isRunning) {
            fromIndex = 0
            return 0
        }
        when (val frame = spriteFrame) {
            is SpriteFrame.Line -> {
                val now = now()
                if (now - lastFrameTime >= frameInterval) {
                    lastFrameTime = now
                    fromIndex++
                }
                fromIndex %= frame.count
                return fromIndex
            }

            SpriteFrame.None -> {
                fromIndex = 0
                return 0
            }
        }
    }

    private fun resetFrame() {
        lastFrameTime = now()
        fromIndex = 0
    }

    private fun now() = SystemClock.uptimeMillis()

}