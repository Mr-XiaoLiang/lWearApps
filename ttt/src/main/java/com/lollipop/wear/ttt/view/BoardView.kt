package com.lollipop.wear.ttt.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.lollipop.wear.ttt.game.GameBoardProvider
import com.lollipop.wear.ttt.game.GamePiece

class BoardView @JvmOverloads constructor(
    context: Context, attributeSet: AttributeSet? = null
) : FrameLayout(context, attributeSet) {

    private var gameBoardProvider: GameBoardProvider? = null

    fun setGameBoardProvider(provider: GameBoardProvider) {
        gameBoardProvider = provider
    }

    interface Piece {

        fun update(piece: GamePiece)

    }

}