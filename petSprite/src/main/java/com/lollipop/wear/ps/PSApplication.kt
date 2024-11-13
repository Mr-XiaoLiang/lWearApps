package com.lollipop.wear.ps

import android.app.Application
import com.lollipop.wear.ps.business.GameInit

class PSApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        GameInit.init(this)
    }

}