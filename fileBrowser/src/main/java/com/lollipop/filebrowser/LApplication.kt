package com.lollipop.filebrowser

import android.app.Application
import be.ppareit.swiftp.App

class LApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        App.onCreate(this)
    }

}