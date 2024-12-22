package com.lollipop.wear.ps.business.options

import com.lollipop.wear.ps.R
import com.lollipop.wear.ps.engine.state.GameOption
import com.lollipop.wear.ps.engine.state.type.Food
import com.lollipop.wear.ps.engine.state.type.Money
import com.lollipop.wear.ps.engine.state.type.Toy

object Works : OptionList {

    abstract class WorkOption(
        override val key: String,
        override val name: Int,
        override val amount: Int,
        override val dopamine: Int,
        override val kcal: Int
    ) : GameOption, Money, Toy, Food

    /**
     * 发传单
     */
    object Leafleteer : WorkOption(
        key = "work_leafleteer",
        name = R.string.label_work_leafleteer,
        amount = 30,
        dopamine = -10,
        kcal = -30
    )

    /**
     * 图书管理员
     */
    object Librarian : WorkOption(
        key = "work_librarian",
        name = R.string.label_work_librarian,
        amount = 30,
        dopamine = -10,
        kcal = -30
    )

    override val options: Array<GameOption> = arrayOf(
        Leafleteer,
        Librarian
    )

}