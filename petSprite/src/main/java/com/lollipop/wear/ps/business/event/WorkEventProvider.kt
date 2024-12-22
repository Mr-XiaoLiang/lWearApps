package com.lollipop.wear.ps.business.event

import com.lollipop.wear.ps.business.options.Works
import com.lollipop.wear.ps.engine.GameController
import com.lollipop.wear.ps.engine.state.GameOptionAction
import com.lollipop.wear.ps.engine.state.GameOptionReason
import com.lollipop.wear.ps.engine.state.GameSomeThings

/**
 * 工作相关的事件
 */
object WorkEventProvider : GameController.SimpleRandomThingsProvider(GameController.WEIGHT_HIGH) {

    override fun getThings(): GameSomeThings? {
        return randomThings(Works.options, GameOptionReason.None, GameOptionAction.DONE)
    }

}