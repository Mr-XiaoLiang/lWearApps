package com.lollipop.wear.ps.business.event

import com.lollipop.wear.ps.business.options.Negative
import com.lollipop.wear.ps.engine.GameController
import com.lollipop.wear.ps.engine.state.GameOptionAction
import com.lollipop.wear.ps.engine.state.GameOptionReason
import com.lollipop.wear.ps.engine.state.GameSomeThings

/**
 * 生理相关的事件
 */
object PhysiologyEventProvider : GameController.SimpleRandomThingsProvider(
    GameController.WEIGHT_MIDDLE
) {

    override fun getThings(): GameSomeThings? {
        return randomThings(Negative.options, GameOptionReason.None, GameOptionAction.SICK)
    }
}