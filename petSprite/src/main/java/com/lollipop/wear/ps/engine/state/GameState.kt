package com.lollipop.wear.ps.engine.state

import org.json.JSONObject

interface GameState {

    val key: String

    fun onOption(option: GameOption)

    fun parse(json: JSONObject)

    fun save(json: JSONObject)

}

abstract class IntGameState : GameState {

    open val maxValue: Int = Int.MAX_VALUE

    open val minValue: Int = 0

    var current: Int = 0
        protected set

    protected fun putValue(newValue: Int) {
        current += newValue
        if (current > maxValue) {
            current = minValue
        }
        if (current < minValue) {
            current = maxValue
        }
    }

}

abstract class FloatGameState : GameState {

    open val maxValue: Float = Float.MAX_VALUE

    open val minValue: Float = 0F

    var current: Float = 0F
        protected set

    protected fun putValue(newValue: Float) {
        current += newValue
        if (current > maxValue) {
            current = minValue
        }
        if (current < minValue) {
            current = maxValue
        }
    }

}
