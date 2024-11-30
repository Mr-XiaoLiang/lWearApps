package com.lollipop.wear.ps.business.options

import com.lollipop.wear.ps.R
import com.lollipop.wear.ps.engine.state.BackpackItem
import com.lollipop.wear.ps.engine.state.GameOption
import com.lollipop.wear.ps.engine.state.type.Commodity
import com.lollipop.wear.ps.engine.state.type.Food
import com.lollipop.wear.ps.engine.state.type.Toy

object Foods : OptionList {

    override val options = arrayOf(
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
            kcal = 1,
            dopamine = 7,
            price = 12
        )

        /**
         * 🍈甜瓜
         */
        object Melon : FoodsOption(
            key = "food_melon",
            name = R.string.food_melon,
            kcal = 1,
            dopamine = 6,
            price = 8
        )

        /**
         * 🍉西瓜
         */
        object Watermelon : FoodsOption(
            key = "food_watermelon",
            name = R.string.food_watermelon,
            kcal = 1,
            dopamine = 6,
            price = 3
        )

        /**
         * 🍊橘子
         */
        object Orange : FoodsOption(
            key = "food_orange",
            name = R.string.food_orange,
            kcal = 1,
            dopamine = 4,
            price = 4
        )

        /**
         * 🍋柠檬
         */
        object Lemon : FoodsOption(
            key = "food_lemon",
            name = R.string.food_lemon,
            kcal = 1,
            dopamine = 2,
            price = 5
        )

        /**
         * 🍌香蕉
         */
        object Banana : FoodsOption(
            key = "food_banana",
            name = R.string.food_banana,
            kcal = 1,
            dopamine = 3,
            price = 6
        )

        /**
         * 🥭芒果
         */
        object Mango : FoodsOption(
            key = "food_mango",
            name = R.string.food_mango,
            kcal = 1,
            dopamine = 4,
            price = 7
        )

        /**
         * 🍍菠萝
         */
        object Pineapple : FoodsOption(
            key = "food_pineapple",
            name = R.string.food_pineapple,
            kcal = 1,
            dopamine = 4,
            price = 9
        )

        /**
         * 🍒樱桃
         */
        object Cherry : FoodsOption(
            key = "food_cherry",
            name = R.string.food_cherry,
            kcal = 1,
            dopamine = 5,
            price = 10
        )

        /**
         * 🍎红苹果
         */
        object RedApple : FoodsOption(
            key = "food_red_apple",
            name = R.string.food_red_apple,
            kcal = 1,
            dopamine = 2,
            price = 4
        )

        /**
         * 🍏绿苹果
         */
        object GreenApple : FoodsOption(
            key = "food_green_apple",
            name = R.string.food_green_apple,
            kcal = 1,
            dopamine = 2,
            price = 4
        )

        /**
         * 🍐梨
         */
        object Pear : FoodsOption(
            key = "food_pear",
            name = R.string.food_pear,
            kcal = 1,
            dopamine = 3,
            price = 5
        )

        /**
         * 🍑桃
         */
        object Peach : FoodsOption(
            key = "food_peach",
            name = R.string.food_peach,
            kcal = 1,
            dopamine = 5,
            price = 6
        )

        /**
         * 🍓草莓
         */
        object Strawberry : FoodsOption(
            key = "food_strawberry",
            name = R.string.food_strawberry,
            kcal = 1,
            dopamine = 7,
            price = 7
        )

        /**
         * 🫐蓝莓
         */
        object Blueberry : FoodsOption(
            key = "food_blueberry",
            name = R.string.food_blueberry,
            kcal = 1,
            dopamine = 7,
            price = 7
        )

        /**
         * 🥝猕猴桃
         */
        object Kiwi : FoodsOption(
            key = "food_kiwi",
            name = R.string.food_kiwi,
            kcal = 1,
            dopamine = 6,
            price = 6
        )

        /**
         * 🍅柿子
         */
        object Persimmon : FoodsOption(
            key = "food_persimmon",
            name = R.string.food_persimmon,
            kcal = 1,
            dopamine = 4,
            price = 4
        )

        /**
         * 🫒橄榄
         */
        object Olive : FoodsOption(
            key = "food_olive",
            name = R.string.food_olive,
            kcal = 1,
            dopamine = 4,
            price = 8
        )

