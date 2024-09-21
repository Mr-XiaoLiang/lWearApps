package com.lollipop.wear.ps.engine

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File

sealed class SpriteInfo(
    val up: SpriteFrame,
    val down: SpriteFrame,
    val left: SpriteFrame,
    val right: SpriteFrame
) {

    abstract fun loadBitmap(context: Context): Bitmap

    class FromAssets(
        val path: String,
        up: SpriteFrame,
        down: SpriteFrame,
        left: SpriteFrame,
        right: SpriteFrame
    ) : SpriteInfo(up, down, left, right) {

        override fun loadBitmap(context: Context): Bitmap {
            return BitmapFactory.decodeStream(context.assets.open(path))
        }

    }

    class FromResource(
        val id: Int,
        up: SpriteFrame,
        down: SpriteFrame,
        left: SpriteFrame,
        right: SpriteFrame
    ) : SpriteInfo(up, down, left, right) {
        override fun loadBitmap(context: Context): Bitmap {
            return BitmapFactory.decodeResource(context.resources, id)
        }
    }

    class FromFile(
        private val file: File,
        up: SpriteFrame,
        down: SpriteFrame,
        left: SpriteFrame,
        right: SpriteFrame
    ) : SpriteInfo(up, down, left, right) {
        override fun loadBitmap(context: Context): Bitmap {
            return BitmapFactory.decodeFile(file.path)
        }
    }

    class FromByteArray(
        private val bytes: ByteArray,
        up: SpriteFrame,
        down: SpriteFrame,
        left: SpriteFrame,
        right: SpriteFrame
    ) : SpriteInfo(up, down, left, right) {
        override fun loadBitmap(context: Context): Bitmap {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
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
