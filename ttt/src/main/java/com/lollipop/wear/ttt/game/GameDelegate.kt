package com.lollipop.wear.ttt.game

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log

class GameDelegate(
    val context: Context,
    val onStateChanged: (GameState) -> Unit
) {

    companion object {
        private const val KEY_MAX_SCORE = "max_score"
        private const val KEY_A_SCORE = "a_score"
        private const val KEY_B_SCORE = "b_score"
        private const val KEY_A_PLAYER = "a_player"
        private const val KEY_B_PLAYER = "b_player"

        private const val KEY_HUMAN_A_SCORE = "HUMAN_A_SCORE"
        private const val KEY_HUMAN_B_SCORE = "HUMAN_B_SCORE"
        private const val KEY_ROBOT_SCORE = "ROBOT_SCORE"

        private val mainHandler by lazy {
            Handler(Looper.getMainLooper())
        }
        private val asyncThread by lazy {
            HandlerThread("TTT_Game_Async")
        }
        private val asyncHandler by lazy {
            asyncThread.start()
            Handler(asyncThread.looper)
        }
    }

    var mode: Mode = Mode.Unknown
        private set

    var playerA: GamePlayer? = null
        private set
    var playerB: GamePlayer? = null
        private set

    var state: GameState = GameState.Idle
        private set

    var winner: GamePlayer? = null
        private set

    var maxScore = 5
        private set

    var playerAScore = 0
        private set

    var playerBScore = 0
        private set

    var humanAScoreHistory = 0
        private set

    var humanBScoreHistory = 0
        private set

    var robotScoreHistory = 0
        private set

    private val robotPlayer = RobotPlayer(::getCurrentBoard, ::getCurrentPiece, ::onRobotPut)

    fun init() {
        getPreferences().apply {
            maxScore = getInt(KEY_MAX_SCORE, 5)
            playerA = getPreferencesPlayer(KEY_A_PLAYER)
            playerB = getPreferencesPlayer(KEY_B_PLAYER)
            playerAScore = getInt(KEY_A_SCORE, 0)
            playerBScore = getInt(KEY_B_SCORE, 0)
            humanAScoreHistory = getInt(KEY_HUMAN_A_SCORE, 0)
            humanBScoreHistory = getInt(KEY_HUMAN_B_SCORE, 0)
            robotScoreHistory = getInt(KEY_ROBOT_SCORE, 0)
        }
        checkPlayer()
    }

    private fun SharedPreferences.getPreferencesPlayer(key: String): GamePlayer? {
        val string = getString(key, "")
        GamePlayer.entries.find { it.name == string }?.let { return it }
        return null
    }

    private fun getPreferences(): SharedPreferences {
        return context.getSharedPreferences("TTT_Game", Context.MODE_PRIVATE)
    }

    private fun savePreferences() {
        getPreferences().edit()
            .putInt(KEY_MAX_SCORE, maxScore)
            .putString(KEY_A_PLAYER, playerA?.name ?: "")
            .putString(KEY_B_PLAYER, playerB?.name ?: "")
            .putInt(KEY_A_SCORE, playerAScore)
            .putInt(KEY_B_SCORE, playerBScore)
            .putInt(KEY_HUMAN_A_SCORE, humanAScoreHistory)
            .putInt(KEY_HUMAN_B_SCORE, humanBScoreHistory)
            .putInt(KEY_ROBOT_SCORE, robotScoreHistory)
            .apply()
    }

    private fun getCurrentBoard(): GameBoard {
        return GameManager.current
    }

    private fun getCurrentPiece(): GamePiece {
        return GameManager.currentPiece()
    }

    private fun log(value: String) {
        Log.d("TTT_Game", value)
    }

    private fun onRobotPut(result: GameRobot.Result) {
        log("Robot Put: $result")
        mainHandler.post {
            when (result) {
                GameRobot.Result.Error -> {
                    end(null)
                }

                is GameRobot.Result.Success -> {
                    onRobotPut(result.x, result.y)
                }
            }
        }
    }

    fun onCurrentHandChanged() {
        log("onCurrentHandChanged: ${GameManager.currentHand}")
        if (GameManager.currentHand == GamePlayer.Robot) {
            asyncHandler.removeCallbacks(robotPlayer)
            asyncHandler.postDelayed(robotPlayer, 1000L)
        }
    }

    fun start() {
        log("start: ${state}")
        if (state == GameState.Pause || state == GameState.Ready) {
            changeState(GameState.Running)
            onCurrentHandChanged()
        }
    }

    fun end(winner: GamePlayer?) {
        this.winner = winner
        saveWinnerScore(winner)
        if (state == GameState.Running) {
            changeState(GameState.Pause)
        }
    }

    private fun saveWinnerScore(winner: GamePlayer?) {
        winner ?: return
        if (winner == playerA) {
            playerAScore++
        } else if (winner == playerB) {
            playerBScore++
        }
        when (winner) {
            GamePlayer.HumanA -> {
                humanAScoreHistory++
            }

            GamePlayer.HumanB -> {
                humanBScoreHistory++
            }

            GamePlayer.Robot -> {
                robotScoreHistory++
            }
        }
        savePreferences()
    }

    fun getScore(): PlayerScore? {
        val a = playerA ?: return null
        val b = playerB ?: return null
        return PlayerScore(a, b, maxScore, playerAScore, playerBScore)
    }

    fun changeMaxScore(score: Int) {
        maxScore = score
        savePreferences()
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

    private fun onRobotPut(x: Int, y: Int) {
        if (GameManager.currentHand != GamePlayer.Robot) {
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
        savePreferences()
    }

    private fun changeState(newState: GameState) {
        log("changeState: ${newState}")
        state = newState
        onStateChanged(newState)
    }

    fun isActive(): Boolean {
        return playerA != null && playerB != null
    }

    fun reset() {
        playerA = null
        playerB = null
        playerAScore = 0
        playerBScore = 0
        changeState(GameState.Idle)
        savePreferences()
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
        checkPlayer()
    }

    private fun checkPlayer() {
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

    private class RobotPlayer(
        val board: () -> GameBoard,
        val piece: () -> GamePiece,
        val onPut: (GameRobot.Result) -> Unit
    ) : Runnable {
        override fun run() {
            val result = GameRobot.getResult(board(), piece())
            onPut(result)
        }
    }
}

enum class GameState {
    Idle,
    Ready,
    Pause,
    Running,
}
