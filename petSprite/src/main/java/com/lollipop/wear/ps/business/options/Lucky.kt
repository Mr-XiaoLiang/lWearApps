package com.lollipop.wear.ps.business.options

import com.lollipop.wear.ps.R
import com.lollipop.wear.ps.engine.state.GameOption
import com.lollipop.wear.ps.engine.state.type.Money
import com.lollipop.wear.ps.engine.state.type.Toy

object Lucky : OptionList {

    override val options: Array<GameOption> = arrayOf(
        *Riches.options
    )

    object Riches : OptionList {

        open class MoneyOption(
            override val key: String,
            override val name: Int,
            override val amount: Int,
            override val dopamine: Int = (amount * 0.1F).toInt(),
        ) : GameOption, Money, Toy

        object Money1 : MoneyOption(
            key = "lucky_money_1",
            name = R.string.label_lucky_money_1,
            amount = 1,
        )

        object Money5 : MoneyOption(
            key = "lucky_money_5",
            name = R.string.label_lucky_money_5,
            amount = 10,
        )

        object Money10 : MoneyOption(
            key = "lucky_money_10",
            name = R.string.label_lucky_money_10,
            amount = 10,
        )

        object Money20 : MoneyOption(
            key = "lucky_money_20",
            name = R.string.label_lucky_money_20,
            amount = 20,
        )

        object Money50 : MoneyOption(
            key = "lucky_money_50",
            name = R.string.label_lucky_money_50,
            amount = 50,
        )

        object Money100 : MoneyOption(
            key = "lucky_money_100",
            name = R.string.label_lucky_money_100,
            amount = 100,
        )

        object Money200 : MoneyOption(
            key = "lucky_money_200",
            name = R.string.label_lucky_money_200,
            amount = 200,
        )

        object Money500 : MoneyOption(
            key = "lucky_money_500",
            name = R.string.label_lucky_money_500,
            amount = 500,
        )

        override val options: Array<GameOption> = arrayOf(
            Money1,
            Money5,
            Money10,
            Money20,
            Money50,
            Money100,
            Money200,
            Money500,
        )

    }

    // TODO 除了捡到钱，还能捡到什么呢？

}