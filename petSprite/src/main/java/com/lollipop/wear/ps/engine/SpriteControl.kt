package com.lollipop.wear.ps.engine

import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import com.lollipop.wear.ps.engine.sprite.SpritePlayer
import com.lollipop.wear.ps.engine.state.SpriteDataStore

class SpriteControl(
    val player: SpritePlayer
) {

    private var infoMode = 0

    private var container: ViewGroup? = null

    private val handler by lazy {
        Handler(Looper.getMainLooper())
    }

    private var speed = 1

    fun onResume() {
        container = player.parent?.parent as? ViewGroup
        if (infoMode != SpriteDataStore.updateMode) {
            infoMode = SpriteDataStore.updateMode
            player.setSpriteInfo(SpriteDataStore.currentSprite)
        }
        nextAction()
    }

    fun onPause() {
        // 清空容器，这样就不会再绘制了
        container = null
    }

    private fun nextAction() {
        container ?: return
        // 下一个动作，比如移动一点，或者换个方向
        // TODO
    }

}