        /**
         * 🥥椰子
         */
        object Coconut : FoodsOption(
            key = "food_coconut",
            name = R.string.food_coconut,
            kcal = 1,
            dopamine = 4,
            price = 9
        )

        /**
         * 🥑鳄梨
         */
        object Avocado : FoodsOption(
            key = "food_avocado",
            name = R.string.food_avocado,
            kcal = 1,
            dopamine = 4,
            price = 16
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
            kcal = 5,
            dopamine = 3,
            price = 1
        )

        /**
         * 🥔土豆
         */
        object Potato : FoodsOption(
            key = "food_potato",
            name = R.string.food_potato,
            kcal = 5,
            dopamine = 3,
            price = 1
        )

        /**
         * 🥕胡萝卜
         */
        object Carrot : FoodsOption(
            key = "food_carrot",
            name = R.string.food_carrot,
            kcal = 5,
            dopamine = 3,
            price = 1
        )

        /**
         * 🌽玉米
         */
        object Corn : FoodsOption(
            key = "food_corn",
            name = R.string.food_corn,
            kcal = 7,
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
            dopamine = 6,
            price = 2
        )

        /**
         * 🫑灯笼椒
         */
        object BellPepper : FoodsOption(
            key = "food_bell_pepper",
            name = R.string.food_bell_pepper,
            kcal = 2,
            dopamine = 4,
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

        /**
         * 🫓扁面包
         */
        object Flatbread : FoodsOption(
            key = "food_flatbread",
            name = R.string.food_flatbread,
            kcal = 10,
            dopamine = 4,
            price = 5
        )

        /**
         * 🥨椒盐脆饼
         */
        object Pretzels : FoodsOption(
            key = "food_pretzels",
            name = R.string.food_pretzels,
            kcal = 10,
            dopamine = 4,
            price = 5
        )

        /**
         * 🥯面包圈
         */
        object Bagels : FoodsOption(
            key = "food_bagels",
            name = R.string.food_bagels,
            kcal = 10,
            dopamine = 4,
            price = 5
        )

        /**
         * 🥞烙饼
         */
        object Pancakes : FoodsOption(
            key = "food_pancakes",
            name = R.string.food_pancakes,
            kcal = 10,
            dopamine = 4,
            price = 5
        )

        /**
         * 🧇华夫饼
         */
        object Waffles : FoodsOption(
            key = "food_waffles",
            name = R.string.food_waffles,
            kcal = 10,
            dopamine = 4,
            price = 5
        )

        /**
         * 🧀奶酪
         */
        object Cheese : FoodsOption(
            key = "food_cheese",
            name = R.string.food_cheese,
            kcal = 10,
            dopamine = 4,
            price = 7
        )

        /**
         * 🍖排骨
         */
        object Ribs : FoodsOption(
            key = "food_ribs",
            name = R.string.food_ribs,
            kcal = 20,
            dopamine = 12,
            price = 15
        )

        /**
         * 🍗鸡腿
         */
        object PoultryLegs : FoodsOption(
            key = "food_poultry_legs",
            name = R.string.food_poultry_legs,
            kcal = 15,
            dopamine = 10,
            price = 10
        )

        /**
         * 🥩牛排
         */
        object Steak : FoodsOption(
            key = "food_steak",
            name = R.string.food_steak,
            kcal = 25,
            dopamine = 14,
            price = 25
        )

        /**
         * 🥓培根
         */
        object Bacon : FoodsOption(
            key = "food_bacon",
            name = R.string.food_bacon,
            kcal = 10,
            dopamine = 4,
            price = 7
        )

        /**
         * 🍔汉堡
         */
        object Hamburger : FoodsOption(
            key = "food_hamburger",
            name = R.string.food_hamburger,
            kcal = 15,
            dopamine = 9,
            price = 10
        )

        /**
         * 🍟薯条
         */
        object FrenchFries : FoodsOption(
            key = "food_french_fries",
            name = R.string.food_french_fries,
            kcal = 10,
            dopamine = 8,
            price = 7
        )

        /**
         * 🍕披萨
         */
        object Pizza : FoodsOption(
            key = "food_pizza",
            name = R.string.food_pizza,
            kcal = 14,
            dopamine = 9,
            price = 12
        )

        /**
         * 🌭热狗
         */
        object HotDog : FoodsOption(
            key = "food_hot_dog",
            name = R.string.food_hot_dog,
            kcal = 16,
            dopamine = 9,
            price = 15
        )

        /**
         * 🥪三明治
         */
        object Sandwich : FoodsOption(
            key = "food_sandwich",
            name = R.string.food_sandwich,
            kcal = 10,
            dopamine = 9,
            price = 9
        )

        /**
         * 🌮墨西哥卷饼
         */
        object Burritos : FoodsOption(
            key = "food_burritos",
            name = R.string.food_burritos,
            kcal = 14,
            dopamine = 9,
            price = 5
        )

        /**
         * 🌯墨西哥玉米卷
         */
        object MexicanTacos : FoodsOption(
            key = "food_mexican_tacos",
            name = R.string.food_mexican_tacos,
            kcal = 10,
            dopamine = 7,
            price = 5
        )

        /**
         * 🫔墨西哥粽子
         */
        object MexicanTamales : FoodsOption(
            key = "food_mexican_tamales",
            name = R.string.food_mexican_tamales,
            kcal = 10,
            dopamine = 5,
            price = 5
        )

        /**
         * 🥙墨西哥卷饼
         */
        object MexicanTortilla : FoodsOption(
            key = "food_mexican_tortilla",
            name = R.string.food_mexican_tortilla,
            kcal = 10,
            dopamine = 4,
            price = 5
        )

        /**
         * 🧆油炸豆丸子
         */
        object FriedBeanBalls : FoodsOption(
            key = "food_fried_bean_balls",
            name = R.string.food_fried_bean_balls,
            kcal = 10,
            dopamine = 4,
            price = 5
        )

        /**
         * 🥚蛋
         */
        object Egg : FoodsOption(
            key = "food_egg",
            name = R.string.food_egg,
            kcal = 6,
            dopamine = 4,
            price = 2
        )

        /**
         * 🍳煎蛋
         */
        object FriedEgg : FoodsOption(
            key = "food_fried_egg",
            name = R.string.food_fried_egg,
            kcal = 6,
            dopamine = 4,
            price = 2
        )

        /**
         * 🥘汤
         */
        object Soup : FoodsOption(
            key = "food_soup",
            name = R.string.food_soup,
            kcal = 8,
            dopamine = 4,
            price = 5
        )

        /**
         * 🥣粥
         */
        object Porridge : FoodsOption(
            key = "food_porridge",
            name = R.string.food_porridge,
            kcal = 4,
            dopamine = 4,
            price = 1
        )

        /**
         * 🍲炖菜
         */
        object Stew : FoodsOption(
            key = "food_stew",
            name = R.string.food_stew,
            kcal = 10,
            dopamine = 4,
            price = 5
        )

        /**
         * 🫕奶酪火锅
         */
        object CheeseHotpot : FoodsOption(
            key = "food_cheese_hotpot",
            name = R.string.food_cheese_hotpot,
            kcal = 30,
            dopamine = 40,
            price = 25
        )

        /**
         * 🥗沙拉
         */
        object Salad : FoodsOption(
            key = "food_salad",
            name = R.string.food_salad,
            kcal = 2,
            dopamine = 4,
            price = 5
        )

        /**
         * 🍿爆米花
         */
        object Popcorn : FoodsOption(
            key = "food_popcorn",
            name = R.string.food_popcorn,
            kcal = 10,
            dopamine = 4,
            price = 8
        )

        /**
         * 🧈黄油
         */
        object Butter : FoodsOption(
            key = "food_butter",
            name = R.string.food_butter,
            kcal = 20,
            dopamine = 1,
            price = 15
        )

        /**
         * 🥫罐头
         */
        object CannedFood : FoodsOption(
            key = "food_canned_food",
            name = R.string.food_canned_food,
            kcal = 10,
            dopamine = 4,
            price = 5
        )

        override val options = arrayOf<GameOption>(
            Toast,
            Hamburger,
            FrenchFries,
            Pizza,
            HotDog,
            Sandwich,
            Burritos,
            MexicanTacos,
            MexicanTamales,
            MexicanTortilla,
            FriedBeanBalls,
            Egg,
            FriedEgg,
            Soup,
            Porridge,
            Stew,
            CheeseHotpot,
            Salad,
            Popcorn,
            Butter,
            CannedFood,
            Croissant,
            Baguette,
            Flatbread,
            Pretzels,
            Bacon,
            Steak,
            PoultryLegs,
            Cheese,
            Ribs,
            Bagels,
            Pancakes,
            Waffles
        )
    }

