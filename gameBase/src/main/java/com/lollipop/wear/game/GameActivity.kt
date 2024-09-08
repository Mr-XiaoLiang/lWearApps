package com.lollipop.wear.game

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.lollipop.wear.game.databinding.ActivityGameBaseBinding

class GameActivity : ComponentActivity() {

    private val binding by lazy {
        ActivityGameBaseBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        val newState = GameConfig.invoke {
            it.beforeCreate(this, savedInstanceState)
        } ?: savedInstanceState
        super.onCreate(newState)
        setContentView(binding.root)
        val dir = GameConfig.invoke { it.getGameDirectory(this) } ?: GameConfig.DEFAULT_GAME_DIR
        binding.coronaView.init(dir)
        GameConfig.invoke { it.getForegroundView(this) }?.let {
            binding.foregroundContainer.addView(
                it,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        GameConfig.invoke { it.afterCreate(this, newState) }
    }

    override fun onStart() {
        GameConfig.invoke { it.beforeStart(this) }
        super.onStart()
        GameConfig.invoke { it.afterStart(this) }
    }

    override fun onResume() {
        GameConfig.invoke { it.beforeResume(this) }
        super.onResume()
        binding.coronaView.resume()
        GameConfig.invoke { it.afterResume(this) }
    }

    override fun onPause() {
        GameConfig.invoke { it.beforePause(this) }
        super.onPause()
        binding.coronaView.pause()
        GameConfig.invoke { it.afterPause(this) }
    }

    override fun onStop() {
        GameConfig.invoke { it.beforeStop(this) }
        super.onStop()
        GameConfig.invoke { it.afterStop(this) }
    }

    override fun onNewIntent(intent: Intent?) {
        val newIntent = GameConfig.invoke { it.beforeNewIntent(this, intent) } ?: intent
        super.onNewIntent(newIntent)
        GameConfig.invoke { it.afterNewIntent(this, newIntent) }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        val newState = GameConfig.invoke {
            it.beforeConfigurationChanged(this, newConfig)
        } ?: newConfig
        super.onConfigurationChanged(newState)
        GameConfig.invoke { it.afterConfigurationChanged(this, newState) }
    }

    override fun onDestroy() {
        GameConfig.invoke { it.beforeDestroy(this) }
        super.onDestroy()
        binding.coronaView.destroy()
        GameConfig.invoke { it.afterDestroy(this) }
    }

}