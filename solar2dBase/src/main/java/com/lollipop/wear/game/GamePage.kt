package com.lollipop.wear.game

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View

interface GamePage {

    fun beforeCreate(activity: GameActivity, bundle: Bundle?): Bundle? {
        return bundle
    }

    fun afterCreate(activity: GameActivity, bundle: Bundle?) {

    }

    fun getGameDirectory(activity: GameActivity): String? {
        return null
    }

    fun getForegroundView(activity: GameActivity): View?

    fun beforeStart(activity: GameActivity) {}

    fun afterStart(activity: GameActivity) {}

    fun beforeResume(activity: GameActivity) {}

    fun afterResume(activity: GameActivity) {}

    fun beforePause(activity: GameActivity) {}

    fun afterPause(activity: GameActivity) {}

    fun beforeStop(activity: GameActivity) {}

    fun afterStop(activity: GameActivity) {}

    fun beforeDestroy(activity: GameActivity) {}

    fun afterDestroy(activity: GameActivity) {}

    fun beforeNewIntent(activity: GameActivity, intent: Intent?): Intent? {
        return intent
    }

    fun afterNewIntent(activity: GameActivity, intent: Intent?) {

    }

    fun beforeConfigurationChanged(
        activity: GameActivity,
        newConfig: Configuration
    ): Configuration {
        return newConfig
    }

    fun afterConfigurationChanged(activity: GameActivity, newConfig: Configuration) {

    }

}