package com.lollipop.wear.ps.business.options

import com.lollipop.wear.ps.engine.state.GameOption

object FoodOptions : OptionList {

    private val options = listOf<GameOption>(

    )

    override fun getOptionList(): List<GameOption> {
        return options
    }
}