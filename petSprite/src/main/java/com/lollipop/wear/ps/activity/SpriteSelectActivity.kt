package com.lollipop.wear.ps.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.wear.ps.business.SpriteAssets
import com.lollipop.wear.ps.databinding.ActivitySpriteSelectBinding
import com.lollipop.wear.ps.databinding.ItemSpriteBinding
import com.lollipop.wear.ps.engine.sprite.SpriteInfo
import com.lollipop.wear.ps.utils.RoundPageListAdapter
import org.json.JSONObject

class SpriteSelectActivity : AppCompatActivity() {

    companion object {

        private const val PARAMS_SPRITE_INFO = "params_sprite_info"
        const val RESULT_SPRITE_INFO = "result_sprite_info"

        private fun setResult(activity: SpriteSelectActivity, info: SpriteInfo) {
            val intent = Intent()
            intent.putExtra(RESULT_SPRITE_INFO, info.toJson().toString())
            activity.setResult(RESULT_OK, intent)
        }

    }

    private val spriteList = ArrayList<Any>()

    private val binding by lazy {
        ActivitySpriteSelectBinding.inflate(layoutInflater)
    }

    private val adapter by lazy {
        ItemAdapter(spriteList, ::onItemClick)
    }

    private var selectedInfo: SpriteInfo = SpriteInfo.None

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        binding.selectedButton.setOnClickListener {
            onConfirmClick()
        }

        adapter.bindViewSize(binding.root)

        initData()

        setResult(RESULT_CANCELED)
    }

    private fun onConfirmClick() {
        val info = SpriteAssets.filterEmptySprite(selectedInfo)
        setResult(this, info)
        onBackPressedDispatcher.onBackPressed()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initData() {
        selectedInfo = SpriteInfo.parse(intent.getStringExtra(PARAMS_SPRITE_INFO) ?: "")
        spriteList.clear()
        spriteList.add(RoundPageListAdapter.SpaceInfo)
        spriteList.addAll(SpriteAssets.spriteList)
        spriteList.add(RoundPageListAdapter.SpaceInfo)
        adapter.notifyDataSetChanged()
        updateSelectedSprite()
    }

    private fun onItemClick(info: SpriteInfo) {
        selectedInfo = SpriteAssets.filterEmptySprite(info)
        updateSelectedSprite()
    }

    private fun updateSelectedSprite() {
        if (selectedInfo == SpriteInfo.None) {
            binding.selectedButton.isVisible = false
        } else {
            binding.selectedButton.isVisible = true
            binding.selectedIcon.setSpriteInfo(selectedInfo)
        }
    }

    private class ItemAdapter(
        dataList: List<Any>,
        private val clickListener: (SpriteInfo) -> Unit
    ) : RoundPageListAdapter(dataList) {

        companion object {
            private const val VIEW_TYPE_SPRITE = 1
        }

        override fun onCreateItemHolder(parent: ViewGroup, viewType: Int): ListHolder? {
            if (viewType == VIEW_TYPE_SPRITE) {
                return ItemHolder(
                    ItemSpriteBinding.inflate(getLayoutInflater(parent), parent, false),
                    ::onItemClick
                )
            }
            return null
        }

        private fun onItemClick(position: Int) {
            if (position < 0 || position >= itemCount) {
                return
            }
            val info = dataList[position]
            if (info is SpriteInfo) {
                clickListener(info)
            }
        }

        override fun getItemType(data: Any, position: Int): Int? {
            if (data is SpriteInfo) {
                return VIEW_TYPE_SPRITE
            }
            return null
        }

        override fun onBindItemHolder(holder: ListHolder, position: Int) {
            if (holder is ItemHolder) {
                val any = dataList[position]
                if (any is SpriteInfo) {
                    holder.bind(any)
                }
            }
        }
    }

    private class ItemHolder(
        val binding: ItemSpriteBinding,
        private val clickListener: (Int) -> Unit
    ) :
        RoundPageListAdapter.ListHolder(binding.root) {

        init {
            binding.itemContentView.setOnClickListener { onClick() }
        }

        private fun onClick() {
            clickListener(adapterPosition)
        }

        fun bind(info: SpriteInfo) {
            binding.spritePlayer.setSpriteInfo(info)
            binding.nameView.text = info.name
        }
    }

    class ResultContracts : ActivityResultContract<SpriteInfo, SpriteInfo>() {

        override fun createIntent(context: Context, input: SpriteInfo): Intent {
            return Intent(context, SpriteSelectActivity::class.java).apply {
                putExtra(PARAMS_SPRITE_INFO, input.toJson().toString())
            }
        }

        override fun parseResult(resultCode: Int, intent: Intent?): SpriteInfo {
            if (resultCode != RESULT_OK) {
                return SpriteInfo.None
            }
            intent ?: return SpriteInfo.None
            val json = intent.getStringExtra(RESULT_SPRITE_INFO)
            if (json == null || TextUtils.isEmpty(json)) {
                return SpriteInfo.None
            }
            try {
                return SpriteInfo.parse(JSONObject(json))
            } catch (_: Throwable) {
            }
            return SpriteInfo.None
        }

    }

}