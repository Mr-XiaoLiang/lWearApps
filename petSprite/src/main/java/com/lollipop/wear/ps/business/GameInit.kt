package com.lollipop.wear.ps.business

import android.app.Application
import com.lollipop.wear.ps.business.event.FoodsEventProvider
import com.lollipop.wear.ps.business.event.LuckyEventProvider
import com.lollipop.wear.ps.business.event.PhysiologyEventProvider
import com.lollipop.wear.ps.business.event.ToysEventProvider
import com.lollipop.wear.ps.business.event.WorkEventProvider
import com.lollipop.wear.ps.engine.GameController
import com.lollipop.wear.ps.engine.attr.AttributeManager
import com.lollipop.wear.ps.engine.log.GameLogDelegate
import com.lollipop.wear.ps.engine.state.BackpackManager
import com.lollipop.wear.ps.engine.state.GameStateManager
import com.lollipop.wear.ps.engine.state.impl.HealthState
import com.lollipop.wear.ps.engine.state.impl.MoodState
import com.lollipop.wear.ps.engine.state.impl.RichState
import com.lollipop.wear.ps.engine.state.impl.SatiationState

object GameInit {

    private var isInit = false

    fun init(app: Application) {
        if (isInit) {
            return
        }
        isInit = true
        // 先注册提供者
        registerProvider(app)
        // 然后初始化管理器
        initManager(app)
        // 最后绑定监听器
        bindListener(app)
    }

    private fun registerProvider(app: Application) {
        GameStateManager.apply {
            register(RichState)
            register(HealthState)
            register(MoodState)
            register(SatiationState)
        }
        GameController.apply {
            addProvider(FoodsEventProvider)
            addProvider(ToysEventProvider)
            addProvider(WorkEventProvider)
            addProvider(LuckyEventProvider)
            addProvider(PhysiologyEventProvider)
        }
    }

    private fun initManager(app: Application) {
        GameStateManager.init(app)
        BackpackManager.init(app)
        AttributeManager.init(app)
        GameController.init(app)
    }

    private fun bindListener(app: Application) {
        GameStateManager.addOptionListener(GameLogDelegate(app))
        GameStateManager.addOptionListener(BackpackManager)
    }

}