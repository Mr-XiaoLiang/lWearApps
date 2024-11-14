package com.lollipop.wear.ps.engine.log

import android.content.Context
import android.util.Log
import com.lollipop.wear.ps.engine.option.SignalOption
import com.lollipop.wear.ps.engine.state.GameOption
import com.lollipop.wear.ps.engine.state.StateManager
import java.util.concurrent.Executors

class GameLogDelegate(private val context: Context) : StateManager.OnOptionListener {

    private val logStore by lazy {
        GameLogStore(context)
    }

    private val executor by lazy {
        Executors.newSingleThreadExecutor()
    }

    override fun onOption(option: GameOption) {
        try {
            if (option is SignalOption) {
                return
            }
            putCurrentLog(what = option.getLogInfo(context), option = option::class.java.simpleName)
        } catch (e: Throwable) {
            Log.e("GameLogDelegate", "onOption error", e)
        }
    }

    private fun getProtagonist(): String {
        return StateManager.currentSprite
    }

    private fun putCurrentLog(what: String, option: String) {
        putLog(getProtagonist(), what, option)
    }

    private fun putLog(who: String, what: String, option: String) {
        executor.execute {
            try {
                logStore.addLog(who, what, option)
            } catch (e: Throwable) {
                Log.e("GameLogDelegate", "putLog error", e)
            }
        }
    }

}