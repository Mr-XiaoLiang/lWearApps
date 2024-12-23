package com.lollipop.wear.ps.business.options

import com.lollipop.wear.ps.R
import com.lollipop.wear.ps.engine.state.GameOption
import com.lollipop.wear.ps.engine.state.type.Food
import com.lollipop.wear.ps.engine.state.type.Money
import com.lollipop.wear.ps.engine.state.type.Toy

object Toys : OptionList {

    abstract class ToyOption(
        override val key: String,
        override val name: Int,
        override val dopamine: Int,
        override val amount: Int,
        override val kcal: Int
    ) : GameOption, Toy, Money, Food

    /**
     * 发呆
     */
    object StareBlankly : ToyOption(
        key = "toy_stare_blankly",
        name = R.string.label_toy_stare_blankly,
        dopamine = 10,
        amount = 0,
        kcal = -10
    )

    /**
     * 乒乓球
     */
    object PingPong : ToyOption(
        key = "toy_ping_pong",
        name = R.string.label_toy_ping_pong,
        dopamine = 30,
        amount = 0,
        kcal = -30
    )

    /**
     * 足球
     */
    object Football : ToyOption(
        key = "toy_football",
        name = R.string.label_toy_football,
        dopamine = 40,
        amount = 0,
        kcal = -40
    )

    /**
     * 篮球
     */
    object Basketball : ToyOption(
        key = "toy_basketball",
        name = R.string.label_toy_basketball,
        dopamine = 40,
        amount = 0,
        kcal = -40
    )

    /**
     * 羽毛球
     */
    object Badminton : ToyOption(
        key = "toy_badminton",
        name = R.string.label_toy_badminton,
        dopamine = 40,
        amount = 0,
        kcal = -40
    )

    /**
     * 网球
     */
    object Tennis : ToyOption(
        key = "toy_tennis",
        name = R.string.label_toy_tennis,
        dopamine = 40,
        amount = 0,
        kcal = -40
    )

    /**
     * 保龄球
     */
    object Bowling : ToyOption(
        key = "toy_bowling",
        name = R.string.label_toy_bowling,
        dopamine = 40,
        amount = -100,
        kcal = -40
    )

    /**
     * 高尔夫球
     */
    object Golf : ToyOption(
        key = "toy_golf",
        name = R.string.label_toy_golf,
        dopamine = 40,
        amount = -1000,
        kcal = -40
    )

    /**
     * 游泳
     */
    object Swimming : ToyOption(
        key = "toy_swimming",
        name = R.string.label_toy_swimming,
        dopamine = 40,
        amount = -10,
        kcal = -40
    )

    override val options: Array<GameOption> = arrayOf(
        StareBlankly,
        PingPong,
        Football,
        Basketball,
        Badminton,
        Tennis,
        Bowling,
        Golf,
        Swimming
    )

}