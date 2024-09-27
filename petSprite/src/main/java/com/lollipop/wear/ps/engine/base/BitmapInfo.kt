package com.lollipop.wear.ps.engine.base

import android.content.Context
import android.graphics.Bitmap
import android.util.Size

abstract class BitmapInfo {

    companion object {
        private val emptySize = Size(0, 0)
    }

    private var bitmapCache: ResourceInfo<Bitmap> = ResourceInfo.Undefined()

    private var bitmapSizeCache: ResourceInfo<Size> = ResourceInfo.Undefined()

    fun getResource(context: Context): Bitmap? {
        if (bitmapCache is ResourceInfo.Undefined) {
            val bitmap = loadBitmap(context)
            bitmapCache = if (bitmap == null) {
                ResourceInfo.None()
            } else {
                ResourceInfo.Success(bitmap)
            }
        }
        bitmapCache.let {
            if (it is ResourceInfo.Success) {
                return it.data
            }
        }
        return null
    }

    fun getResourceSize(context: Context): Size {
        if (bitmapSizeCache is ResourceInfo.Undefined) {
            val bitmap = getResource(context)
            bitmapSizeCache = if (bitmap == null) {
                ResourceInfo.None()
            } else {
                ResourceInfo.Success(Size(bitmap.width, bitmap.height))
            }
        }
        bitmapSizeCache.let {
            if (it is ResourceInfo.Success) {
                return it.data
            }
        }
        return emptySize
    }

    protected abstract fun loadBitmap(context: Context): Bitmap?

    protected sealed class ResourceInfo<T> {

        class Undefined<T> : ResourceInfo<T>()

        class Success<T>(val data: T) : ResourceInfo<T>()

        class None<T> : ResourceInfo<T>()

    }

}