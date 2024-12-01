package com.lollipop.wear.ps.engine.state.impl

import com.lollipop.wear.ps.engine.state.GameOption
import com.lollipop.wear.ps.engine.state.GameSomeThings
import com.lollipop.wear.ps.engine.state.IntGameState
import com.lollipop.wear.ps.engine.state.type.Toy

/**
 * 心情的状态对象
 */
object MoodState : IntGameState() {

    override val key: String = "Mood"

    override val maxValue: Int = 100

    override val minValue: Int = 0

    override fun onOption(things: GameSomeThings) {
        val option = things.option
        if (option is Toy) {
            putValue(option.dopamine)
        }
    }

}