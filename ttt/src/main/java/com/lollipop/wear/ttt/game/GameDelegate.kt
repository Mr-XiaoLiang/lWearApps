package com.lollipop.wear.ttt.game

class GameDelegate(
    val onStateChanged: (GameState) -> Unit
) {

    var mode: Mode = Mode.Unknown
        private set

    var playerA: GamePlayer? = null
        private set
    var playerB: GamePlayer? = null
        private set

    var state: GameState = GameState.Idle
        private set

    fun start() {
        if (state == GameState.Pause || state == GameState.Ready) {
            changeState(GameState.Running)
        }
    }

    fun onClick(x: Int, y: Int) {
        if (state != GameState.Running) {
            return
        }
        val currentHand = GameManager.currentHand
        if (currentHand == GamePlayer.Robot) {
            return
        }
        if (GameManager.current.get(x, y) != GamePiece.Empty) {
            return
        }
        GameManager.put(x, y, GameManager.currentPiece())
    }

    fun onResume() {
        changeState(state)
    }

    fun onPause() {
        changeState(GameState.Pause)
    }

    private fun changeState(newState: GameState) {
        state = newState
        onStateChanged(newState)
    }

    fun isActive(): Boolean {
        return playerA != null && playerB != null
    }

    fun reset() {
        playerA = null
        playerB = null
        changeState(GameState.Idle)
    }

    fun addPlayer(player: GamePlayer) {
        if (player == GamePlayer.Robot) {
            // 有机器人了，就不能要机器人了
            if (playerA == GamePlayer.Robot || playerB == GamePlayer.Robot) {
                changeState(GameState.Idle)
                return
            }
        }
        if (playerA == null) {
            playerA = player
        } else if (playerB == null) {
            playerB = player
        }
        val a = playerA
        val b = playerB
        if (a == null || b == null) {
            changeState(GameState.Idle)
            return
        }
        if (a == b && a == GamePlayer.Robot) {
            playerB = null
            changeState(GameState.Idle)
            return
        }
        mode = if (a == GamePlayer.Robot || b == GamePlayer.Robot) {
            Mode.HumanVsRobot
        } else {
            Mode.HumanVsHuman
        }
        GameManager.randomFirstHand(a, b)
        changeState(GameState.Ready)
    }



    enum class Mode {
        HumanVsHuman,
        HumanVsRobot,
        Unknown
    }

}

enum class GameState {
    Idle,
    Ready,
    Pause,
    Running,
}
