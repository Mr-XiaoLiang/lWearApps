package com.lollipop.wear.ps.business.options

import com.lollipop.wear.ps.R
import com.lollipop.wear.ps.engine.state.BackpackItem
import com.lollipop.wear.ps.engine.state.GameOption
import com.lollipop.wear.ps.engine.state.type.Commodity
import com.lollipop.wear.ps.engine.state.type.Food
import com.lollipop.wear.ps.engine.state.type.Toy

object Foods : OptionList {

    override val options = arrayOf(
        Cookie,
        *Fruit.options,
        *Vegetable.options,
        *Cooked.options,
        *Asian.options,
        *Seafood.options,
        *Dessert.options,
        *Drink.options
    )

    abstract class FoodsOption(
        override val key: String,
        override val name: Int,
        override val kcal: Int,
        override val dopamine: Int,
        override val price: Int
    ) : GameOption, Food, BackpackItem, Toy, Commodity

    object Cookie : FoodsOption(
        key = "food_cookie",
        name = R.string.food_cookie,
        kcal = 5,
        dopamine = 5,
        price = 3
    )

    /**
     * 水果
     */
    object Fruit : OptionList {

        /**
         * 🍇葡萄
         */
        object Grape : FoodsOption(
            key = "food_grape",
            name = R.string.food_grape,
            kcal = 2,
            dopamine = 7,
            price = 12
        )

        /**
         * 🍈甜瓜
         */
        object Melon : FoodsOption(
            key = "food_melon",
            name = R.string.food_melon,
            kcal = 2,
            dopamine = 6,
            price = 8
        )

        /**
         * 🍉西瓜
         */
        object Watermelon : FoodsOption(
            key = "food_watermelon",
            name = R.string.food_watermelon,
            kcal = 3,
            dopamine = 6,
            price = 3
        )

        /**
         * 🍊橘子
         */
        object Orange : FoodsOption(
            key = "food_orange",
            name = R.string.food_orange,
            kcal = 2,
            dopamine = 4,
            price = 4
        )

        /**
         * 🍋柠檬
         */
        object Lemon : FoodsOption(
            key = "food_lemon",
            name = R.string.food_lemon,
            kcal = 2,
            dopamine = 2,
            price = 5
        )

        /**
         * 🍌香蕉
         */
        object Banana : FoodsOption(
            key = "food_banana",
            name = R.string.food_banana,
            kcal = 2,
            dopamine = 3,
            price = 6
        )

        /**
         * 🥭芒果
         */
        object Mango : FoodsOption(
            key = "food_mango",
            name = R.string.food_mango,
            kcal = 4,
            dopamine = 4,
            price = 7
        )

        /**
         * 🍍菠萝
         */
        object Pineapple : FoodsOption(
            key = "food_pineapple",
            name = R.string.food_pineapple,
            kcal = 3,
            dopamine = 4,
            price = 9
        )

        /**
         * 🍒樱桃
         */
        object Cherry : FoodsOption(
            key = "food_cherry",
            name = R.string.food_cherry,
            kcal = 2,
            dopamine = 5,
            price = 10
        )

        /**
         * 🍎红苹果
         */
        object RedApple : FoodsOption(
            key = "food_red_apple",
            name = R.string.food_red_apple,
            kcal = 2,
            dopamine = 3,
            price = 4
        )

        /**
         * 🍏绿苹果
         */
        object GreenApple : FoodsOption(
            key = "food_green_apple",
            name = R.string.food_green_apple,
            kcal = 2,
            dopamine = 3,
            price = 4
        )

        /**
         * 🍐梨
         */
        object Pear : FoodsOption(
            key = "food_pear",
            name = R.string.food_pear,
            kcal = 2,
            dopamine = 3,
            price = 5
        )

        /**
         * 🍑桃
         */
        object Peach : FoodsOption(
            key = "food_peach",
            name = R.string.food_peach,
            kcal = 2,
            dopamine = 5,
            price = 6
        )

        /**
         * 🍓草莓
         */
        object Strawberry : FoodsOption(
            key = "food_strawberry",
            name = R.string.food_strawberry,
            kcal = 2,
            dopamine = 7,
            price = 7
        )

        /**
         * 🫐蓝莓
         */
        object Blueberry : FoodsOption(
            key = "food_blueberry",
            name = R.string.food_blueberry,
            kcal = 2,
            dopamine = 7,
            price = 7
        )

        /**
         * 🥝猕猴桃
         */
        object Kiwi : FoodsOption(
            key = "food_kiwi",
            name = R.string.food_kiwi,
            kcal = 2,
            dopamine = 6,
            price = 6
        )

        /**
         * 🍅柿子
         */
        object Persimmon : FoodsOption(
            key = "food_persimmon",
            name = R.string.food_persimmon,
            kcal = 2,
            dopamine = 4,
            price = 4
        )

        /**
         * 🫒橄榄
         */
        object Olive : FoodsOption(
            key = "food_olive",
            name = R.string.food_olive,
            kcal = 2,
            dopamine = 4,
            price = 8
        )

        /**
         * 🥥椰子
         */
        object Coconut : FoodsOption(
            key = "food_coconut",
            name = R.string.food_coconut,
            kcal = 2,
            dopamine = 4,
            price = 9
        )

        /**
         * 🥑鳄梨
         */
        object Avocado : FoodsOption(
            key = "food_avocado",
            name = R.string.food_avocado,
            kcal = 2,
            dopamine = 4,
            price = 7
        )

