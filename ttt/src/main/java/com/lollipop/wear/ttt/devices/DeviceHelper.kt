package com.lollipop.wear.ttt.devices

import android.content.Context

val Context.isScreenRound: Boolean
    get() {
        return resources.configuration.isScreenRound
    }