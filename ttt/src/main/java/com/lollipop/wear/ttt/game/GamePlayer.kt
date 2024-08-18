package com.lollipop.wear.ttt.game

import com.lollipop.wear.ttt.R

enum class GamePlayer(val colorRes: Int, val iconRes: Int) {

    HumanA(R.color.piece_tint_a, R.drawable.baseline_videogame_asset_24),
    HumanB(R.color.piece_tint_b, R.drawable.baseline_videogame_asset_24),
    Robot(R.color.piece_tint_c, R.drawable.baseline_android_24);

    fun isHuman(): Boolean {
        return this != Robot
    }

}