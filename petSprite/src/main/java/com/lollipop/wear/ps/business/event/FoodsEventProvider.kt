package com.lollipop.wear.ps.business.event

import com.lollipop.wear.ps.business.options.Foods
import com.lollipop.wear.ps.engine.GameController
import com.lollipop.wear.ps.engine.state.BackpackItem
import com.lollipop.wear.ps.engine.state.BackpackManager
import com.lollipop.wear.ps.engine.state.GameOption
import com.lollipop.wear.ps.engine.state.GameOptionAction
import com.lollipop.wear.ps.engine.state.GameOptionReason
import com.lollipop.wear.ps.engine.state.GameSomeThings
import com.lollipop.wear.ps.engine.state.GameState
import com.lollipop.wear.ps.engine.state.IntGameState
import com.lollipop.wear.ps.engine.state.impl.HealthState
import com.lollipop.wear.ps.engine.state.impl.MoodState
import com.lollipop.wear.ps.engine.state.impl.SatiationState
import com.lollipop.wear.ps.engine.state.type.Antibiotic
import com.lollipop.wear.ps.engine.state.type.Food
import com.lollipop.wear.ps.engine.state.type.Toy

/**
 * 食物相关的事件
 */
object FoodsEventProvider : GameController.SimpleRandomThingsProvider(GameController.WEIGHT_HIGH) {

    private val stateArray by lazy {
        arrayOf(HealthState, MoodState, SatiationState)
    }

    override fun getThings(): GameSomeThings? {
        val state = findFocusState() ?: return null
        val optionList = ArrayList<GameOption>()
        optionList.addAll(Foods.options)
        var reason: GameOptionReason = GameOptionReason.None
        when (state) {
            HealthState -> {
                // 健康属性
                optionList.sortToDescending<GameOption, Antibiotic> { it.antibody }
                reason = GameOptionReason.HealthLow
            }

            MoodState -> {
                // 心情属性
                optionList.sortToDescending<GameOption, Toy> { it.dopamine }
                reason = GameOptionReason.MoodLow
            }

            SatiationState -> {
                // 饱腹属性
                optionList.sortToDescending<GameOption, Food> { it.kcal }
                reason = GameOptionReason.SatiationLow
            }

            else -> {
                return null
            }
        }
        // 背包数量
        optionList.sortToDescending<GameOption, BackpackItem> { BackpackManager.getItemCount(it) }
        val option = optionList[0]
        return if (option is Food) {
            GameSomeThings(reason, GameOptionAction.ATE, option)
        } else {
            GameSomeThings(reason, GameOptionAction.USED, option)
        }
    }

    private inline fun <T, reified R> MutableList<T>.sortTo(crossinline block: (R) -> Int) {
        val list = this
        list.sortBy { item ->
            if (item is R) {
                block(item)
            } else {
                0
            }
        }
    }

    private inline fun <T, reified R> MutableList<T>.sortToDescending(crossinline block: (R) -> Int) {
        val list = this
        list.sortByDescending { item ->
            if (item is R) {
                block(item)
            } else {
                0
            }
        }
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