package com.lollipop.wifip2p

import android.content.Intent

interface WPStateListener {

//    WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
//        // Check to see if Wi-Fi is enabled and notify appropriate activity
//        // 状态发生变更，因此需要在此时检测是否可用，以及更新交互与UI
//    }
//
//    WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
//        // Call WifiP2pManager.requestPeers() to get a list of current peers
//        // 获取当前可用设备列表，此时表示设备列表发生变更了
//    }
//
//    WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
//        // Respond to new connection or disconnections
//        // 连接状态变更
//    }
//
//    WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
//        // Respond to this device's wifi state changing
//        // 设备自身状态变更
//    }

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