package com.lollipop.wear.ps.engine

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.ViewGroup
import com.lollipop.wear.ps.engine.sprite.SpriteInfo
import com.lollipop.wear.ps.engine.sprite.SpritePlayer
import com.lollipop.wear.ps.engine.sprite.SpriteToward
import com.lollipop.wear.ps.engine.state.SpriteDataStore
import kotlin.random.Random

class SpriteControl(
    private val player: SpritePlayer
) : SpriteDataStore.OnChangeListener {

    companion object {
        private const val SPEED_DP = 1F
    }

    private var infoMode = -1

    private var container: ViewGroup? = null

    private var containerWidth = 0
    private var containerHeight = 0

    private val handler by lazy {
        Handler(Looper.getMainLooper())
    }

    private val actionTask = Runnable {
        nextAction()
    }

    private var speed = 1

    private val currentToward: SpriteToward
        get() = player.spriteToward

    fun onResume() {
        Log.d("SpriteControl", "onResume")
        speed = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            SPEED_DP,
            player.resources.displayMetrics
        ).toInt()
        container = player.parent?.parent as? ViewGroup
        if (infoMode != SpriteDataStore.updateMode) {
            infoMode = SpriteDataStore.updateMode
            player.setSpriteInfo(SpriteDataStore.currentSprite)
        }
        SpriteDataStore.addOnChangeListener(this)
        player.resume()
        nextAction()
    }

    override fun onSpriteChange(spriteInfo: SpriteInfo) {
        infoMode = SpriteDataStore.updateMode
        player.setSpriteInfo(SpriteDataStore.currentSprite)
    }

    fun onPause() {
        // 清空容器，这样就不会再绘制了
        container = null
        SpriteDataStore.removeOnChangeListener(this)
        player.pause()
    }

    private fun nextAction() {
        Log.d("SpriteControl", "nextAction")
        val c = container ?: return
        containerWidth = c.width
        containerHeight = c.height
        if (containerWidth <= 0 || containerHeight <= 0) {
            c.post { postNext() }
            return
        }
        // 下一个动作，比如移动一点，或者换个方向
        if (allowPassage(currentToward) && randomAb(0.9F)) {
            // 如果当前方向还可以移动，那么它有50%的概率继续向前
            runStep()
        } else {
            // 否则的情况下，就是我们转个方向，这里我们就不走，只是转一下,选择之前，我们需要确定它有哪些方向可以走
            val nextToward = SpriteToward.entries.filter {
                allowPassage(it) && it != currentToward
            }.random()
            player.changedToward(nextToward)
        }
        postNext()
    }

    private fun postNext() {
        handler.removeCallbacks(actionTask)
        handler.postDelayed(actionTask, player.frameInterval)
    }

    private fun randomAb(weight: Float): Boolean {
        return Random.nextFloat() < weight
    }

    private fun runStep() {
        when (currentToward) {
            SpriteToward.Left -> {
                player.translationX -= speed
                if (player.translationX < 0) {
                    player.translationX = 0F
                }
            }

            SpriteToward.Up -> {
                player.translationY -= speed
                if (player.translationY < 0) {
                    player.translationY = 0F
                }
            }

            SpriteToward.Right -> {
                player.translationX += speed
                val maxX = containerWidth - player.width
                if (player.translationX > maxX) {
                    player.translationX = maxX.toFloat()
                }
            }

            SpriteToward.Down -> {
                player.translationY += speed
                val maxY = containerHeight - player.height
                if (player.translationY > maxY) {
                    player.translationY = maxY.toFloat()
                }
            }
        }
    }

    private fun allowPassage(toward: SpriteToward): Boolean {
        when (toward) {
            SpriteToward.Left -> {
                return player.translationX > 0
            }

            SpriteToward.Up -> {
                return player.translationY > 0
            }

            SpriteToward.Right -> {
                val maxX = containerWidth - player.width
                return player.translationX < maxX
            }

            SpriteToward.Down -> {
                val maxY = containerHeight - player.height
                return player.translationY < maxY
            }
        }
    }

}