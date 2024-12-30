package com.lollipop.wear.ps.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.wear.ps.R
import com.lollipop.wear.ps.databinding.ItemSimpleOptionBinding
import com.lollipop.wear.ps.engine.sprite.SpriteInfo
import com.lollipop.wear.ps.engine.state.SpriteDataStore
import com.lollipop.wear.ps.utils.RoundPageListAdapter

class ContentMenuActivity : DashboardBasicListActivity() {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, ContentMenuActivity::class.java))
        }
    }

    private val optionList = ArrayList<Any>()

    private val adapter by lazy {
        BackpackOptionAdapter(optionList, ::onItemClick)
    }

    private var spriteSelectLauncher: ActivityResultLauncher<SpriteInfo>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        spriteSelectLauncher = registerForActivityResult(SpriteSelectActivity.ResultContracts()) { info ->
            onSpriteSelectResult(info)
        }

        optionList.add(RoundPageListAdapter.SpaceInfo)
        optionList.addAll(MenuItem.entries)
        optionList.add(RoundPageListAdapter.SpaceInfo)
    }

    private fun onSpriteSelectResult(info: SpriteInfo) {
        SpriteDataStore.updateSprite(info)
    }

    private fun openSpriteSelect() {
        spriteSelectLauncher?.launch(SpriteDataStore.currentSprite)
    }

    override fun initRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        bindViewSize(recyclerView, adapter)
    }

    private fun onItemClick(item: MenuItem) {
        when (item) {
            MenuItem.GameState -> {
                GameStateActivity.start(this)
            }

            MenuItem.GameLog -> {
                GameLogActivity.start(this)
            }

            MenuItem.Backpack -> {
                BackpackActivity.start(this)
            }

            MenuItem.Sprite -> {
                openSpriteSelect()
            }
        }
    }

    private class BackpackOptionAdapter(
        dataList: List<Any>,
        private val onItemClickCallback: (MenuItem) -> Unit
    ) : ListAdapter(dataList) {

        companion object {
            private const val TYPE_OPTION = 2
        }

        override fun onCreateItemHolder(parent: ViewGroup, viewType: Int): ListHolder? {
            if (TYPE_OPTION == viewType) {
                return MenuOptionHolder(
                    ItemSimpleOptionBinding.inflate(
                        getLayoutInflater(
                            parent
                        ), parent, false
                    ),
                    ::onItemClick
                )
            }
            return null
        }

        private fun onItemClick(position: Int) {
            if (position < 0 || position >= dataList.size) {
                return
            }
            val item = dataList[position]
            if (item is MenuItem) {
                onItemClickCallback(item)
            }
        }

        override fun getItemType(data: Any, position: Int): Int? {
            if (data is MenuItem) {
                return TYPE_OPTION
            }
            return null
        }

        override fun onBindItemHolder(holder: ListHolder, position: Int) {
            if (holder is MenuOptionHolder) {
                val any = dataList[position]
                if (any is MenuItem) {
                    holder.bind(any)
                }
            }
        }

    }

    private class MenuOptionHolder(
        binding: ItemSimpleOptionBinding,
        private val onClickCallback: (Int) -> Unit
    ) : RoundPageListAdapter.SimpleHolder(binding) {

        override fun onItemClick() {
            onClickCallback(adapterPosition)
        }

        fun bind(item: MenuItem) {
            binding.nameView.setText(item.label)
        }

    }

    private enum class MenuItem(val label: Int) {

        GameState(R.string.label_game_state),
        GameLog(R.string.label_game_log),
        Backpack(R.string.label_backpack),
        Sprite(R.string.label_sprite),

    }

}