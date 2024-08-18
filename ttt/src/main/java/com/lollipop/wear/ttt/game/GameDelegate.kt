package com.lollipop.wear.ttt.game

import android.content.Context
import android.content.SharedPreferences

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

    fun init() {
        val preferences = context.getSharedPreferences("TTT_Game", Context.MODE_PRIVATE)
        maxScore = preferences.getInt(KEY_MAX_SCORE, 5)
        playerA = getPreferencesPlayer(preferences, KEY_A_PLAYER)
        playerB = getPreferencesPlayer(preferences, KEY_B_PLAYER)
        playerAScore = preferences.getInt(KEY_A_SCORE, 0)
        playerBScore = preferences.getInt(KEY_B_SCORE, 0)
    }

    private fun getPreferencesPlayer(preferences: SharedPreferences, key: String): GamePlayer? {
        val string = preferences.getString(key, "")
        GamePlayer.entries.find { it.name == string }?.let { return it }
        return null
    }

    private fun getPreferences(): SharedPreferences {
        return context.getSharedPreferences("TTT_Game", Context.MODE_PRIVATE)
    }

    private fun savePreferences() {
        val preferences = getPreferences()
        preferences.edit()
            .putInt(KEY_MAX_SCORE, maxScore)
            .putString(KEY_A_PLAYER, playerA?.name ?: "")
            .putString(KEY_B_PLAYER, playerB?.name ?: "")
            .putInt(KEY_A_SCORE, playerAScore)
            .putInt(KEY_B_SCORE, playerBScore)
            .apply()
    }

    fun start() {
        if (state == GameState.Pause || state == GameState.Ready) {
            changeState(GameState.Running)
        }
    }

    fun end(winner: GamePlayer?) {
        this.winner = winner
        if (winner != null) {
            if (winner == playerA) {
                playerAScore++
                savePreferences()
            } else if (winner == playerB) {
                playerBScore++
                savePreferences()
            }
        }
        if (state == GameState.Running) {
            changeState(GameState.Pause)
        }
    }

    fun getScore(): PlayerScore? {
        val a = playerA ?: return null
        val b = playerB ?: return null
        return PlayerScore(a, b, maxScore, playerAScore, playerBScore)
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
        savePreferences()
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
