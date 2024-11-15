package com.lollipop.wear.ps.engine.state

import android.content.Context

interface GameOption {

    val key: String

    val name: Int

    fun getLogInfo(context: Context): String

}