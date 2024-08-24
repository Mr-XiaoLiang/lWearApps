package com.lollipop.wear.ttt.theme

import android.content.Context
import android.content.SharedPreferences
import com.lollipop.wear.ttt.R

object PieceTheme {

    private const val KEY_PIECE_THEME = "piece_theme"
    private const val KEY_PIECE_THEME_X = "piece_theme_x"
    private const val KEY_PIECE_THEME_O = "piece_theme_o"
    private const val KEY_PIECE_THEME_EMPTY = "piece_theme_empty"

    const val PIECE_NONE = 0

    val DEFAULT_X = R.drawable.baseline_close_24
    val DEFAULT_O = R.drawable.baseline_radio_button_unchecked_24
    const val DEFAULT_EMPTY = PIECE_NONE

    var pieceX: Int = DEFAULT_X
        private set
    var pieceO: Int = DEFAULT_O
        private set
    var pieceEmpty: Int = DEFAULT_EMPTY
        private set

    val resourceArray = arrayOf(
        Style(
            R.drawable.baseline_close_24,
            R.drawable.baseline_radio_button_unchecked_24,
            0
        ),
        Style(
            R.drawable.baseline_done_24,
            R.drawable.baseline_favorite_border_24,
            0
        ),
        Style(
            R.drawable.baseline_sentiment_dissatisfied_24,
            R.drawable.baseline_sentiment_satisfied_24,
            0
        ),
        Style(
            R.drawable.baseline_sentiment_very_dissatisfied_24,
            R.drawable.baseline_sentiment_satisfied_alt_24,
            0
        ),
        Style(
            R.drawable.baseline_android_24,
            R.drawable.baseline_smart_toy_24,
            0
        ),
        Style(
            R.drawable.baseline_sports_soccer_24,
            R.drawable.baseline_sports_basketball_24,
            0
        ),
        Style(
            R.drawable.baseline_sports_rugby_24,
            R.drawable.baseline_sports_volleyball_24,
            0
        ),
        Style(
            R.drawable.baseline_star_24,
            R.drawable.baseline_star_border_24,
            0
        ),
    )

    fun init(context: Context) {
        getSharedPreferences(context).apply {
            pieceX = getInt(KEY_PIECE_THEME_X, DEFAULT_X)
            pieceO = getInt(KEY_PIECE_THEME_O, DEFAULT_O)
            pieceEmpty = getInt(KEY_PIECE_THEME_EMPTY, DEFAULT_EMPTY)
        }
    }

    fun change(x: Int, o: Int, empty: Int, context: Context) {
        pieceX = x
        pieceO = o
        pieceEmpty = empty
        getSharedPreferences(context).edit().apply {
            putInt(KEY_PIECE_THEME_X, pieceX)
            putInt(KEY_PIECE_THEME_O, pieceO)
            putInt(KEY_PIECE_THEME_EMPTY, pieceEmpty)
        }.apply()
    }

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(KEY_PIECE_THEME, Context.MODE_PRIVATE)
    }

    class Style(
        val x: Int,
        val o: Int,
        val empty: Int
    )

}