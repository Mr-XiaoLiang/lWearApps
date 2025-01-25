package com.lollipop.file.sender

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.file.sender.databinding.ActivityConnectListBinding
import com.lollipop.file.sender.databinding.ItemConnectItemBinding
import com.lollipop.file.sender.ftp.ConnectInfo
import com.lollipop.file.sender.ftp.FtpManager
import com.lollipop.file.sender.ftp.RequestResult

class ConnectListActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityConnectListBinding.inflate(layoutInflater)
    }

    private val connectInfoList = ArrayList<ConnectInfo>()
    private val itemAdapter = ItemAdapter(connectInfoList, ::onItemClick)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        initInsets()
        initView()
    }

    private fun initInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.actionBar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.recyclerView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initView() {
        binding.scanButton.setOnClickListener {
            startActivity(Intent(this, ScanActivity::class.java))
        }
        binding.customButton.setOnClickListener {
            startActivity(Intent(this, CustomLoginActivity::class.java))
        }
        binding.recyclerView.adapter = itemAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

    }

    private fun onItemClick(info: ConnectInfo) {
        // TODO
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun refresh() {
        connectInfoList.clear()
        connectInfoList.addAll(FtpManager.connectList())
        itemAdapter.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        refresh()
    }

    override fun onPause() {
        super.onPause()
        // 页面被暂停的时候，保存数据
        FtpManager.save(this)
    }

    private class ItemAdapter(
        private val data: List<ConnectInfo>,
        private val onItemClick: (ConnectInfo) -> Unit
    ) : RecyclerView.Adapter<ItemHolder>() {

        private var layoutInflater: LayoutInflater? = null

        private fun getLayoutInflater(parent: ViewGroup): LayoutInflater {
            return layoutInflater ?: LayoutInflater.from(parent.context).also {
                layoutInflater = it
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            return ItemHolder(
                ItemConnectItemBinding.inflate(getLayoutInflater(parent), parent, false),
                ::onItemClick
            )
        }

        private fun onItemClick(position: Int) {
            if (position < 0 || position >= data.size) {
                return
            }
            onItemClick(data[position])
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            holder.bind(data[position])
        }

    }

    private class ItemHolder(
        val binding: ItemConnectItemBinding,
        val onClickCallback: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private var changeMode = 0

        init {
            binding.root.setOnClickListener {
                onItemClick()
            }
        }

        private fun onItemClick() {
            onClickCallback(adapterPosition)
        }

        private fun updateMode() {
            changeMode++
            if (changeMode == Int.MAX_VALUE) {
                changeMode = 0
            }
        }

        fun bind(info: ConnectInfo) {
            updateMode()
            binding.nameView.text = info.displayName
            binding.stateView.text = ""
            binding.stateIcon.setImageDrawable(null)
            updateState(info)
        }

        private fun updateState(info: ConnectInfo) {
            val currentMode = changeMode
            val client = FtpManager.findClient(info.token)
            if (client == null) {
                updateState(false)
                return
            }
            client.isConnected { result ->
                if (currentMode == changeMode) {
                    when (result) {
                        is RequestResult.Success -> {
                            updateState(result.data)
                        }

                        is RequestResult.Failure -> {
                            updateState(false)
                        }
                    }
                }
            }
        }

        private fun updateState(isConnected: Boolean) {
            binding.stateView.setText(
                if (isConnected) {
                    R.string.state_connected
                } else {
                    R.string.state_disconnected
                }
            )
            binding.stateIcon.setImageResource(
                if (isConnected) {
                    R.drawable.baseline_link_24
                } else {
                    R.drawable.baseline_link_off_24
                }
            )
        }
    }

}