package com.lollipop.wear.phone.send

import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pInfo
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.lollipop.wear.phone.R
import com.lollipop.wear.phone.databinding.ActivityWifiP2pBinding
import com.lollipop.wifip2p.WP2P
import com.lollipop.wifip2p.WPActivityDelegate

class WifiP2pActivity : AppCompatActivity(), WPActivityDelegate.Callback {

    private val delegate by lazy {
        WP2P.with(this, this)
    }

    private val binding by lazy {
        ActivityWifiP2pBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onStateChanged(supported: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onPeersChanged(peers: WifiP2pDeviceList?) {
        TODO("Not yet implemented")
    }

    override fun onChannelDisconnected() {
        TODO("Not yet implemented")
    }

    override fun onConnectionChanged() {
        TODO("Not yet implemented")
    }

    override fun onDeviceChanged() {
        TODO("Not yet implemented")
    }

    override fun onConnectionAvailable(info: WifiP2pInfo) {
        TODO("Not yet implemented")
    }
}