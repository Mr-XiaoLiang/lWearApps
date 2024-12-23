package com.lollipop.wear.ps.business.event

import com.lollipop.wear.ps.business.options.Toys
import com.lollipop.wear.ps.engine.GameController
import com.lollipop.wear.ps.engine.state.GameOption
import com.lollipop.wear.ps.engine.state.GameOptionAction
import com.lollipop.wear.ps.engine.state.GameOptionReason
import com.lollipop.wear.ps.engine.state.GameSomeThings
import com.lollipop.wear.ps.engine.state.impl.RichState
import com.lollipop.wear.ps.engine.state.impl.SatiationState
import com.lollipop.wear.ps.engine.state.type.Food
import com.lollipop.wear.ps.engine.state.type.Money

/**
 * 玩耍相关的事件
 */
object ToysEventProvider : GameController.SimpleRandomThingsProvider(GameController.WEIGHT_HIGH) {

    override fun getThings(): GameSomeThings? {
        val srcOptions = Toys.options
        val activeOptions = ArrayList<GameOption>()
        val richValue = RichState.current
        val satiationState = SatiationState.current
        for (option in srcOptions) {
            if (option is Money) {
                val amount = option.amount
                if (amount < 0 && richValue + amount < 0) {
                    // 如果需要付费，并且余额不足，那么放弃运动
                    continue
                }
            }
            if (option is Food) {
                val kcal = option.kcal
                if (kcal < 0 && satiationState + kcal < 0) {
                    // 如果需要消耗能量，并且能量不足，那么放弃运动
                    continue
                }
            }
            activeOptions.add(option)
        }
        return randomThings(
            activeOptions.toTypedArray(),
            GameOptionReason.Lucky,
            GameOptionAction.GOT
        )
    }
}