package com.lollipop.qr

import androidx.lifecycle.LifecycleOwner
import com.lollipop.qr.reader.CameraBarcodeReader
import com.lollipop.qr.reader.ImageBarcodeReader

object BarcodeReader {
    fun fromCamera(lifecycleOwner: LifecycleOwner): CameraBarcodeReader {
        return CameraBarcodeReader(lifecycleOwner)
    }

    fun fromLocal(lifecycleOwner: LifecycleOwner): ImageBarcodeReader {
        return ImageBarcodeReader(lifecycleOwner)
    }
}