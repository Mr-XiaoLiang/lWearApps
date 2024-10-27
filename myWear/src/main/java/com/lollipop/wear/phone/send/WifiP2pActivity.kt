package com.lollipop.wear.phone.send

import android.annotation.SuppressLint
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.wear.phone.BuildConfig
import com.lollipop.wear.phone.databinding.ActivityWifiP2pBinding
import com.lollipop.wear.phone.databinding.ItemSendDeviceBinding
import com.lollipop.wifip2p.WP2P
import com.lollipop.wifip2p.WPActivityDelegate


class WifiP2pActivity : AppCompatActivity(), WPActivityDelegate.Callback {

    private val delegate by lazy {
        WP2P.with(this, this)
    }

    private val binding by lazy {
        ActivityWifiP2pBinding.inflate(layoutInflater)
    }

    private val deviceList = ArrayList<WifiP2pDevice>()
    private val deviceListAdapter = DeviceListAdapter(deviceList, ::onDeviceClick)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // 设置浅色状态栏时的界面显示
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.actionBar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }
        binding.devicesList.apply {
            layoutManager = LinearLayoutManager(this@WifiP2pActivity, RecyclerView.VERTICAL, false)
            adapter = deviceListAdapter
        }
        delegate.onCreate()
    }

    private fun showDeviceContentLoading() {
        runOnUiThread {
            binding.deviceContentLoadingView.isIndeterminate = true
            binding.deviceContentLoadingView.show()
        }
    }

    private fun hideDeviceContentLoading() {
        runOnUiThread {
            binding.deviceContentLoadingView.hide()
        }
    }

    override fun onResume() {
        super.onResume()
        delegate.onResume()
        showDeviceContentLoading()
        delegate.discoverPeers {
            hideDeviceContentLoading()
        }
    }

    override fun onPause() {
        super.onPause()
        delegate.onPause()
    }

    override fun onStateChanged(supported: Boolean) {
        log { "onStateChanged: $supported" }
        // TODO("Not yet implemented")
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onPeersChanged(peers: WifiP2pDeviceList?) {
        log { "onPeersChanged: $peers" }
        runOnUiThread {
            deviceList.clear()
            peers?.deviceList?.let { deviceList.addAll(it) }
            deviceListAdapter.notifyDataSetChanged()
            log { "onPeersChanged: itemCount = ${deviceList.size}" }
        }
    }

    private fun onDeviceClick(device: WifiP2pDevice) {
        log { "onDeviceClick: $device" }
        delegate.connect(device) {
            log { "onDeviceClick: result = $it" }
        }
    }

    override fun onChannelDisconnected() {
        // TODO("Not yet implemented")
        log { "onChannelDisconnected" }
    }

    override fun onConnectionChanged() {
        // TODO("Not yet implemented")
        log { "onConnectionChanged" }
    }

    override fun onDeviceChanged() {
        // TODO("Not yet implemented")
        log { "onDeviceChanged" }
    }

    override fun onConnectionAvailable(info: WifiP2pInfo) {
        // TODO("Not yet implemented")
        log { "onConnectionAvailable: $info" }
    }

    private fun log(message: () -> String) {
        if (!BuildConfig.DEBUG) {
            return
        }
        Log.i("WifiP2P", message())
    }

    private class DeviceListAdapter(
        val data: List<WifiP2pDevice>,
        val onClick: (WifiP2pDevice) -> Unit
    ) : RecyclerView.Adapter<DeviceInfoHolder>() {

        private var layoutInflater: LayoutInflater? = null

        private fun getLayoutInflater(parent: ViewGroup): LayoutInflater {
            return layoutInflater ?: LayoutInflater.from(parent.context).also {
                layoutInflater = it
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceInfoHolder {
            return DeviceInfoHolder(
                ItemSendDeviceBinding.inflate(getLayoutInflater(parent), parent, false),
                ::onItemClick
            )
        }

        private fun onItemClick(position: Int) {
            if (position == RecyclerView.NO_POSITION) {
                return
            }
            if (position < 0 || position >= data.size) {
                return
            }
            onClick(data[position])
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: DeviceInfoHolder, position: Int) {
            holder.bind(data[position])
        }
    }

    private class DeviceInfoHolder(
        val binding: ItemSendDeviceBinding,
        val onCLickCallback: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                onItemClick()
            }
        }

        private fun onItemClick() {
            onCLickCallback(adapterPosition)
        }

        fun bind(info: WifiP2pDevice) {
            binding.nameView.text = info.deviceName
            binding.addressView.text = info.deviceAddress
        }

    }

}