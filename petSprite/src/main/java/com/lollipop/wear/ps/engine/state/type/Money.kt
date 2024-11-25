package com.lollipop.wear.ps.engine.state.type

/**
 * 钱
 * 值钱的物品
 * 可以代替钱的物品
 */
interface Money {

    /**
     * 钱的数量
     * 整数，方便扣除余额
     */
    val amount: Int

}