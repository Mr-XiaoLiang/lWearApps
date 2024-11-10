package com.lollipop.wear.ps

import android.app.Application
import com.lollipop.wear.data.FileHelper
import com.lollipop.wear.ps.business.GameInit
import com.lollipop.wear.ps.engine.state.StateManager

class PSApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        GameInit.init(this)
        StateManager.work {
            FileHelper.readJson()
        }
    }

}