        override val options = arrayOf<GameOption>(
            Grape,
            Melon,
            Watermelon,
            Orange,
            Lemon,
            Banana,
            Mango,
            Pineapple,
            Cherry,
            RedApple,
            GreenApple,
            Pear,
            Peach,
            Strawberry,
            Blueberry,
            Kiwi,
            Persimmon,
            Olive,
            Coconut,
            Avocado
        )

    }

    /**
     * 蔬菜
     */
    object Vegetable : OptionList {

        /**
         * 🍆茄子
         */
        object Eggplant : FoodsOption(
            key = "food_eggplant",
            name = R.string.food_eggplant,
            kcal = 2,
            dopamine = 4,
            price = 1
        )

        /**
         * 🥔土豆
         */
        object Potato : FoodsOption(
            key = "food_potato",
            name = R.string.food_potato,
            kcal = 2,
            dopamine = 4,
            price = 1
        )

        /**
         * 🥕胡萝卜
         */
        object Carrot : FoodsOption(
            key = "food_carrot",
            name = R.string.food_carrot,
            kcal = 2,
            dopamine = 4,
            price = 1
        )

        /**
         * 🌽玉米
         */
        object Corn : FoodsOption(
            key = "food_corn",
            name = R.string.food_corn,
            kcal = 2,
            dopamine = 4,
            price = 2
        )

        /**
         * 🌶️红辣椒
         */
        object RedPepper : FoodsOption(
            key = "food_red_pepper",
            name = R.string.food_red_pepper,
            kcal = 2,
            dopamine = 2,
            price = 2
        )

        /**
         * 🫑灯笼椒
         */
        object BellPepper : FoodsOption(
            key = "food_bell_pepper",
            name = R.string.food_bell_pepper,
            kcal = 2,
            dopamine = 2,
            price = 2
        )

        /**
         * 🥒黄瓜
         */
        object Cucumber : FoodsOption(
            key = "food_cucumber",
            name = R.string.food_cucumber,
            kcal = 2,
            dopamine = 3,
            price = 2
        )

        /**
         * 🥬青菜
         */
        object Greengrocery : FoodsOption(
            key = "food_greengrocery",
            name = R.string.food_greengrocery,
            kcal = 2,
            dopamine = 3,
            price = 2
        )

        /**
         * 🥦西兰花
         */
        object Broccoli : FoodsOption(
            key = "food_broccoli",
            name = R.string.food_broccoli,
            kcal = 2,
            dopamine = 4,
            price = 3
        )

        /**
         * 🧄蒜
         */
        object Garlic : FoodsOption(
            key = "food_garlic",
            name = R.string.food_garlic,
            kcal = 1,
            dopamine = 1,
            price = 2
        )

        /**
         * 🧅洋葱
         */
        object Onion : FoodsOption(
            key = "food_onion",
            name = R.string.food_onion,
            kcal = 1,
            dopamine = 2,
            price = 2
        )

        /**
         * 🍄蘑菇
         */
        object Mushroom : FoodsOption(
            key = "food_mushroom",
            name = R.string.food_mushroom,
            kcal = 1,
            dopamine = 7,
            price = 6
        )

        /**
         * 🥜花生
         */
        object Peanut : FoodsOption(
            key = "food_peanut",
            name = R.string.food_peanut,
            kcal = 2,
            dopamine = 4,
            price = 3
        )

        /**
         * 🫘豆子
         */
        object Bean : FoodsOption(
            key = "food_bean",
            name = R.string.food_bean,
            kcal = 2,
            dopamine = 1,
            price = 2
        )

        /**
         * 🌰栗子
         */
        object Chestnut : FoodsOption(
            key = "food_chestnut",
            name = R.string.food_chestnut,
            kcal = 2,
            dopamine = 4,
            price = 4
        )

        override val options = arrayOf<GameOption>(
            Eggplant,
            Potato,
            Carrot,
            Corn,
            RedPepper,
            BellPepper,
            Cucumber,
            Greengrocery,
            Broccoli,
            Garlic,
            Onion,
            Mushroom,
            Peanut,
            Bean,
            Chestnut
        )

    }

    /**
     * 熟食
     */
    object Cooked : OptionList {

        /**
         * 🍞吐司面包
         */
        object Toast : FoodsOption(
            key = "food_toast",
            name = R.string.food_toast,
            kcal = 10,
            dopamine = 4,
            price = 5
        )

        /**
         * 🥐牛角面包
         */
        object Croissant : FoodsOption(
            key = "food_croissant",
            name = R.string.food_croissant,
            kcal = 10,
            dopamine = 4,
            price = 5
        )

        /**
         * 🥖法式长棍面包
         */
        object Baguette : FoodsOption(
            key = "food_baguette",
            name = R.string.food_baguette,
            kcal = 10,
            dopamine = 4,
            price = 5
        )

        // TODO

        override val options = arrayOf<GameOption>()
    }

    /**
     * 亚洲食物
     */
    object Asian : OptionList {
        override val options = arrayOf<GameOption>()
    }

    /**
     * 海鲜
     */
    object Seafood : OptionList {
        override val options = arrayOf<GameOption>()
    }

    /**
     * 甜点
     */
    object Dessert : OptionList {
        override val options = arrayOf<GameOption>()
    }

    /**
     * 饮料
     */
    object Drink : OptionList {
        override val options = arrayOf<GameOption>()
    }

}