package com.lollipop.wear.ps.business.event

import com.lollipop.wear.ps.engine.GameController
import com.lollipop.wear.ps.engine.state.GameSomeThings

/**
 * 工作相关的事件
 */
object WorkEventProvider : GameController.SimpleRandomThingsProvider(GameController.WEIGHT_HIGH) {

    override fun getThings(): GameSomeThings? {
        return null
    }
}