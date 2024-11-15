package com.lollipop.wear.ps.business.options

import com.lollipop.wear.ps.engine.state.GameOption

interface OptionList {

    fun getOptionList(): List<GameOption>

}