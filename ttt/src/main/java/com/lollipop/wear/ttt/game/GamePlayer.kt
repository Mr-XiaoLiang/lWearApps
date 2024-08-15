package com.lollipop.wear.ttt.game

import com.lollipop.wear.ttt.R

enum class GamePlayer(val colorRes: Int) {

    HumanA(R.color.piece_tint_a),
    HumanB(R.color.piece_tint_b),
    Robot(R.color.piece_tint_c);

    fun isHuman(): Boolean {
        return this != Robot
    }

}