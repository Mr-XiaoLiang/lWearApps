package com.lollipop.wear.ttt.theme

import com.lollipop.wear.ttt.R

object PieceTheme {

    const val PIECE_NONE = 0

    val DEFAULT_X = R.drawable.baseline_close_24
    val DEFAULT_O = R.drawable.baseline_radio_button_unchecked_24
    val DEFAULT_EMPTY = PIECE_NONE

    var pieceX: Int = DEFAULT_X
    var pieceO: Int = DEFAULT_O
    var pieceEmpty: Int = DEFAULT_EMPTY

    val resourceArray = arrayOf(
        R.drawable.baseline_close_24,
        R.drawable.baseline_radio_button_unchecked_24
    )

}