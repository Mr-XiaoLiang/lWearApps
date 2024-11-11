package com.lollipop.wear.ps

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
        MainDashboardDelegate(binding.gamePanel)
    }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        dashboardDelegate.updateState()
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
        dashboardDelegate.updateState()
    }

    private fun updateToward(player: SpritePlayer, toward: SpriteToward) {
        if (player.spriteToward == toward) {
            player.changedToward(toward, !player.isRunning)
        } else {
            player.changedToward(toward, true)
        }
    }

}