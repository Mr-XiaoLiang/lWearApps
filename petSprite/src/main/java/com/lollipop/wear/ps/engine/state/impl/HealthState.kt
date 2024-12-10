package com.lollipop.wear.ps.engine.state.impl

import com.lollipop.wear.ps.R
import com.lollipop.wear.ps.engine.state.GameSomeThings
import com.lollipop.wear.ps.engine.state.IntGameState
import com.lollipop.wear.ps.engine.state.type.Antibiotic

/**
 * 健康的状态对象
 */
object HealthState : IntGameState() {

    override val key: String = "Health"

    override val name: Int = R.string.label_state_health

    override val maxValue: Int = 100

    override val minValue: Int = 0

    override fun onOption(things: GameSomeThings) {
        val option = things.option
        if (option is Antibiotic) {
            putValue(option.antibody)
        }
    }

}