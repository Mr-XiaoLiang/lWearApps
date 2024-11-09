package com.lollipop.wear.ps.engine.state.impl

import com.lollipop.wear.ps.engine.state.GameOption
import com.lollipop.wear.ps.engine.state.IntGameState
import com.lollipop.wear.ps.engine.state.type.Antibiotic

/**
 * 健康的状态对象
 */
object HealthState : IntGameState() {

    override val key: String = "Health"

    override val maxValue: Int = 100

    override val minValue: Int = 0

    override fun onOption(option: GameOption) {
        if (option is Antibiotic) {
            putValue(option.antibody)
        }
    }

}