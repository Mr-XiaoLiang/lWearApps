package com.lollipop.qr

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.lollipop.qr.writer.BarcodeWriter

class QrView @JvmOverloads constructor(
    context: Context, attributeSet: AttributeSet? = null
) : AppCompatImageView(context, attributeSet) {

    private var barcodeWriter: BarcodeWriter? = null

    init {
        findLifecycleOwner()?.let {
            bind(it.lifecycle)
        }
    }

    private fun findLifecycleOwner(): LifecycleOwner? {
        var c: Context? = context
        do {
            if (c is LifecycleOwner) {
                return c
            }
            c = if (c is ContextWrapper) {
                c.baseContext
            } else {
                null
            }
        } while (c != null)
        return null
    }

    fun bind(lifecycle: Lifecycle) {
        barcodeWriter?.destroy()
        barcodeWriter = BarcodeWriter(lifecycle)
    }

    fun setContent(content: String) {
        val writer = barcodeWriter ?: return
        writer.encode(content).loadBitmap { result ->
            onQrResult(result)
        }
    }

    private fun onQrResult(result: Result<Bitmap>) {
        result.onSuccess {
            setImageBitmap(it)
        }
    }
}