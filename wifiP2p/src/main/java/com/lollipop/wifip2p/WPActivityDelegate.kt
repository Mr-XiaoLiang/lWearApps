package com.lollipop.wifip2p

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import androidx.core.content.ContextCompat

class WPActivityDelegate(
    val activity: Activity,
    val callback: Callback
) : WPStateListener, WifiP2pManager.ChannelListener, WifiP2pManager.ConnectionInfoListener {

    private val receiver = WiFiDirectBroadcastReceiver(this)

    private val manager: WifiP2pManager? by lazy(LazyThreadSafetyMode.NONE) {
        activity.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager?
    }

    private var channel: WifiP2pManager.Channel? = null

    /**
     * 默认认为是支持的
     */
    private var isP2pSupport = true

    /**
     * 设备列表
     */
    private val peerListListener = WifiP2pManager.PeerListListener { peers ->
        deviceList = peers
        callback.onPeersChanged(peers)
    }

    /**
     * 是否激活状态
     */
    private var isResumed = false

    /**
     * 最新的设备列表
     */
    private var deviceList: WifiP2pDeviceList? = null

    /**
     * 已连接的设备（只保证连接时不报错）
     */
    val connectedDeviceList = mutableMapOf<String, WifiP2pDevice>()

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
        isResumed = true
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
        isResumed = false
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

    fun requestPeers() {
        manager?.requestPeers(channel, peerListListener)
    }

    fun isConnected(address: String): Boolean {
        return connectedDeviceList.containsKey(address)
    }

    fun isConnected(device: WifiP2pDevice): Boolean {
        return isConnected(device.deviceAddress)
    }

    fun saveConnectedDevice(device: WifiP2pDevice) {
        connectedDeviceList[device.deviceAddress] = device
    }

    fun clearConnectedDevice() {
        connectedDeviceList.clear()
    }

    fun removeConnectedDevice(address: String) {
        connectedDeviceList.remove(address)
    }

    fun removeConnectedDevice(device: WifiP2pDevice) {
        removeConnectedDevice(device.deviceAddress)
    }

    /**
     * 设备连接
     */
    fun connect(info: WifiP2pDevice, result: (Boolean) -> Unit) {
        val m = manager ?: return
        m.connect(
            channel,
            WifiP2pConfig().apply {
                deviceAddress = info.deviceAddress
            },
            object : WifiP2pManager.ActionListener {

                override fun onSuccess() {
                    saveConnectedDevice(info)
                    result(true)
                }

                override fun onFailure(reason: Int) {
                    result(false)
                }

            }
        )
    }

    /**
     * 移除分组
     */
    fun removeGroup(result: (Boolean) -> Unit) {
        manager?.removeGroup(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                result(true)
            }

            override fun onFailure(reason: Int) {
                result(false)
            }
        })
    }

    /**
     * 尝试获取连接
     */
    fun requestConnection() {
        manager?.requestConnectionInfo(channel, this)
    }

    /**
     * 当接口链接断开
     */
    override fun onChannelDisconnected() {
        callback.onChannelDisconnected()
    }

    override fun onStateChanged(intent: Intent, enable: Boolean) {
        isP2pSupport = enable
        callback.onStateChanged(enable)
    }

    override fun onPeersChanged(intent: Intent) {
        if (isResumed) {
            requestPeers()
        }
    }

    override fun onConnectionChanged(intent: Intent) {
        callback.onConnectionChanged()
    }

    override fun onDeviceChanged(intent: Intent) {
        callback.onDeviceChanged()
    }

    override fun onConnectionInfoAvailable(info: WifiP2pInfo?) {
        info ?: return
        callback.onConnectionAvailable(info)
    }

    interface Callback {

        fun onStateChanged(supported: Boolean)

        fun onPeersChanged(peers: WifiP2pDeviceList?)

        fun onChannelDisconnected()

        fun onConnectionChanged()

        fun onDeviceChanged()

        fun onConnectionAvailable(info: WifiP2pInfo)

    }

}