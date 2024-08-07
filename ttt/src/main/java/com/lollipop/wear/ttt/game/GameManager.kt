package com.lollipop.wear.ttt.game

/**
 * 游戏的状态机
 * 这里设定游戏的规则
 * 1. 游戏只有两方
 * 2. 允许选择机器人作为其中一方参与游戏
 * 3. 状态机本身不包含计算逻辑，只判断输赢
 * 4. 状态机不包含任何UI逻辑
 */
object GameManager : GameBoardProvider {

    var current: GameBoard = GameBoard()
        private set

    var firstHand: GamePlayer = GamePlayer.HumanA
        private set
    var rearHand: GamePlayer = GamePlayer.HumanB
        private set

    override fun snapshot(): GameBoardSnapshot {
        return current.snapshot()
    }

    fun randomFirstHand(robot: Boolean) {
        if (robot) {
            randomFirstHand(GamePlayer.Robot, GamePlayer.HumanA)
        } else {
            randomFirstHand(GamePlayer.HumanA, GamePlayer.HumanB)
        }
    }

    private fun randomFirstHand(playerA: GamePlayer, playerB: GamePlayer) {
        if (Math.random() > 0.5) {
            firstHand = playerA
            rearHand = playerB
        } else {
            firstHand = playerB
            rearHand = playerA
        }
    }

}