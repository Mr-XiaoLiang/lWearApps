package com.lollipop.filebrowser.page

import android.os.Bundle
import com.bumptech.glide.Glide
import com.lollipop.filebrowser.databinding.ActivityImagePreviewBinding

class ImagePreviewActivity : FileBasicActivity() {

    private val binding by lazy {
        ActivityImagePreviewBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        if (targetFile.exists()) {
            Glide.with(this).load(targetFile).into(binding.imageView)
        } else if (fileActionUri != null) {
            Glide.with(this).load(fileActionUri).into(binding.imageView)
        }
    }
}