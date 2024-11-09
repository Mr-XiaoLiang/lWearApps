package com.lollipop.wear.ps.engine.state

fun interface GameOptionFilter {

    fun filter(option: GameOption): GameOption

}