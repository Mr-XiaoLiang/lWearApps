package com.lollipop.wear.ttt.game

class GameDelegate(
    val onStateChanged: (State) -> Unit
) {

    var mode: Mode = Mode.Unknown
        private set

    var playerA: GamePlayer? = null
        private set
    var playerB: GamePlayer? = null
        private set

    var state: State = State.Idle
        private set

    fun start() {
        if (state == State.Pause || state == State.Ready) {
            changeState(State.Running)
        }
    }

    fun onClick(x: Int, y: Int) {
        if (state != State.Running) {
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
        changeState(State.Pause)
    }

    private fun changeState(newState: State) {
        state = newState
        onStateChanged(newState)
    }

    fun isActive(): Boolean {
        return playerA != null && playerB != null
    }

    fun reset() {
        playerA = null
        playerB = null
        changeState(State.Idle)
    }

    fun addPlayer(player: GamePlayer) {
        if (player == GamePlayer.Robot) {
            // 有机器人了，就不能要机器人了
            if (playerA == GamePlayer.Robot || playerB == GamePlayer.Robot) {
                changeState(State.Idle)
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
            changeState(State.Idle)
            return
        }
        if (a == b && a == GamePlayer.Robot) {
            playerB = null
            changeState(State.Idle)
            return
        }
        mode = if (a == GamePlayer.Robot || b == GamePlayer.Robot) {
            Mode.HumanVsRobot
        } else {
            Mode.HumanVsHuman
        }
        GameManager.randomFirstHand(a, b)
        changeState(State.Ready)
    }

    enum class State {
        Idle,
        Ready,
        Pause,
        Running,
    }

    enum class Mode {
        HumanVsHuman,
        HumanVsRobot,
        Unknown
    }

}