package com.lollipop.wear.ps

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lollipop.wear.ps.activity.ContentMenuActivity
import com.lollipop.wear.ps.business.MainDashboardDelegate
import com.lollipop.wear.ps.databinding.ActivityMainBinding
import com.lollipop.wear.ps.engine.GameController
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
        binding.backpackButtonClickDelegate.setOnClickListener {
            ContentMenuActivity.start(this)
        }
//        binding.contentPageIndicator.indicatorCount = pageList.size
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

    override fun onResume() {
        super.onResume()
        GameController.findFuture(this)
    }

    private fun updateToward(player: SpritePlayer, toward: SpriteToward) {
        if (player.spriteToward == toward) {
            player.changedToward(toward, !player.isRunning)
        } else {
            player.changedToward(toward, true)
        }
    }

}