package com.lollipop.filebrowser.page

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lollipop.filebrowser.databinding.ActivityFileDetailBinding
import java.io.File

class FileDetailActivity : AppCompatActivity() {

    companion object {

        private const val PARAMS_TARGET_FILE = "target_file"

        fun open(context: Context, file: File) {
            context.startActivity(Intent(context, FileDetailActivity::class.java).apply {
                putExtra(PARAMS_TARGET_FILE, file.path)
            })
        }
    }

    private val binding by lazy {
        ActivityFileDetailBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }


}