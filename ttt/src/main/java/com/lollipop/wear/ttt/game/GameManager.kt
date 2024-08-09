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
object GameManager : GameBoardProvider, GameControl {

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

    private val stateListener = ListenerManager<GameControl.StateListener>()

    override fun snapshot(): GameBoardSnapshot {
        return current.snapshot()
    }

    override fun newGame() {
        current = GameBoard()
        currentHand = firstHand
        stateListener.invoke {
            it.onGameStart()
            it.onCurrentHandChanged()
        }
    }

    override fun addListener(listener: GameControl.StateListener) {
        stateListener.add(listener)
    }

    override fun removeListener(listener: GameControl.StateListener) {
        stateListener.remove(listener)
    }

    fun currentPiece(): GamePiece {
        return if (currentHand == firstHand) {
            GamePiece.X
        } else {
            GamePiece.O
        }
    }

    override fun put(x: Int, y: Int, piece: GamePiece) {
        if (current.get(x, y) != GamePiece.Empty) {
            return
        }
        current.put(x, y, piece)
        when (checkWinner()) {
            GameReferee.Result.WinnerO -> {
                // 后手获胜
                stateListener.invoke { it.onGameEnd(rearHand) }
            }

            GameReferee.Result.WinnerX -> {
                // 先手获胜
                stateListener.invoke { it.onGameEnd(firstHand) }
            }

            GameReferee.Result.Draw -> {
                // 平局，没有赢家
                stateListener.invoke { it.onGameEnd(null) }
            }

            GameReferee.Result.Continue -> {
                // 没有结束，继续
                nextHand()
            }
        }
    }

    private fun checkWinner(): GameReferee.Result {
        return GameReferee.checkWinner(current)
    }

    override fun switchHand() {
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

    override fun randomFirstHand(playerA: GamePlayer, playerB: GamePlayer) {
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

}


//fun main() {
//    val game = GameManager
//    var gameLive = true
//    game.addListener(object : GameControl.StateListener {
//        override fun onGameStart() {
//            println("游戏开始")
//        }
//
//        override fun onGameEnd(winner: GamePlayer?) {
//            gameLive = false
//            println("游戏结束，${winner?.name ?: "平局"}")
//        }
//
//        override fun onCurrentHandChanged() {
//            println("当前手：${game.currentHand.name}")
//        }
//
//        override fun onPlayerChanged() {
//            println("玩家切换：${game.firstHand.name} vs ${game.rearHand.name}")
//        }
//    })
//    while (gameLive) {
//        print(game.current)
//        val self = game.currentPiece()
//        when (val result = GameRobot.getResult(game.current, self)) {
//            GameRobot.Result.Error -> {
//                println("Robot Error")
//                break
//            }
//
//            is GameRobot.Result.Success -> {
//                game.put(result.x, result.y, self)
//            }
//        }
//    }
//}
//
//private fun print(game: GameBoard) {
//    println("-------------")
//    for (y in 0 until 3) {
//        print("| ")
//        for (x in 0 until 3) {
//            val piece = game.get(x, y)
//            if (piece == GamePiece.Empty) {
//                print(" ")
//            } else {
//                print(piece.name)
//            }
//            print(" | ")
//        }
//        println("")
//    }
//    println("-------------")
//}
