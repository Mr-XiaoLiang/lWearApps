package com.lollipop.wear.ps.business.event

import com.lollipop.wear.ps.business.options.Toys
import com.lollipop.wear.ps.engine.GameController
import com.lollipop.wear.ps.engine.state.GameOptionAction
import com.lollipop.wear.ps.engine.state.GameOptionReason
import com.lollipop.wear.ps.engine.state.GameSomeThings

/**
 * 玩耍相关的事件
 */
object ToysEventProvider : GameController.SimpleRandomThingsProvider(GameController.WEIGHT_HIGH) {

    override fun getThings(): GameSomeThings? {
        return randomThings(Toys.options, GameOptionReason.Lucky, GameOptionAction.GOT)
    }
}