package com.lollipop.wear.ttt.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.lollipop.wear.ttt.R
import com.lollipop.wear.ttt.databinding.FragmentGameBoardBinding
import com.lollipop.wear.ttt.game.GameBoardProvider
import com.lollipop.wear.ttt.game.GameBoardSnapshot
import com.lollipop.wear.ttt.game.GameManager
import com.lollipop.wear.ttt.game.GamePlayer
import com.lollipop.wear.ttt.game.GameState
import com.lollipop.wear.ttt.ui.basic.SubpageFragment
import com.lollipop.wear.ttt.view.BoardView.OnPieceClickListener
import com.lollipop.wear.ttt.view.BoardView.PieceProvider
import com.lollipop.wear.ttt.view.PieceView
import com.lollipop.wear.ttt.view.PieceViewFactory

/**
 * 游戏棋盘
 */
class GameBoardFragment : SubpageFragment(),
    OnPieceClickListener,
    GameBoardProvider,
    PieceProvider {

    private val binding by lazy {
        FragmentGameBoardBinding.inflate(layoutInflater)
    }

    private var callback: Callback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = findCallback(context)
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.pausePanel.setOnClickListener { }
        binding.playerSelectPanel.setOnClickListener { }
        binding.resumeButton.setOnClickListener {
            startGame()
        }
        binding.playerAButton.setOnClickListener {
            callback?.onPlayerSelect(GamePlayer.HumanA)
        }
        binding.playerBButton.setOnClickListener {
            callback?.onPlayerSelect(GamePlayer.HumanB)
        }
        binding.robotButton.setOnClickListener {
            callback?.onPlayerSelect(GamePlayer.Robot)
        }
        binding.boardView.setPieceClickListener(this)
        binding.boardView.setGameBoardProvider(this)
        binding.boardView.setPieceProvider(this)
    }

    override fun onResume() {
        super.onResume()
        updateByState(callback?.getGameState() ?: GameState.Idle)
    }

    private fun startGame() {
        callback?.startGame()
        binding.boardView.notifyBoardChanged()
    }

    override fun onPieceClick(x: Int, y: Int) {
        callback?.onPieceClick(x, y)
    }

    override fun snapshot(): GameBoardSnapshot {
        return GameManager.snapshot()
    }

    override fun themeChanged(): Boolean {
        return PieceViewFactory.checkResourceChanged()
    }

    override fun createPieceView(context: Context): PieceView {
        return PieceViewFactory.createPieceView(context)
    }

    fun onNewGame() {
        binding.boardView.notifyPieceChanged()
    }

    fun onPlayerChanged() {
        // TODO
    }

    fun onCurrentHandChanged() {
        binding.boardView.notifyBoardChanged()
        // TODO
    }

    fun updateByState(gameState: GameState) {
        if (!isResumed) {
            // 不是resume之后的话，可能会有空指针
            return
        }
        binding.pausePanel.isVisible = gameState == GameState.Pause || gameState == GameState.Ready
        binding.playerSelectPanel.isVisible = gameState == GameState.Idle
        when (gameState) {
            GameState.Idle -> {
                binding.playerSelectPanel.isVisible = true
                updatePlayerSelectPanel()
            }

            GameState.Ready -> {
                binding.resumeButtonText.setText(R.string.title_start)
            }

            GameState.Pause -> {
                binding.resumeButtonText.setText(R.string.title_resume)
            }

            GameState.Running -> {
                // 更新游戏界面
            }
        }
    }

    private fun updatePlayerSelectPanel() {
        val playerAState = isPlayerSelected(GamePlayer.HumanA)
        binding.playerAButton.isClickable = !playerAState
        binding.playerAButton.alpha = if (playerAState) {
            0.5f
        } else {
            1f
        }

        val playerBState = isPlayerSelected(GamePlayer.HumanB)
        binding.playerBButton.isClickable = !playerBState
        binding.playerBButton.alpha = if (playerBState) {
            0.5f
        } else {
            1f
        }

        val playerRobotState = isPlayerSelected(GamePlayer.Robot)
        binding.robotButton.isClickable = !playerRobotState
        binding.robotButton.alpha = if (playerRobotState) {
            0.5f
        } else {
            1f
        }
    }

    private fun isPlayerSelected(player: GamePlayer): Boolean {
        return callback?.isPlayerSelected(player) ?: false
    }

    interface Callback {
        fun startGame()
        fun getGameState(): GameState
        fun onPlayerSelect(player: GamePlayer)
        fun isPlayerSelected(player: GamePlayer): Boolean
        fun onPieceClick(x: Int, y: Int)
    }

}