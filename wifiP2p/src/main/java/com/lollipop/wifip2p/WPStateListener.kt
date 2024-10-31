package com.lollipop.wifip2p

import android.content.Intent

interface WPStateListener {

    /**
     * 功能状态发生变化
     * 可能不可用
     */
    fun onStateChanged(intent: Intent, enable: Boolean)

    /**
     * 设备列表发生变化
     * 建议此时刷新列表
     */
    fun onPeersChanged(intent: Intent)

    /**
     * 连接状态发生变化
     */
    fun onConnectionChanged(intent: Intent)

    /**
     * 本机设备信息发生变化
     */
    fun onDeviceChanged(intent: Intent)

}