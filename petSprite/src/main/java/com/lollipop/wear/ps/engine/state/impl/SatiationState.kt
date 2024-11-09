package com.lollipop.wear.ps.engine.state.impl

import com.lollipop.wear.ps.engine.state.GameOption
import com.lollipop.wear.ps.engine.state.IntGameState
import com.lollipop.wear.ps.engine.state.type.Food

/**
 * 饱腹感的状态对象
 */
object SatiationState : IntGameState() {

    override val key: String = "Satiation"

    override val maxValue: Int = 100

    override val minValue: Int = 0

    override fun onOption(option: GameOption) {
        if (option is Food) {
            putValue(option.kcal)
        }
    }

}