package com.lollipop.wear.ps.engine

import android.content.Context
import android.util.Log
import com.lollipop.wear.basic.doAsync
import com.lollipop.wear.basic.onUI
import com.lollipop.wear.ps.engine.state.GameOption
import com.lollipop.wear.ps.engine.state.GameOptionAction
import com.lollipop.wear.ps.engine.state.GameOptionReason
import com.lollipop.wear.ps.engine.state.GameSomeThings
import com.lollipop.wear.ps.engine.state.GameStateManager
import com.lollipop.wear.utils.PreferencesDelegate
import kotlin.random.Random

object GameController {

    const val WEIGHT_NONE = 0
    const val WEIGHT_LOW = 20
    const val WEIGHT_MIDDLE = 50
    const val WEIGHT_HIGH = 100

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

    fun findFuture(context: Context) {
        timeSync(context)
    }

    private fun timeSync(context: Context) {
        doAsync {
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
                        onUI {
                            postOption(things)
                        }
                    }
                }
            }
            updateTime(context)
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
        timeSync(context)
    }

    private fun updateTime(context: Context) {
        getPreferences(context).lastTime = System.currentTimeMillis()
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

        /**
         * 随机获取一个事件
         * 它会在子线程中执行，所以可以做耗时的操作，但是不要更新UI
         */
        fun getThings(): GameSomeThings?

    }

    abstract class SimpleRandomThingsProvider(
        override val weight: Int
    ) : RandomThingsProvider {

        protected fun randomOption(options: Array<GameOption>): GameOption? {
            try {
                val size = options.size
                if (size < 1) {
                    return null
                }
                val nextInt = Random.nextInt(size)
                return options[nextInt]
            } catch (e: Exception) {
                Log.e("SimpleRandomThingsProvider", "randomOption: ", e)
            }
            return null
        }

        protected fun randomThings(
            options: Array<GameOption>,
            reason: GameOptionReason,
            action: GameOptionAction,
        ): GameSomeThings? {
            try {
                val nextOption = randomOption(options) ?: return null
                return GameSomeThings(reason, action, nextOption)
            } catch (e: Exception) {
                Log.e("SimpleRandomThingsProvider", "randomThings: ", e)
            }
            return null
        }

    }

}