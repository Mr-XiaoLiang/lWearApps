package com.lollipop.wear.ps.engine

import android.content.Context
import com.lollipop.wear.ps.engine.state.GameSomeThings
import com.lollipop.wear.ps.engine.state.GameStateManager
import com.lollipop.wear.utils.PreferencesDelegate
import kotlin.random.Random

object GameController {

    private var currentGameTime = 0L

    private const val TIME_NONE = 0L

    /**
     * 默认10分钟一件事
     */
    private const val TIME_STEP = 1000L * 60 * 10

    private var pref: ControllerPreferences? = null

    private val providerList = ArrayList<RandomThingsProvider>()
    private var providerWeightTotal = 0

    fun addProvider(provider: RandomThingsProvider) {
        if (provider.weight == 0) {
            return
        }
        providerList.add(provider)
        providerWeightTotal += provider.weight
    }

    fun removeProvider(provider: RandomThingsProvider) {
        providerList.remove(provider)
        providerWeightTotal -= provider.weight
    }

    fun timeSync() {
        val now = System.currentTimeMillis()
        val weightTotal = providerWeightTotal
        val providers = ArrayList<RandomThingsProvider>()
        providers.addAll(providerList)
        providers.sortBy { it.weight }
        while (currentGameTime < now) {
            currentGameTime += TIME_STEP
            val target = (Random.nextFloat() * weightTotal).toInt()
            findProvider(providers, target)?.let { provider ->
                provider.getThings()?.let { things ->
                    postOption(things)
                }
            }
        }
    }

    private fun findProvider(
        providers: List<RandomThingsProvider>,
        target: Int
    ): RandomThingsProvider? {
        var current = 0
        for (provider in providers) {
            val next = provider.weight + current
            if ((current < target) && (target <= next)) {
                return provider
            }
            current = next
        }
        return null
    }

    fun init(context: Context) {
        val preferences = getPreferences(context)
        val lastTime = preferences.lastTime
        if (lastTime == TIME_NONE) {
            val now = System.currentTimeMillis()
            preferences.lastTime = now
            currentGameTime = now
        } else {
            currentGameTime = lastTime
        }
    }

    private fun postOption(things: GameSomeThings) {
        GameStateManager.onOption(things.action, things.option)
    }

    private fun getPreferences(context: Context): ControllerPreferences {
        return pref ?: ControllerPreferences(context).also {
            pref = it
        }
    }

    private class ControllerPreferences(
        context: Context
    ) : PreferencesDelegate(getPreferences(context, "GameController")) {

        var lastTime by long(TIME_NONE)

    }

    interface RandomThingsProvider {
        val weight: Int

        fun getThings(): GameSomeThings?

    }

}