    /**
     * 亚洲食物
     */
    object Asian : OptionList {

        /**
         * 🍱盒饭
         */
        object BoxLunch : FoodsOption(
            key = "food_box_lunch",
            name = R.string.food_box_lunch,
            kcal = 40,
            dopamine = 14,
            price = 15
        )

        /**
         * 🍘米饼
         */
        object RiceCake : FoodsOption(
            key = "food_rice_cake",
            name = R.string.food_rice_cake,
            kcal = 10,
            dopamine = 8,
            price = 3
        )

        /**
         * 🍙饭团
         */
        object RiceBall : FoodsOption(
            key = "food_rice_ball",
            name = R.string.food_rice_ball,
            kcal = 10,
            dopamine = 4,
            price = 5
        )

        /**
         * 🍚米饭
         */
        object Rice : FoodsOption(
            key = "food_rice",
            name = R.string.food_rice,
            kcal = 50,
            dopamine = 10,
            price = 3
        )

        /**
         * 🍛咖喱饭
         */
        object CurryRice : FoodsOption(
            key = "food_curry_rice",
            name = R.string.food_curry_rice,
            kcal = 40,
            dopamine = 14,
            price = 15
        )

        /**
         * 🍜面条
         */
        object Noodle : FoodsOption(
            key = "food_noodle",
            name = R.string.food_noodle,
            kcal = 30,
            dopamine = 12,
            price = 9
        )

