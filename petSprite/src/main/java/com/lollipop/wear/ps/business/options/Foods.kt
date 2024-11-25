package com.lollipop.wear.ps.business.options

import com.lollipop.wear.ps.R
import com.lollipop.wear.ps.engine.state.BackpackItem
import com.lollipop.wear.ps.engine.state.GameOption
import com.lollipop.wear.ps.engine.state.type.Food

object Foods : OptionList {

    private val options = listOf<GameOption>(
        Cookie,
        Grape
    )

    override fun getOptionList(): List<GameOption> {
        return options
    }

    object Cookie : GameOption, Food, BackpackItem {
        override val key: String = "food_cookie"
        override val name: Int = R.string.food_cookie
        override val kcal: Int = 5
    }

    object Grape : GameOption, Food, BackpackItem {
        override val key: String = "food_grape"
        override val name: Int = R.string.food_grape
        override val kcal: Int = 5
    }

}