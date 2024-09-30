package com.lollipop.wear.ps

import androidx.appcompat.app.AppCompatActivity
import com.lollipop.wear.ps.databinding.ActivityMainBinding
import com.lollipop.wear.ps.engine.sprite.SpriteInfo
import com.lollipop.wear.ps.engine.sprite.SpritePlayer
import com.lollipop.wear.ps.engine.sprite.SpriteToward

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.spritePlayer.setSpriteInfo(
            SpriteInfo.createBy4x4(256) { left, up, right, down ->
                SpriteInfo.FromAssets(
                    "sprite/PIKACHU.png",
                    up = up,
                    down = down,
                    left = left,
                    right = right
                )
            }
        )
    }

    private fun updateToward(player: SpritePlayer, toward: SpriteToward) {
        if (player.spriteToward == toward) {
            player.changedToward(toward, !player.isRunning)
        } else {
            player.changedToward(toward, true)
        }
    }

}