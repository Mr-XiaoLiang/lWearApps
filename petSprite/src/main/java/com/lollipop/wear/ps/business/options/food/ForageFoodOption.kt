package com.lollipop.wear.ps.business.options.food

import android.content.Context
import com.lollipop.wear.ps.R
import com.lollipop.wear.ps.engine.state.GameOption
import com.lollipop.wear.ps.engine.state.type.Food
import com.lollipop.wear.ps.engine.state.type.Goods

object ForageFoodOption : GameOption, Food, Goods {
    override val key: String = "food_forage"
    override val name: Int = R.string.food_forage

    override fun getLogInfo(context: Context): String {
        TODO("Not yet implemented")
    }

    override val kcal: Int
        get() = TODO("Not yet implemented")
}