package com.lollipop.wear.ps.engine.state

import com.lollipop.wear.ps.engine.state.impl.RichState
import com.lollipop.wear.ps.engine.state.type.Commodity
import com.lollipop.wear.ps.utils.BasicDataManager

object BackpackManager : BasicDataManager("PS_Backpack.lf"), GameStateManager.OnOptionListener {

    private const val KEY_ITEM_COUNT = "count"

    fun getItemCount(item: BackpackItem): Int {
        return getIntFromGroup(item.key, KEY_ITEM_COUNT, 0)
    }

    private fun saveItemCount(item: BackpackItem, count: Int) {
        putValueToGroup(item.key) { json, _ ->
            json.put(KEY_ITEM_COUNT, count)
        }
        save()
    }

    override fun onOption(things: GameSomeThings) {
        val option = things.option
        if (option is BackpackItem) {
            val count = getItemCount(option)
            if (count > 0) {
                saveItemCount(option, count - 1)
            } else {
                buy(option)
            }
        } else {
            buy(option)
        }
    }

    private fun buy(option: GameOption) {
        if (option is Commodity) {
            RichState.use(option.price)
        }
    }

}