package com.lollipop.wear.ps

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.lollipop.wear.ps.business.MainDashboardDelegate
import com.lollipop.wear.ps.databinding.ActivityMainBinding
import com.lollipop.wear.ps.engine.sprite.SpritePlayer
import com.lollipop.wear.ps.engine.sprite.SpriteToward

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val dashboardDelegate by lazy {
        MainDashboardDelegate(this, binding.dashboardPanel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        dashboardDelegate.onCreate()
        Log.w("Test", "onCreate")
//        binding.spritePlayer.setSpriteInfo(
//            SpriteInfo.createBy4x4(256) { left, up, right, down ->
//                SpriteInfo.FromAssets(
//                    "sprite/PIKACHU.png",
//                    up = up,
//                    down = down,
//                    left = left,
//                    right = right
//                )
//            }
//        )

    }

    private fun updateToward(player: SpritePlayer, toward: SpriteToward) {
        if (player.spriteToward == toward) {
            player.changedToward(toward, !player.isRunning)
        } else {
            player.changedToward(toward, true)
        }
    }

    private class ContentPanelController(
        private val contentPanel: View,
        private val contentPanelBackground: View,
        private val openButton: View
    ) : ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {

        companion object {
            private const val ANIMATOR_DURATION = 300L
            private const val ANIMATOR_VALUE_MIN = 0F
            private const val ANIMATOR_VALUE_MAX = 1F
        }

        private val valueAnimator by lazy {
            ValueAnimator().apply {
                addUpdateListener(this@ContentPanelController)
                addListener(this@ContentPanelController)
            }
        }

        init {
            openButton.setOnClickListener {
                open()
            }
        }

        fun close() {
            TODO("Not yet implemented")
        }

        fun open() {
            TODO("Not yet implemented")
        }

        override fun onAnimationUpdate(animation: ValueAnimator) {
            TODO("Not yet implemented")
        }

        override fun onAnimationStart(animation: Animator) {
            TODO("Not yet implemented")
        }

        override fun onAnimationEnd(animation: Animator) {
            TODO("Not yet implemented")
        }

        override fun onAnimationCancel(animation: Animator) {
            TODO("Not yet implemented")
        }

        override fun onAnimationRepeat(animation: Animator) {
            TODO("Not yet implemented")
        }

    }

}