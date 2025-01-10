package com.lollipop.filebrowser.page

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lollipop.filebrowser.databinding.ActivityFtpServiceBinding

class FtpServiceActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityFtpServiceBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

}