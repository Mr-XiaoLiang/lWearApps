package com.lollipop.wear.ps.business.event

import android.util.Log
import com.lollipop.wear.ps.business.options.Lucky
import com.lollipop.wear.ps.engine.GameController
import com.lollipop.wear.ps.engine.state.GameOptionAction
import com.lollipop.wear.ps.engine.state.GameOptionReason
import com.lollipop.wear.ps.engine.state.GameSomeThings
import kotlin.random.Random

/**
 * 幸运相关的事件
 */
object LuckyEventProvider : GameController.RandomThingsProvider {

    override val weight: Int = 100

    override fun getThings(): GameSomeThings? {
        try {
            val options = Lucky.options
            val size = options.size
            if (size < 1) {
                return null
            }
            val nextInt = Random.nextInt(size)
            return GameSomeThings(
                GameOptionReason.Lucky,
                GameOptionAction.GOT,
                options[nextInt]
            )
        } catch (e: Exception) {
            Log.e("LuckyEventProvider", "getThings: ", e)
        }
        return null
    }
}