package com.lollipop.wear.ps.engine.state

import androidx.collection.ArraySet
import com.lollipop.wear.basic.ListenerManager
import com.lollipop.wear.basic.doAsync
import com.lollipop.wear.basic.onUI
import com.lollipop.wear.ps.engine.option.SignalOption
import com.lollipop.wear.ps.utils.BasicDataManager
import org.json.JSONObject

object GameStateManager : BasicDataManager("PS_State.lf") {

    val stateList = ArraySet<GameState>()

    var currentSprite: String = ""
        private set

    private val optionListenerList = ListenerManager<OnOptionListener>()
    private val optionFilterList = ListenerManager<GameOptionFilter>()

    inline fun <reified T : GameState> getState(): GameState? {
        stateList.forEach {
            if (it is T) {
                return it
            }
        }
        return null
    }

    override fun parseData(json: JSONObject) {
        super.parseData(json)
        doAsync {
            stateList.forEach { state ->
                val obj = json.optJSONObject(state.key) ?: JSONObject()
                state.parse(obj)
            }
            onUI {
                // 更新数据之后，发起一次刷新
                onOption(GameSomeThings(GameOptionReason.None, GameOptionAction.DONE, SignalOption))
            }
        }
    }

    override fun saveData(out: JSONObject) {
        super.saveData(out)
        stateList.forEach { state ->
            val obj = JSONObject()
            state.save(obj)
            out.put(state.key, obj)
        }
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

    fun addOptionFilter(filter: GameOptionFilter) {
        optionFilterList.add(filter)
    }

    fun removeOptionFilter(filter: GameOptionFilter) {
        optionFilterList.remove(filter)
    }

    private fun filterOption(things: GameSomeThings): GameSomeThings {
        var resultOption = things
        optionFilterList.invoke {
            resultOption = it.filter(resultOption)
        }
        return resultOption
    }

    fun onOption(things: GameSomeThings) {
        val realOption = filterOption(things)
        stateList.forEach {
            it.onOption(realOption)
        }
        optionListenerList.invoke { it.onOption(realOption) }
    }

    fun interface OnOptionListener {
        fun onOption(things: GameSomeThings)
    }

}