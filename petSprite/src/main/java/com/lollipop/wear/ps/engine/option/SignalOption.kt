package com.lollipop.wear.ps.engine.option

import com.lollipop.wear.ps.engine.state.GameOption

/**
 * 单纯的信号，同步一次更新而已
 */
object SignalOption : GameOption {
    override val key: String = ""
    override val name: Int = 0
}