package com.lollipop.wear.ps.business.event

import com.lollipop.wear.ps.engine.GameController
import com.lollipop.wear.ps.engine.state.GameSomeThings
import com.lollipop.wear.ps.engine.state.GameState
import com.lollipop.wear.ps.engine.state.IntGameState
import com.lollipop.wear.ps.engine.state.impl.HealthState
import com.lollipop.wear.ps.engine.state.impl.MoodState
import com.lollipop.wear.ps.engine.state.impl.SatiationState

object FoodsEventProvider : GameController.RandomThingsProvider {

    override val weight: Int = 100

    private val stateArray by lazy {
        arrayOf(HealthState, MoodState, SatiationState)
    }

    override fun getThings(): GameSomeThings? {
        val state = findFocusState() ?: return null
        when (state) {
            HealthState -> {
                TODO()
            }

            MoodState -> {
                TODO()
            }

            SatiationState -> {
                TODO()
            }
        }
        return null
    }

    private fun findFocusState(): GameState? {
        return findMinState(stateArray)
    }

    private fun findMinState(array: Array<IntGameState>): GameState? {
        if (array.isEmpty()) {
            return null
        }
        var min = array[0]
        for (i in 1 until array.size) {
            val state = array[i]
            if (state.current < min.current) {
                min = state
            }
        }
        return min
    }

}