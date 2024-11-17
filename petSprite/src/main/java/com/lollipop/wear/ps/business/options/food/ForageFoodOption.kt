package com.lollipop.wear.ps.business.options.food

import com.lollipop.wear.ps.R
import com.lollipop.wear.ps.engine.state.BackpackItem
import com.lollipop.wear.ps.engine.state.GameOption
import com.lollipop.wear.ps.engine.state.type.Food

object ForageFoodOption : GameOption, Food, BackpackItem {
    override val key: String = "food_forage"
    override val name: Int = R.string.food_forage
    override val kcal: Int = 30
}