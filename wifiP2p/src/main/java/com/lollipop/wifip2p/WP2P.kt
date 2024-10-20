package com.lollipop.wifip2p

import android.app.Activity

object WP2P {

    fun with(activity: Activity, callback: WPActivityDelegate.Callback): WPActivityDelegate {
        return WPActivityDelegate(activity, callback)
    }

}