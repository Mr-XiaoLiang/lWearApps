package com.lollipop.wear.game

import android.util.Log

object GameConfig {

    const val DEFAULT_GAME_DIR = "corona/"

    var gamePage: GamePage? = null

    inline fun <reified T> invoke(block: (GamePage) -> T?): T? {
        val page = gamePage ?: return null
        return try {
            block(page)
        } catch (e: Exception) {
            Log.e("GameConfig", "invoke error", e)
            null
        }
    }

}