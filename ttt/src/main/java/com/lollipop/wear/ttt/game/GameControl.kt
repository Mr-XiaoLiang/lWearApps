package com.lollipop.wear.ttt.game


interface GameControl {

    fun newGame()

    fun put(x: Int, y: Int, piece: GamePiece)

    fun randomFirstHand(playerA: GamePlayer, playerB: GamePlayer)

    fun switchHand()

    fun addListener(listener: StateListener)

    fun removeListener(listener: StateListener)

    interface StateListener {
        fun onGameStart()
        fun onGameEnd(winner: GamePlayer?)
        fun onPlayerChanged()
        fun onCurrentHandChanged()
    }

}