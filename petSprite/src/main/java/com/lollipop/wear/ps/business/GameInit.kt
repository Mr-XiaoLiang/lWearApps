package com.lollipop.wear.ps.business

import android.app.Application
import com.lollipop.wear.ps.engine.attr.AttributeManager
import com.lollipop.wear.ps.engine.log.GameLogDelegate
import com.lollipop.wear.ps.engine.state.BackpackManager
import com.lollipop.wear.ps.engine.state.StateManager
import com.lollipop.wear.ps.engine.state.impl.HealthState
import com.lollipop.wear.ps.engine.state.impl.MoodState
import com.lollipop.wear.ps.engine.state.impl.RichState
import com.lollipop.wear.ps.engine.state.impl.SatiationState

object GameInit {

    private var isInit = false

    fun init(app: Application) {
        if (isInit) {
            return
        }
        isInit = true
        registerState()
        StateManager.init(app)
        BackpackManager.init(app)
        AttributeManager.init(app)
        StateManager.addOptionListener(GameLogDelegate(app))
    }

    private fun registerState() {
        StateManager.apply {
            register(RichState)
            register(HealthState)
            register(MoodState)
            register(SatiationState)
        }
    }

}