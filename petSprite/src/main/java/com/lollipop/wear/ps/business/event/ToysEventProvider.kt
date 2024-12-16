package com.lollipop.wear.ps.business.event

import com.lollipop.wear.ps.engine.GameController
import com.lollipop.wear.ps.engine.state.GameSomeThings

/**
 * 玩耍相关的事件
 */
object ToysEventProvider : GameController.RandomThingsProvider {
    
    override val weight: Int = 100

    override fun getThings(): GameSomeThings? {
        return null
    }
}