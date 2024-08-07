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

    override fun snapshot(): GameBoardSnapshot {
        return current.snapshot()
    }


}