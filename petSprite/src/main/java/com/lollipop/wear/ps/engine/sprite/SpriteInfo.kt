package com.lollipop.wear.ps.engine.sprite

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.lollipop.wear.ps.engine.sprite.SpriteFrame.Line
import java.io.File

sealed class SpriteInfo(
    val left: SpriteFrame,
    val up: SpriteFrame,
    val right: SpriteFrame,
    val down: SpriteFrame
) {

    abstract fun loadBitmap(context: Context): Bitmap?

    fun getFrame(toward: SpriteToward): SpriteFrame {
        when (toward) {
            SpriteToward.Up -> {
                return up
            }

            SpriteToward.Down -> {
                return down
            }

            SpriteToward.Left -> {
                return left
            }

            SpriteToward.Right -> {
                return right
            }
        }
    }

    fun isSame(other: SpriteInfo): Boolean {
        if (this === other) {
            return true
        }
        if (javaClass != other.javaClass) {
            return false
        }
        if (this.left != other.left
            || this.right != other.right
            || this.up != other.up
            || this.down != other.down
        ) {
            return false
        }
        return isContentSame(other)
    }

    protected abstract fun isContentSame(other: SpriteInfo): Boolean

    data object None : SpriteInfo(
        SpriteFrame.None,
        SpriteFrame.None,
        SpriteFrame.None,
        SpriteFrame.None
    ) {
        override fun loadBitmap(context: Context): Bitmap? {
            return null
        }

        override fun isContentSame(other: SpriteInfo): Boolean {
            return true
        }

    }

    class FromAssets(
        val path: String,
        left: SpriteFrame,
        up: SpriteFrame,
        right: SpriteFrame,
        down: SpriteFrame
    ) : SpriteInfo(left, up, right, down) {

        override fun loadBitmap(context: Context): Bitmap? {
            try {
                return BitmapCache.optAssets(context, path)
            } catch (e: Throwable) {
                Log.e("SpriteInfo", "FromAssets.loadBitmap", e)
            }
            return null
        }

        override fun isContentSame(other: SpriteInfo): Boolean {
            if (other is FromAssets) {
                return other.path == this.path
            }
            return false
        }

    }

    class FromResource(
        private val id: Int,
        left: SpriteFrame,
        up: SpriteFrame,
        right: SpriteFrame,
        down: SpriteFrame
    ) : SpriteInfo(left, up, right, down) {
        override fun loadBitmap(context: Context): Bitmap? {
            try {
                return BitmapCache.optResource(context, id)
            } catch (e: Throwable) {
                Log.e("SpriteInfo", "FromResource.loadBitmap", e)
            }
            return null
        }

        override fun isContentSame(other: SpriteInfo): Boolean {
            if (other is FromResource) {
                return other.id == this.id
            }
            return false
        }
    }

    class FromFile(
        private val file: File,
        left: SpriteFrame,
        up: SpriteFrame,
        right: SpriteFrame,
        down: SpriteFrame
    ) : SpriteInfo(left, up, right, down) {
        override fun loadBitmap(context: Context): Bitmap? {
            try {
                return BitmapCache.optFile(file)
            } catch (e: Throwable) {
                Log.e("SpriteInfo", "FromFile.loadBitmap", e)
            }
            return null
        }

        override fun isContentSame(other: SpriteInfo): Boolean {
            if (other is FromFile) {
                return other.file.path == this.file.path
            }
            return false
        }
    }

    class FromByteArray(
        private val bytes: ByteArray,
        left: SpriteFrame,
        up: SpriteFrame,
        right: SpriteFrame,
        down: SpriteFrame
    ) : SpriteInfo(left, up, right, down) {
        override fun loadBitmap(context: Context): Bitmap? {
            try {
                return BitmapCache.optByteArray(bytes)
            } catch (e: Throwable) {
                Log.e("SpriteInfo", "FromByteArray.loadBitmap", e)
            }
            return null
        }

        override fun isContentSame(other: SpriteInfo): Boolean {
            if (other is FromByteArray) {
                return other.bytes.contentEquals(this.bytes)
            }
            return false
        }
    }

    companion object {
        inline fun <reified T : SpriteInfo> createBy4x4(
            imageWidth: Int,
            imageHeight: Int = imageWidth,
            paddingLeft: Int = 0,
            paddingTop: Int = 0,
            paddingRight: Int = 0,
            paddingBottom: Int = 0,
            result: (left: Line, up: Line, right: Line, down: Line) -> T
        ): T {
            val lines = SpriteFrame.createBy4x4(
                imageWidth,
                imageHeight,
                paddingLeft,
                paddingTop,
                paddingRight,
                paddingBottom
            )
            return result(lines[0], lines[1], lines[2], lines[3])
        }
    }

}

sealed class SpriteFrame {

    data object None : SpriteFrame()

    class Line(
        val count: Int,
        val top: Int,
        val left: Int,
        val frameWidth: Int,
        val frameHeight: Int
    ) : SpriteFrame()

    companion object {
        /**
         * @return [left, up, right, down]
         */
        fun createBy4x4(
            imageWidth: Int,
            imageHeight: Int,
            paddingLeft: Int = 0,
            paddingTop: Int = 0,
            paddingRight: Int = 0,
            paddingBottom: Int = 0,
        ): Array<Line> {
            // 图片一般都是，从上到下依次为：正面、左面、右边、背面
            // 因此顺序为：下，左，右，上
            val frameWidth = (imageWidth - paddingLeft - paddingRight) / 4
            val frameHeight = (imageHeight - paddingTop - paddingBottom) / 4
            var linTop = paddingTop
            val down = Line(
                count = 4,
                top = linTop,
                left = paddingLeft,
                frameWidth = frameWidth,
                frameHeight = frameHeight
            )
            linTop += frameHeight
            val left = Line(
                count = 4,
                top = linTop,
                left = paddingLeft,
                frameWidth = frameWidth,
                frameHeight = frameHeight
            )
            linTop += frameHeight
            val right = Line(
                count = 4,
                top = linTop,
                left = paddingLeft,
                frameWidth = frameWidth,
                frameHeight = frameHeight
            )
            linTop += frameHeight
            val up = Line(
                count = 4,
                top = linTop,
                left = paddingLeft,
                frameWidth = frameWidth,
                frameHeight = frameHeight
            )
            return arrayOf(left, up, right, down)
        }
    }

}
