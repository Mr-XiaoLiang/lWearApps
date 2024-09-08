package com.lollipop.wear.fb

import android.app.Application
import com.lollipop.wear.game.GameConfig

class FbApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        GameConfig.gamePage = PsGamePage()
    }

}