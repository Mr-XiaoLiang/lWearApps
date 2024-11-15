package com.lollipop.wear.ps.engine.state

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.collection.ArraySet
import com.lollipop.wear.basic.ListenerManager
import com.lollipop.wear.data.FileHelper
import com.lollipop.wear.ps.utils.doAsync
import com.lollipop.wear.ps.utils.onUI
import com.lollipop.wear.ps.engine.option.SignalOption
import org.json.JSONObject
import java.io.File

object StateManager {

    private const val STATE_FILE = "PS_State.lf"

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

    fun initState(app: Application) {
        doAsync {
            when (val result = FileHelper.readJson(File(app.filesDir, STATE_FILE))) {
                is FileHelper.FileResult.Success -> {
                    parse(result.data)
                    onUI {
                        onOption(SignalOption)
                    }
                }

                is FileHelper.FileResult.Failure -> {
                    parse(JSONObject())
                    onUI {
                        onOption(SignalOption)
                    }
                    Log.e("StateManager", "initState error", result.error)
                }
            }
        }
    }

    fun saveState(context: Context) {
        val app = context.applicationContext
        doAsync {
            val json = JSONObject()
            save(json)
            val result = FileHelper.write(File(app.filesDir, STATE_FILE), json)
            if (result is FileHelper.FileResult.Failure) {
                Log.e("StateManager", "saveState error", result.error)
            }
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

    private fun filterOption(option: GameOption): GameOption {
        var resultOption = option
        optionFilterList.invoke {
            resultOption = it.filter(resultOption)
        }
        return resultOption
    }

    fun onOption(option: GameOption) {
        val realOption = filterOption(option)
        stateList.forEach {
            it.onOption(realOption)
        }
        optionListenerList.invoke { it.onOption(realOption) }
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