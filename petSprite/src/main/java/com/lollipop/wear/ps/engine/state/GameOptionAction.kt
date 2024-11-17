package com.lollipop.wear.ps.engine.state

import com.lollipop.wear.ps.R

enum class GameOptionAction(val resId: Int) {

    /**
     * 使用了
     */
    USED(R.string.action_used),

    /**
     * 吃了
     */
    ATE(R.string.action_ate),

    /**
     * 获得了
     */
    GOT(R.string.action_got),

    /**
     * 失去了
     */
    LOST(R.string.action_lost),

    /**
     * 丢弃了
     */
    DISCARDED(R.string.action_discarded),

    /**
     * 开始了某个工作
     */
    STARTED(R.string.action_started),

    /**
     * 完成了什么事情
     */
    DONE(R.string.action_done)


}