package com.lollipop.wear.ps.engine

import android.content.Context
import com.lollipop.wear.ps.engine.state.GameSomeThings
import com.lollipop.wear.ps.engine.state.GameStateManager
import com.lollipop.wear.utils.PreferencesDelegate

object GameController {

    private var currentGameTime = 0L

    private const val TIME_NONE = 0L

    /**
     * 默认10分钟一件事
     */
    private const val TIME_STEP = 1000L * 60 * 10

    private var pref: ControllerPreferences? = null

    fun timeSync() {
        val now = System.currentTimeMillis()
        while (currentGameTime < now) {
            // TODO
        }
    }

    fun init(context: Context) {
        val preferences = getPreferences(context)
        val lastTime = preferences.lastTime
        if (lastTime == TIME_NONE) {
            val now = System.currentTimeMillis()
            preferences.lastTime = now
            currentGameTime = now
        } else {
            currentGameTime = lastTime
        }
    }

    private fun postOption(things: GameSomeThings) {
        GameStateManager.onOption(things.action, things.option)
    }

    private fun getPreferences(context: Context): ControllerPreferences {
        return pref ?: ControllerPreferences(context).also {
            pref = it
        }
    }

    private class ControllerPreferences(
        context: Context
    ) : PreferencesDelegate(getPreferences(context, "GameController")) {

        var lastTime by long(TIME_NONE)

    }

}