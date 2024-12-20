package com.lollipop.wear.ps.engine.state.impl

import com.lollipop.wear.ps.R
import com.lollipop.wear.ps.engine.state.GameSomeThings
import com.lollipop.wear.ps.engine.state.IntGameState
import com.lollipop.wear.ps.engine.state.type.Money

/**
 * 财富的状态对象
 */
object RichState : IntGameState() {

    override val key: String = "Rich"
    override val name: Int = R.string.label_state_rich

    override val maxValue: Int = Int.MAX_VALUE

    override val minValue: Int = 0

    override fun onOption(things: GameSomeThings) {
        val option = things.option
        if (option is Money) {
            putValue(option.amount)
        }
    }

    fun use(amount: Int) {
        putValue(-amount)
    }

}