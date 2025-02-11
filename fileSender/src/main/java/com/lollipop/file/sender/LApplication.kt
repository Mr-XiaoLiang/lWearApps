package com.lollipop.file.sender

import android.app.Application
import com.lollipop.file.sender.ftp.FileTransferStation

class LApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        FileTransferStation.init(this)
    }

}