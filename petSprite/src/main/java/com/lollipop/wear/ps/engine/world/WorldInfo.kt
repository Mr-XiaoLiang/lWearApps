package com.lollipop.wear.ps.engine.world

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.File


/**
 * 世界的信息
 */
class WorldInfo(
    val gaia: GaiaInfo,
    val edifice: List<EdificeInfo>
) {

    companion object {

        val EMPTY = WorldInfo(EmptyGaia, emptyList())

        fun create(): Builder {
            return Builder()
        }
    }

    class Builder {
        private var gaia: GaiaInfo = EmptyGaia
        private val edificeList = mutableListOf<EdificeInfo>()

        fun bindGaia(gaia: GaiaInfo): Builder {
            this.gaia = gaia
            return this
        }

        fun addEdifice(edifice: EdificeInfo): Builder {
            edificeList.add(edifice)
            return this
        }

        fun build(): WorldInfo {
            return WorldInfo(gaia, edificeList)
        }

    }

}

/**
 * 大地，不知道取什么名字，就叫盖亚吧
 * 世界的尺寸设置，决定了单元格的数量，以及单元格的尺寸
 */
interface GaiaInfo {

    /**
     * 世界的宽度，表示格子的数量
     */
    val width: Int

    /**
     * 世界的高度，表示格子的数量
     */
    val height: Int

    /**
     * 是否使用填充模式，如果是的话，地图的横向或者纵向，短边会填充满屏幕并且拉伸
     */
    val fixMode: Boolean

}

object EmptyGaia : GaiaInfo {
    override val width: Int = 0
    override val height: Int = 0
    override val fixMode: Boolean = true
}

sealed class StaticGaia(
    override val width: Int,
    override val height: Int,
    override val fixMode: Boolean = true
) : GaiaInfo {

    abstract fun loadBitmap(context: Context): Bitmap?

    class FromAssets(
        val path: String,
        width: Int,
        height: Int,
        fixMode: Boolean = true
    ) : StaticGaia(width = width, height = height, fixMode = fixMode) {

        override fun loadBitmap(context: Context): Bitmap? {
            try {
                return BitmapFactory.decodeStream(context.assets.open(path))
            } catch (e: Throwable) {
                Log.e("StaticGaia", "FromAssets.loadBitmap", e)
            }
            return null
        }

    }

    class FromResource(
        private val id: Int,
        width: Int,
        height: Int,
        fixMode: Boolean = true
    ) : StaticGaia(width = width, height = height, fixMode = fixMode) {
        override fun loadBitmap(context: Context): Bitmap? {
            try {
                return BitmapFactory.decodeResource(context.resources, id)
            } catch (e: Throwable) {
                Log.e("StaticGaia", "FromResource.loadBitmap", e)
            }
            return null
        }
    }

    class FromFile(
        private val file: File,
        width: Int,
        height: Int,
        fixMode: Boolean = true
    ) : StaticGaia(width = width, height = height, fixMode = fixMode) {
        override fun loadBitmap(context: Context): Bitmap? {
            try {
                return BitmapFactory.decodeFile(file.path)
            } catch (e: Throwable) {
                Log.e("StaticGaia", "FromFile.loadBitmap", e)
            }
            return null
        }

    }

    class FromByteArray(
        private val bytes: ByteArray,
        width: Int,
        height: Int,
        fixMode: Boolean = true
    ) : StaticGaia(width = width, height = height, fixMode = fixMode) {
        override fun loadBitmap(context: Context): Bitmap? {
            try {
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (e: Throwable) {
                Log.e("StaticGaia", "FromByteArray.loadBitmap", e)
            }
            return null
        }
    }

}


/**
 * 建筑信息
 */
interface EdificeInfo {

    val width: Int

    val height: Int

    val x: Int

    val y: Int

}

object EmptyEdifice : EdificeInfo {
    override val width: Int = 0
    override val height: Int = 0
    override val x: Int = 0
    override val y: Int = 0
}

sealed class StaticEdifice(
    override val width: Int,
    override val height: Int,
    override val x: Int,
    override val y: Int
) : EdificeInfo {

    abstract fun loadBitmap(context: Context): Bitmap?

    class FromAssets(
        val path: String,
        width: Int,
        height: Int,
        x: Int,
        y: Int
    ) : StaticEdifice(width = width, height = height, x = x, y = y) {
        override fun loadBitmap(context: Context): Bitmap? {
            try {
                return BitmapFactory.decodeStream(context.assets.open(path))
            } catch (e: Throwable) {
                Log.e("StaticEdifice", "FromAssets.loadBitmap", e)
            }
            return null
        }
    }

    class FromResource(
        private val id: Int,
        width: Int,
        height: Int,
        x: Int,
        y: Int
    ) : StaticEdifice(width = width, height = height, x = x, y = y) {
        override fun loadBitmap(context: Context): Bitmap? {
            try {
                return BitmapFactory.decodeResource(context.resources, id)
            } catch (e: Throwable) {
                Log.e("StaticEdifice", "FromResource.loadBitmap", e)
            }
            return null
        }
    }

    class FromFile(
        private val file: File,
        width: Int,
        height: Int,
        x: Int,
        y: Int
    ) : StaticEdifice(width = width, height = height, x = x, y = y) {
        override fun loadBitmap(context: Context): Bitmap? {
            try {
                return BitmapFactory.decodeFile(file.path)
            } catch (e: Throwable) {
                Log.e("StaticEdifice", "FromFile.loadBitmap", e)
            }
            return null
        }
    }

    class FromByteArray(
        private val bytes: ByteArray,
        width: Int,
        height: Int,
        x: Int,
        y: Int
    ) : StaticEdifice(width = width, height = height, x = x, y = y) {
        override fun loadBitmap(context: Context): Bitmap? {
            try {
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (e: Throwable) {
                Log.e("StaticEdifice", "FromByteArray.loadBitmap", e)
            }
            return null
        }
    }

}
