package com.lollipop.wear.ps.engine.state.type

/**
 * 这是一个商品
 */
interface Goods {

    companion object {
        const val SIZE_MAX = 999
        const val INFINITE = -1
    }

    /**
     * 商品是有数量的
     */
    val size: Int

    /**
     * 它被使用了一次
     */
    fun useOnce()

}