package com.lollipop.filebrowser.page

import android.os.Bundle
import android.util.Log
import com.lollipop.filebrowser.databinding.ActivityTextPreviewBinding
import com.lollipop.wear.basic.doAsync
import com.lollipop.wear.basic.onUI
import java.io.ByteArrayOutputStream
import java.io.InputStream

class TextPreviewActivity : FileBasicActivity() {

    private val binding by lazy {
        ActivityTextPreviewBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        readInfo {
            binding.textView.text = it
        }
    }

    private fun readInfo(callback: (info: String) -> Unit) {
        doAsync {
            var inputStream: InputStream? = null
            try {
                inputStream = optFileStream()
                val outputStream = ByteArrayOutputStream()
                val buffer = ByteArray(4096)
                val length = inputStream.read(buffer)
                if (length > 0) {
                    outputStream.write(buffer, 0, length)
                }
                outputStream.flush()
                val info = outputStream.toString()
                onUI { callback(info) }
            } catch (e: Throwable) {
                Log.e("TextPreviewActivity", "readInfo: ", e)
            } finally {
                try {
                    inputStream?.close()
                } catch (_: Throwable) {
                }
            }
        }
    }
}