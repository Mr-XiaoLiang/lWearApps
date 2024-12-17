package com.lollipop.wear.ps.engine.log

import android.content.Context
import android.util.Log
import com.lollipop.wear.ps.engine.option.SignalOption
import com.lollipop.wear.ps.engine.state.GameSomeThings
import com.lollipop.wear.ps.engine.state.GameStateManager
import java.util.concurrent.Executors

class GameLogDelegate(private val context: Context) : GameStateManager.OnOptionListener {

    private val logStore by lazy {
        GameLogStore(context)
    }

    private val executor by lazy {
        Executors.newSingleThreadExecutor()
    }

    override fun onOption(things: GameSomeThings) {
        try {
            if (things.option is SignalOption) {
                return
            }
            val what = context.getString(things.action.resId, context.getString(things.option.name))
            val reason = if (things.reason.display != 0) {
                context.getString(things.reason.display)
            } else {
                ""
            }
            putCurrentLog(
                what = what,
                option = things.option::class.java.simpleName,
                reason = reason
            )
        } catch (e: Throwable) {
            Log.e("GameLogDelegate", "onOption error", e)
        }
    }

    private fun getProtagonist(): String {
        return GameStateManager.currentSprite
    }

    private fun putCurrentLog(what: String, option: String, reason: String) {
        putLog(getProtagonist(), what, option, reason)
    }

    private fun putLog(who: String, what: String, option: String, reason: String) {
        executor.execute {
            try {
                logStore.addLog(who, what, option, reason)
            } catch (e: Throwable) {
                Log.e("GameLogDelegate", "putLog error", e)
            }
        }
    }

}