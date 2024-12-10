package com.lollipop.wear.ps.engine.state.impl

import com.lollipop.wear.ps.R
import com.lollipop.wear.ps.engine.state.GameSomeThings
import com.lollipop.wear.ps.engine.state.IntGameState
import com.lollipop.wear.ps.engine.state.type.Food

/**
 * 饱腹感的状态对象
 */
object SatiationState : IntGameState() {

    override val key: String = "Satiation"
    override val name: Int = R.string.label_state_satiation

    override val maxValue: Int = 100

    override val minValue: Int = 0

    override fun onOption(things: GameSomeThings) {
        val option = things.option
        if (option is Food) {
            putValue(option.kcal)
        }
    }

}