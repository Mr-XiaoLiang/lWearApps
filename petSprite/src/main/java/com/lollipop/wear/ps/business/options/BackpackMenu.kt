package com.lollipop.wear.ps.business.options

import com.lollipop.wear.ps.R
import com.lollipop.wear.ps.engine.state.GameOption

enum class BackpackMenu(val key: String, val label: Int) {

    FoodsFruit("foods_fruit", R.string.label_food_fruit),
    FoodsVegetable("foods_vegetable", R.string.label_food_vegetable),
    FoodsCooked("foods_cooked", R.string.label_food_cooked),
    FoodsAsian("foods_asian", R.string.label_food_asian),
    FoodsSeafood("foods_seafood", R.string.label_food_seafood),
    FoodsDessert("foods_dessert", R.string.label_food_dessert),
    FoodsDrink("foods_drink", R.string.label_food_drink);

    companion object {
        fun find(key: String): BackpackMenu? {
            return entries.firstOrNull { it.key == key }
        }
    }

    fun getOptionList(): Array<GameOption> {
        val optionList = when (this) {
            FoodsFruit -> Foods.Fruit
            FoodsVegetable -> Foods.Vegetable
            FoodsCooked -> Foods.Cooked
            FoodsAsian -> Foods.Asian
            FoodsSeafood -> Foods.Seafood
            FoodsDessert -> Foods.Dessert
            FoodsDrink -> Foods.Drink
        }
        return optionList.options
    }

}