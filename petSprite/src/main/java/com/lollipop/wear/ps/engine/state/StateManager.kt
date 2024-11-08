package com.lollipop.wear.ps.engine.state

import androidx.collection.ArraySet
import com.lollipop.wear.basic.ListenerManager
import org.json.JSONObject

object StateManager {

    val stateList = ArraySet<GameState>()

    private val optionListenerList = ListenerManager<OnOptionListener>()

    inline fun <reified T : GameState> getState(): GameState? {
        stateList.forEach {
            if (it is T) {
                return it
            }
        }
        return null
    }

    fun register(state: GameState) {
        stateList.add(state)
    }

    fun addOptionListener(listener: OnOptionListener) {
        optionListenerList.add(listener)
    }

    fun removeOptionListener(listener: OnOptionListener) {
        optionListenerList.remove(listener)
    }

    fun onOption(option: GameOption) {
        stateList.forEach {
            it.onOption(option)
        }
        optionListenerList.invoke { it.onOption(option) }
    }

    fun parse(json: JSONObject) {
        stateList.forEach { state ->
            val obj = json.optJSONObject(state.key) ?: JSONObject()
            state.parse(obj)
        }
    }

    fun save(json: JSONObject) {
        stateList.forEach { state ->
            val obj = JSONObject()
            state.save(obj)
            json.put(state.key, obj)
        }
    }

    fun interface OnOptionListener {
        fun onOption(option: GameOption)
    }

}