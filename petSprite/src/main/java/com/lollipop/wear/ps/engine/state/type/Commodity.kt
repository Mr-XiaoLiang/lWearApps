package com.lollipop.wear.ps.engine.state.type

/**
 * 商品
 * 可交易的
 */
interface Commodity {
    /**
     * 价格，单价
     * 这是个整数（方便计算和扣除余额）
     */
    val price: Int
}