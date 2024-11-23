package com.lollipop.wear.ps

import android.animation.Animator
import android.animation.ValueAnimator
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import com.lollipop.wear.animation.AnimationHelper
import com.lollipop.wear.animation.converter.Alpha
import com.lollipop.wear.animation.converter.TranslationX
import com.lollipop.wear.animation.dsl.logOnDebug
import com.lollipop.wear.animation.dsl.withView
import com.lollipop.wear.animation.end.HideOnClose
import com.lollipop.wear.animation.start.ShowOnStart
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

    private val contentPanelController by lazy {
        AnimationHelper()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        dashboardDelegate.onCreate()
        initContentPanelController()
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

    private fun initContentPanelController() {
        contentPanelController.build {
            withView(binding.contentPanel) {
                HideOnClose()
                ShowOnStart()
                TranslationX()
            }
            withView(binding.contentPanelBackground) {
                HideOnClose()
                ShowOnStart()
                Alpha()
            }
            withView(binding.contentPanelBackgroundRing) {
                HideOnClose()
                ShowOnStart()
                Alpha()
            }
        }
        binding.contentPanel.isInvisible = true
        binding.contentPanelBackground.isInvisible = true
        binding.contentPanelBackgroundRing.isInvisible = true
        binding.contentPanelBackgroundRing.setOnClickListener {
            contentPanelController.close()
        }
        binding.backpackButton.setOnClickListener {
            contentPanelController.expand()
        }
    }

    private fun updateToward(player: SpritePlayer, toward: SpriteToward) {
        if (player.spriteToward == toward) {
            player.changedToward(toward, !player.isRunning)
        } else {
            player.changedToward(toward, true)
        }
    }

}