package com.lollipop.wear.ps.engine.state

import com.lollipop.wear.ps.utils.BasicDataManager

object BackpackManager : BasicDataManager("PS_Backpack.lf") {

    private const val KEY_ITEM_COUNT = "count"

    fun getItemCount(item: BackpackItem): Int {
        return getIntFromGroup(item.name, KEY_ITEM_COUNT, 0)
    }

    fun changeItem(item: BackpackItem, count: Int = 1) {
        if (count == 0) {
            return
        }
        putValueToGroup(item.name) { json, _ ->
            json.put(KEY_ITEM_COUNT, json.optInt(KEY_ITEM_COUNT, 0) + count)
        }
    }

}