package com.lollipop.wear.ps.engine.sprite

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.collection.LruCache
import java.io.File
import java.security.MessageDigest

object BitmapCache {

    private val lruCache = LruCache<String, Bitmap>(50)

    fun getBitmap(key: String): Bitmap? {
        return lruCache.get(key)
    }

    fun putBitmap(key: String, bitmap: Bitmap) {
        lruCache.put(key, bitmap)
    }

    fun optAssets(context: Context, path: String): Bitmap? {
        val key = "Assets:$path"
        var bitmap = getBitmap(key)
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeStream(context.assets.open(path))
            putBitmap(key, bitmap)
        }
        return bitmap
    }

    fun optFile(file: File): Bitmap? {
        val key = "File:${file.path}"
        var bitmap = getBitmap(key)
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeFile(file.path)
            putBitmap(key, bitmap)
        }
        return bitmap
    }

    fun optByteArray(bytes: ByteArray): Bitmap? {
        val key = "Byte:${getMd5(bytes)}"
        var bitmap = getBitmap(key)
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            putBitmap(key, bitmap)
        }
        return bitmap
    }

    fun optResource(context: Context, id: Int): Bitmap? {
        val key = "Resource:$id"
        var bitmap = getBitmap(key)
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(context.resources, id)
            putBitmap(key, bitmap)
        }
        return bitmap
    }

    private fun getMd5(bytes: ByteArray): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(bytes)
        val sb = StringBuilder()
        for (b in digest) {
            sb.append(String.format("%02x", b))
        }
        return sb.toString()
    }


}