package com.lollipop.wear.ps.engine.sprite

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.File

sealed class SpriteInfo(
    val up: SpriteFrame,
    val down: SpriteFrame,
    val left: SpriteFrame,
    val right: SpriteFrame
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
        up: SpriteFrame,
        down: SpriteFrame,
        left: SpriteFrame,
        right: SpriteFrame
    ) : SpriteInfo(up, down, left, right) {

        override fun loadBitmap(context: Context): Bitmap? {
            try {
                return BitmapFactory.decodeStream(context.assets.open(path))
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
        up: SpriteFrame,
        down: SpriteFrame,
        left: SpriteFrame,
        right: SpriteFrame
    ) : SpriteInfo(up, down, left, right) {
        override fun loadBitmap(context: Context): Bitmap? {
            try {
                return BitmapFactory.decodeResource(context.resources, id)
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
        up: SpriteFrame,
        down: SpriteFrame,
        left: SpriteFrame,
        right: SpriteFrame
    ) : SpriteInfo(up, down, left, right) {
        override fun loadBitmap(context: Context): Bitmap? {
            try {
                return BitmapFactory.decodeFile(file.path)
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
        up: SpriteFrame,
        down: SpriteFrame,
        left: SpriteFrame,
        right: SpriteFrame
    ) : SpriteInfo(up, down, left, right) {
        override fun loadBitmap(context: Context): Bitmap? {
            try {
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
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

}
