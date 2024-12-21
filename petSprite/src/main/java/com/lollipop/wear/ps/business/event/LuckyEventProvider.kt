package com.lollipop.wear.ps.business.event

import android.util.Log
import com.lollipop.wear.ps.business.options.Lucky
import com.lollipop.wear.ps.engine.GameController
import com.lollipop.wear.ps.engine.state.GameOptionAction
import com.lollipop.wear.ps.engine.state.GameOptionReason
import com.lollipop.wear.ps.engine.state.GameSomeThings

/**
 * 幸运相关的事件
 */
object LuckyEventProvider : GameController.SimpleRandomThingsProvider(GameController.WEIGHT_HIGH) {

    override fun getThings(): GameSomeThings? {
        return randomThings(
            Lucky.options,
            GameOptionReason.Lucky,
            GameOptionAction.GOT)
    }
}