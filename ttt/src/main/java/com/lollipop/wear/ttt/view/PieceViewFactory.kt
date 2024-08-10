package com.lollipop.wear.ttt.view

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.lollipop.wear.ttt.game.GameManager
import com.lollipop.wear.ttt.theme.PieceTheme

object PieceViewFactory {

    var colorful: Boolean = false

    private var currentPieceX: Int = PieceTheme.DEFAULT_X
    private var currentPieceO: Int = PieceTheme.DEFAULT_O
    private var currentPieceEmpty: Int = PieceTheme.DEFAULT_EMPTY

    fun checkResourceChanged(): Boolean {
        return currentPieceX != PieceTheme.pieceX ||
                currentPieceO != PieceTheme.pieceO ||
                currentPieceEmpty != PieceTheme.pieceEmpty
    }

    fun createPieceView(context: Context): PieceView {
        currentPieceX = PieceTheme.pieceX
        currentPieceO = PieceTheme.pieceO
        currentPieceEmpty = PieceTheme.pieceEmpty

        val pieceXTint = GameManager.firstHand.colorRes
        val pieceOTint = GameManager.rearHand.colorRes

        val needTint = colorful || currentPieceX == currentPieceO
        val tintX = if (needTint) {
            ColorStateList.valueOf(ContextCompat.getColor(context, pieceXTint))
        } else {
            null
        }
        val tintO = if (needTint) {
            ColorStateList.valueOf(ContextCompat.getColor(context, pieceOTint))
        } else {
            null
        }
        return PieceView(context).apply {
            setPieceX(createPieceIconView(context, currentPieceX, tintX))
            setPieceO(createPieceIconView(context, currentPieceO, tintO))
            setPieceEmpty(createPieceIconView(context, currentPieceEmpty, null))
        }
    }

    private fun createPieceIconView(context: Context, icon: Int, tint: ColorStateList?): View {
        return ImageView(context).apply {
            if (icon != PieceTheme.PIECE_NONE) {
                setImageResource(icon)
                if (tint != null) {
                    imageTintList = tint
                }
            }
        }
    }

}