        /**
         * 🍝意大利面
         */
        object Pasta : FoodsOption(
            key = "food_pasta",
            name = R.string.food_pasta,
            kcal = 30,
            dopamine = 14,
            price = 15
        )

        /**
         * 🍠烤红薯
         */
        object BakedSweetPotatoes : FoodsOption(
            key = "food_baked_sweet_potatoes",
            name = R.string.food_baked_sweet_potatoes,
            kcal = 10,
            dopamine = 16,
            price = 5
        )

        /**
         * 🍢关东煮
         */
        object Oden : FoodsOption(
            key = "food_oden",
            name = R.string.food_oden,
            kcal = 10,
            dopamine = 14,
            price = 15
        )

        /**
         * 🍣寿司
         */
        object Sushi : FoodsOption(
            key = "food_sushi",
            name = R.string.food_sushi,
            kcal = 20,
            dopamine = 20,
            price = 35
        )

        /**
         * 🍤天妇罗
         */
        object FriedShrimp : FoodsOption(
            key = "food_fried_shrimp",
            name = R.string.food_fried_shrimp,
            kcal = 10,
            dopamine = 8,
            price = 5
        )

        /**
         * 🍥鱼板
         */
        object FishPan : FoodsOption(
            key = "food_fish_pan",
            name = R.string.food_fish_pan,
            kcal = 1,
            dopamine = 1,
            price = 1
        )

        /**
         * 🥮月饼
         */
        object YueBing : FoodsOption(
            key = "food_yue_bing",
            name = R.string.food_yue_bing,
            kcal = 10,
            dopamine = 4,
            price = 5
        )

        /**
         * 🍡团子
         */
        object Dango : FoodsOption(
            key = "food_dango",
            name = R.string.food_dango,
            kcal = 10,
            dopamine = 4,
            price = 6
        )

        /**
         * 🥟饺子
         */
        object JiaoZi : FoodsOption(
            key = "food_jiao_zi",
            name = R.string.food_jiao_zi,
            kcal = 15,
            dopamine = 7,
            price = 6
        )

        /**
         * 🥠幸运饼干
         */
        object LuckyCookie : FoodsOption(
            key = "food_lucky_cookie",
            name = R.string.food_lucky_cookie,
            kcal = 1,
            dopamine = 24,
            price = 15
        )

        /**
         * 🥡外卖
         */
        object Takeout : FoodsOption(
            key = "food_takeout",
            name = R.string.food_takeout,
            kcal = 14,
            dopamine = 4,
            price = 5
        )

