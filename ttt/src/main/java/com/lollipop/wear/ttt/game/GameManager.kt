package com.lollipop.wear.ttt.game

import com.lollipop.wear.basic.ListenerManager

/**
 * 游戏的状态机
 * 这里设定游戏的规则
 * 1. 游戏只有两方
 * 2. 允许选择机器人作为其中一方参与游戏
 * 3. 状态机本身不包含计算逻辑，只判断输赢
 * 4. 状态机不包含任何UI逻辑
 * 5. 只有8种连线方式（横3 + 纵3 + 斜2）
 * 6. 在8条线中任意一条被填满，有且只有一种棋子的时候胜利
 * 7. 因此当8条线都存在2种棋子的时候，认为平局
 * 8. 如果不满足[6, 7]，则游戏继续
 */
object GameManager : GameBoardProvider {

    var current: GameBoard = GameBoard()
        private set

    var currentHand: GamePlayer = GamePlayer.HumanA
        private set

    /**
     * 先手为X
     */
    var firstHand: GamePlayer = GamePlayer.HumanA
        private set

    /**
     * 后手为O
     */
    var rearHand: GamePlayer = GamePlayer.HumanB
        private set

    private val stateListener = ListenerManager<StateListener>()

    override fun snapshot(): GameBoardSnapshot {
        return current.snapshot()
    }

    fun newGame() {
        current = GameBoard()
        currentHand = firstHand
        stateListener.invoke {
            it.onGameStart()
            it.onCurrentHandChanged()
        }
    }

    fun addListener(listener: StateListener) {
        stateListener.add(listener)
    }

    fun removeListener(listener: StateListener) {
        stateListener.remove(listener)
    }

    fun randomFirstHand(robot: Boolean) {
        if (robot) {
            randomFirstHand(GamePlayer.Robot, GamePlayer.HumanA)
        } else {
            randomFirstHand(GamePlayer.HumanA, GamePlayer.HumanB)
        }
    }

    fun put(x: Int, y: Int, piece: GamePiece) {
        if (current.get(x, y) != GamePiece.Empty) {
            return
        }
        current.put(x, y, piece)
        checkWinner()
    }

    private fun checkWinner() {
        // TODO

    }

    fun switchHand() {
        val player = firstHand
        firstHand = rearHand
        rearHand = player
        stateListener.invoke { it.onPlayerChanged() }
        newGame()
    }

    private fun nextHand() {
        currentHand = if (currentHand == firstHand) {
            rearHand
        } else {
            firstHand
        }
        stateListener.invoke { it.onCurrentHandChanged() }
    }

    private fun randomFirstHand(playerA: GamePlayer, playerB: GamePlayer) {
        if (Math.random() > 0.5) {
            firstHand = playerA
            rearHand = playerB
        } else {
            firstHand = playerB
            rearHand = playerA
        }
        stateListener.invoke { it.onPlayerChanged() }
        newGame()
    }

    interface StateListener {
        fun onGameStart()
        fun onGameEnd(winner: GamePlayer?)
        fun onPlayerChanged()
        fun onCurrentHandChanged()
    }

}