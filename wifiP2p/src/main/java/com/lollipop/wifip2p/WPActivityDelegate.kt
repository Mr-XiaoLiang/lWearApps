package com.lollipop.wifip2p

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import androidx.core.content.ContextCompat

class WPActivityDelegate(
    val activity: Activity
) : WPStateListener, WifiP2pManager.ChannelListener {

    private val receiver = WiFiDirectBroadcastReceiver(this)

    private val manager: WifiP2pManager? by lazy(LazyThreadSafetyMode.NONE) {
        activity.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager?
    }

    private var channel: WifiP2pManager.Channel? = null

    /**
     * 生命周期函数，在Activity的onCreate中调用
     * 用于初始化操作
     */
    fun onCreate() {
        channel = manager?.initialize(activity, activity.mainLooper, this)
    }

    /**
     * 生命周期函数，在Activity的onResume中调用
     * 用于激活广播
     */
    fun onResume() {
        ContextCompat.registerReceiver(
            activity,
            receiver,
            receiver.intentFilter,
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    /**
     * 生命周期函数，在Activity的onPause中调用
     * 用于取消激活
     */
    fun onPause() {
        activity.unregisterReceiver(receiver)
    }

    /**
     * 发现设备
     */
    fun discoverPeers(callback: (success: Boolean) -> Unit) {
        manager?.discoverPeers(
            channel,
            object : WifiP2pManager.ActionListener {

                override fun onSuccess() {
                    callback(true)
                }

                override fun onFailure(reasonCode: Int) {
                    callback(false)
                }
            }
        )
    }

    fun requestPeers(callback: (success: Boolean) -> Unit) {
        manager?.requestPeers(
            channel,
            object : WifiP2pManager.PeerListListener {
                override fun onPeersAvailable(peers: WifiP2pDeviceList) {
                    callback(true)
                }
            }
        )
    }

    override fun onChannelDisconnected() {
        TODO("Not yet implemented")
    }

    override fun onStateChanged(intent: Intent, enable: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onPeersChanged(intent: Intent) {
        TODO("Not yet implemented")
    }

    override fun onConnectionChanged(intent: Intent) {
        TODO("Not yet implemented")
    }

    override fun onDeviceChanged(intent: Intent) {
        TODO("Not yet implemented")
    }

}