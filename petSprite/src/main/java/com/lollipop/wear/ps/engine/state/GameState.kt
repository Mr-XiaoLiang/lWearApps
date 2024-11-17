package com.lollipop.wear.ps.engine.state

import org.json.JSONObject

interface GameState {

    val key: String

    fun onOption(things: GameSomeThings)

    fun parse(json: JSONObject)

    fun save(json: JSONObject)

}

abstract class SingleGameState : GameState {

    companion object {
        const val KEY_VALUE = "value"
    }

}

abstract class IntGameState : SingleGameState() {

    open val maxValue: Int = Int.MAX_VALUE

    open val minValue: Int = 0

    var current: Int = 0
        private set

    protected fun putValue(newValue: Int) {
        current += newValue
        if (current > maxValue) {
            current = minValue
        }
        if (current < minValue) {
            current = maxValue
        }
    }

    override fun parse(json: JSONObject) {
        current = json.optInt(KEY_VALUE, current)
    }

    override fun save(json: JSONObject) {
        json.put(KEY_VALUE, current)
    }

}