        override val options = arrayOf<GameOption>(
            BoxLunch,
            RiceCake,
            RiceBall,
            Rice,
            CurryRice,
            Noodle,
            Pasta,
            BakedSweetPotatoes,
            Oden,
            Sushi,
            FriedShrimp,
            FishPan,
            YueBing,
            Dango,
            JiaoZi,
            LuckyCookie,
            Takeout
        )
    }

    /**
     * 海鲜
     */
    object Seafood : OptionList {

        /**
         * 🦀螃蟹
         */
        object Crab : FoodsOption(
            key = "food_crab",
            name = R.string.food_crab,
            kcal = 10,
            dopamine = 24,
            price = 35
        )

        /**
         * 🦞龙虾
         */
        object Lobster : FoodsOption(
            key = "food_lobster",
            name = R.string.food_lobster,
            kcal = 10,
            dopamine = 34,
            price = 45
        )

        /**
         * 🦐虾
         */
        object Shrimp : FoodsOption(
            key = "food_shrimp",
            name = R.string.food_shrimp,
            kcal = 3,
            dopamine = 14,
            price = 15
        )

        /**
         * 🦑鱿鱼
         */
        object Squid : FoodsOption(
            key = "food_squid",
            name = R.string.food_squid,
            kcal = 1,
            dopamine = 9,
            price = 5
        )

        /**
         * 🦪牡蛎
         */
        object Oyster : FoodsOption(
            key = "food_oyster",
            name = R.string.food_oyster,
            kcal = 2,
            dopamine = 9,
            price = 5
        )

        override val options = arrayOf<GameOption>(
            Crab,
            Lobster,
            Shrimp,
            Squid,
            Oyster
        )
    }

    /**
     * 甜点
     */
    object Dessert : OptionList {

        /**
         * 🍦甜筒冰淇淋
         */
        object IceCreamCone : FoodsOption(
            key = "food_ice_cream_cone",
            name = R.string.food_ice_cream_cone,
            kcal = 0,
            dopamine = 14,
            price = 5
        )

        /**
         * 🍧刨冰
         */
        object ShavedIce : FoodsOption(
            key = "food_shaved_ice",
            name = R.string.food_shaved_ice,
            kcal = 0,
            dopamine = 14,
            price = 5
        )

        /**
         * 🍨冰淇淋
         */
        object IceCream : FoodsOption(
            key = "food_ice_cream",
            name = R.string.food_ice_cream,
            kcal = 0,
            dopamine = 14,
            price = 5
        )

        /**
         * 🍩甜甜圈
         */
        object Donut : FoodsOption(
            key = "food_donut",
            name = R.string.food_donut,
            kcal = 5,
            dopamine = 6,
            price = 5
        )

        /**
         * 🍪曲奇饼
         */
        object Cookie : FoodsOption(
            key = "food_cookie",
            name = R.string.food_cookie,
            kcal = 7,
            dopamine = 5,
            price = 3
        )

        /**
         * 🎂生日蛋糕
         */
        object BirthdayCake : FoodsOption(
            key = "food_birthday_cake",
            name = R.string.food_birthday_cake,
            kcal = 10,
            dopamine = 24,
            price = 19
        )

        /**
         * 🍰水果蛋糕
         */
        object FruitCake : FoodsOption(
            key = "food_fruit_cake",
            name = R.string.food_fruit_cake,
            kcal = 10,
            dopamine = 14,
            price = 15
        )

        /**
         * 🧁纸杯蛋糕
         */
        object Cupcakes : FoodsOption(
            key = "food_cupcakes",
            name = R.string.food_cupcakes,
            kcal = 10,
            dopamine = 14,
            price = 9
        )

        /**
         * 🥧派
         */
        object Pie : FoodsOption(
            key = "food_pie",
            name = R.string.food_pie,
            kcal = 15,
            dopamine = 14,
            price = 15
        )

        /**
         * 🍫巧克力
         */
        object Chocolate : FoodsOption(
            key = "food_chocolate",
            name = R.string.food_chocolate,
            kcal = 10,
            dopamine = 9,
            price = 5
        )

        /**
         * 🍬糖果
         */
        object Candy : FoodsOption(
            key = "food_candy",
            name = R.string.food_candy,
            kcal = 1,
            dopamine = 9,
            price = 5
        )

        /**
         * 🍭棒棒糖
         */
        object Lollipop : FoodsOption(
            key = "food_lollipop",
            name = R.string.food_lollipop,
            kcal = 1,
            dopamine = 6,
            price = 0
        )

