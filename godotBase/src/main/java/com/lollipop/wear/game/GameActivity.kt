package com.lollipop.wear.game

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.lollipop.wear.basic.findTypedFragment
import com.lollipop.wear.game.databinding.ActivityGameBaseBinding
import org.godotengine.godot.Godot
import org.godotengine.godot.GodotFragment
import org.godotengine.godot.GodotHost
import org.godotengine.godot.utils.ProcessPhoenix

class GameActivity : AppCompatActivity(), GodotHost {

    companion object {
        const val EXTRA_FORCE_QUIT = "force_quit_requested"
        const val EXTRA_NEW_LAUNCH = "new_launch_requested"
    }

    private val binding by lazy {
        ActivityGameBaseBinding.inflate(layoutInflater)
    }

    private val godotBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            findGodot()?.onBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        val newState = GameConfig.invoke {
            it.beforeCreate(this, savedInstanceState)
        } ?: savedInstanceState
        super.onCreate(newState)
        setContentView(binding.root)
        GameConfig.invoke { it.getForegroundView(this) }?.let {
            binding.foregroundContainer.addView(
                it,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        onBackPressedDispatcher.addCallback(godotBackPressedCallback)
        GameConfig.invoke { it.afterCreate(this, newState) }
    }

    private fun findGodot(): GodotFragment? {
        return supportFragmentManager.findTypedFragment<GodotFragment>()
    }

    override fun onStart() {
        GameConfig.invoke { it.beforeStart(this) }
        super.onStart()
        GameConfig.invoke { it.afterStart(this) }
    }

    override fun onResume() {
        GameConfig.invoke { it.beforeResume(this) }
        super.onResume()
        GameConfig.invoke { it.afterResume(this) }
    }

    override fun onPause() {
        GameConfig.invoke { it.beforePause(this) }
        super.onPause()
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
        this.intent = newIntent
        if (newIntent != null) {
            handleStartIntent(newIntent, false)
            findGodot()?.onNewIntent(newIntent)
        }
        GameConfig.invoke { it.afterNewIntent(this, newIntent) }
    }

    override fun onGodotForceQuit(instance: Godot) {
        runOnUiThread { terminateGodotInstance(instance) }
    }

    private fun terminateGodotInstance(instance: Godot) {
        findGodot()?.let {
            if (instance === it.godot) {
                ProcessPhoenix.forceQuit(this)
            }
        }
    }

    override fun onGodotRestartRequested(instance: Godot) {
        runOnUiThread {
            findGodot()?.let {
                if (instance === it.godot) {
                    // It's very hard to properly de-initialize Godot on Android to restart the game
                    // from scratch. Therefore, we need to kill the whole app process and relaunch it.
                    //
                    // Restarting only the activity, wouldn't be enough unless it did proper cleanup (including
                    // releasing and reloading native libs or resetting their state somehow and clearing static data).
                    ProcessPhoenix.triggerRebirth(this)
                }
            }
        }
    }

    private fun handleStartIntent(intent: Intent, newLaunch: Boolean) {
        val forceQuitRequested = intent.getBooleanExtra(EXTRA_FORCE_QUIT, false)
        if (forceQuitRequested) {
            ProcessPhoenix.forceQuit(this)
            return
        }
        if (!newLaunch) {
            val newLaunchRequested = intent.getBooleanExtra(EXTRA_NEW_LAUNCH, false)
            if (newLaunchRequested) {
                val restartIntent = Intent(intent).putExtra(EXTRA_NEW_LAUNCH, false)
                ProcessPhoenix.triggerRebirth(this, restartIntent)
                return
            }
        }
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
        GameConfig.invoke { it.afterDestroy(this) }
    }

    @Deprecated("Deprecated in Java")
    @CallSuper
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        findGodot()?.onActivityResult(requestCode, resultCode, data)
    }

    @CallSuper
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        findGodot()?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun getActivity(): Activity {
        return this
    }

    override fun getGodot(): Godot? {
        return findGodot()?.godot
    }

}