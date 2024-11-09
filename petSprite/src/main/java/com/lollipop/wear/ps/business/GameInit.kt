package com.lollipop.wear.ps.business

import android.app.Application
import com.lollipop.wear.ps.engine.state.StateManager
import com.lollipop.wear.ps.engine.state.impl.HealthState
import com.lollipop.wear.ps.engine.state.impl.MoodState
import com.lollipop.wear.ps.engine.state.impl.RichState
import com.lollipop.wear.ps.engine.state.impl.SatiationState

object GameInit {

    fun init(app: Application) {
        StateManager.apply {
            register(RichState)
            register(HealthState)
            register(MoodState)
            register(SatiationState)
        }
    }

}