        /**
         * 🍮布丁
         */
        object Pudding : FoodsOption(
            key = "food_pudding",
            name = R.string.food_pudding,
            kcal = 10,
            dopamine = 8,
            price = 5
        )

        /**
         * 🍯蜂蜜
         */
        object Honey : FoodsOption(
            key = "food_honey",
            name = R.string.food_honey,
            kcal = 10,
            dopamine = 14,
            price = 8
        )

        override val options = arrayOf<GameOption>(
            IceCreamCone,
            ShavedIce,
            IceCream,
            Donut,
            Cookie,
            BirthdayCake,
            FruitCake,
            Cupcakes,
            Pie,
            Chocolate,
            Candy,
            Lollipop,
            Pudding,
            Honey
        )
    }

    /**
     * 饮料
     */
    object Drink : OptionList {

        /**
         * 🍼瓶瓶奶
         */
        object Bottle : FoodsOption(
            key = "food_bottle",
            name = R.string.food_bottle,
            kcal = 6,
            dopamine = 4,
            price = 5
        )

        /**
         * 🥛牛奶
         */
        object Milk : FoodsOption(
            key = "food_milk",
            name = R.string.food_milk,
            kcal = 6,
            dopamine = 4,
            price = 5
        )

        /**
         * ☕咖啡
         */
        object Coffee : FoodsOption(
            key = "food_coffee",
            name = R.string.food_coffee,
            kcal = 1,
            dopamine = 4,
            price = 5
        )

        /**
         * 🫖红茶
         */
        object Tea : FoodsOption(
            key = "food_tea",
            name = R.string.food_tea,
            kcal = 0,
            dopamine = 5,
            price = 5
        )

        /**
         * 🍵绿茶
         */
        object GreenTea : FoodsOption(
            key = "food_green_tea",
            name = R.string.food_green_tea,
            kcal = 0,
            dopamine = 5,
            price = 5
        )

        /**
         * 🍶清酒
         */
        object Sake : FoodsOption(
            key = "food_sake",
            name = R.string.food_sake,
            kcal = 0,
            dopamine = 2,
            price = 5
        )

        /**
         * 🍾香槟
         */
        object Champagne : FoodsOption(
            key = "food_champagne",
            name = R.string.food_champagne,
            kcal = 0,
            dopamine = 8,
            price = 15
        )

        /**
         * 🍷红酒
         */
        object RedWine : FoodsOption(
            key = "food_red_wine",
            name = R.string.food_red_wine,
            kcal = 0,
            dopamine = 8,
            price = 15
        )

        /**
         * 🍸鸡尾酒
         */
        object Cocktail : FoodsOption(
            key = "food_cocktail",
            name = R.string.food_cocktail,
            kcal = 0,
            dopamine = 14,
            price = 25
        )

        /**
         * 🍹水果饮料
         */
        object FruitDrink : FoodsOption(
            key = "food_fruit_drink",
            name = R.string.food_fruit_drink,
            kcal = 0,
            dopamine = 24,
            price = 15
        )

        /**
         * 🍺啤酒
         */
        object Beer : FoodsOption(
            key = "food_beer",
            name = R.string.food_beer,
            kcal = 0,
            dopamine = 4,
            price = 5
        )

        /**
         * 🧋珍珠奶茶
         */
        object MilkTea : FoodsOption(
            key = "food_milk_tea",
            name = R.string.food_milk_tea,
            kcal = 10,
            dopamine = 34,
            price = 9
        )

        /**
         * 🧃果汁
         */
        object Juice : FoodsOption(
            key = "food_juice",
            name = R.string.food_juice,
            kcal = 0,
            dopamine = 4,
            price = 2
        )

        /**
         * 🧉马黛茶
         */
        object YerbaMate : FoodsOption(
            key = "food_yerba_mate",
            name = R.string.food_yerba_mate,
            kcal = 0,
            dopamine = 2,
            price = 5
        )

        override val options = arrayOf<GameOption>(
            Bottle,
            Milk,
            Coffee,
            Tea,
            GreenTea,
            Sake,
            Champagne,
            RedWine,
            Cocktail,
            FruitDrink,
            Beer,
            MilkTea,
            Juice,
            YerbaMate
        )
    }

}