package com.lollipop.wear.ps.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.wear.ps.business.options.BackpackMenu
import com.lollipop.wear.ps.business.options.OptionBufferBuilder
import com.lollipop.wear.ps.databinding.ItemOptionBinding
import com.lollipop.wear.ps.engine.state.BackpackItem
import com.lollipop.wear.ps.engine.state.BackpackManager
import com.lollipop.wear.ps.engine.state.GameOption
import com.lollipop.wear.ps.engine.state.GameOptionAction
import com.lollipop.wear.ps.engine.state.GameStateManager
import com.lollipop.wear.ps.engine.state.type.Commodity
import com.lollipop.wear.ps.engine.state.type.Food

class GameOptionListActivity : DashboardBasicListActivity() {

    companion object {

        private const val EXTRA_OPTION = "option"

        fun start(context: Context, option: BackpackMenu) {
            context.startActivity(Intent(context, GameOptionListActivity::class.java).apply {
                putExtra(EXTRA_OPTION, option.key)
            })
        }
    }

    private val optionList = ArrayList<Any>()

    private val adapter by lazy {
        OptionAdapter(optionList, ::onOptionClick)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val menu = BackpackMenu.find(intent.getStringExtra(EXTRA_OPTION) ?: "")
        if (menu == null) {
            finish()
            return
        }
        optionList.add(SpaceInfo)
        optionList.addAll(menu.getOptionList())
        optionList.add(SpaceInfo)
        adapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun initRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        bindViewSize(recyclerView, adapter)
    }

    private fun onOptionClick(option: GameOption) {
        if (option is Food) {
            GameStateManager.onOption(GameOptionAction.ATE, option)
        } else {
            GameStateManager.onOption(GameOptionAction.USED, option)
        }
        GameStateManager.save(this)
    }

    private class OptionAdapter(
        dataList: List<Any>,
        private val onOptionClick: (GameOption) -> Unit
    ) : ListAdapter(dataList) {

        companion object {
            const val TYPE_OPTION = 2
        }

        override fun onCreateItemHolder(parent: ViewGroup, viewType: Int): ListHolder? {
            if (viewType == TYPE_OPTION) {
                return OptionHolder(
                    ItemOptionBinding.inflate(getLayoutInflater(parent), parent, false),
                    this::onItemClick
                )
            }
            return null
        }

        private fun onItemClick(position: Int) {
            if (position < 0 || position >= dataList.size) {
                return
            }
            val d = dataList[position]
            if (d is GameOption) {
                onOptionClick(d)
            }
        }

        override fun getItemType(data: Any, position: Int): Int? {
            if (data is GameOption) {
                return TYPE_OPTION
            }
            return null
        }

        override fun onBindItemHolder(holder: ListHolder, position: Int) {
            if (holder is OptionHolder) {
                val d = dataList[position]
                if (d is GameOption) {
                    holder.bind(d)
                }
            }
        }

    }

    private class OptionHolder(
        private val binding: ItemOptionBinding,
        private val clickCallback: (Int) -> Unit
    ) : ListHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                onItemClick()
            }
        }

        private fun onItemClick() {
            clickCallback(adapterPosition)
        }

        @SuppressLint("SetTextI18n")
        fun bind(option: GameOption) {
            binding.nameView.setText(option.name)
            if (option is BackpackItem) {
                val count = BackpackManager.getItemCount(option)
                binding.countView.text = "x$count"
                binding.countView.isVisible = true
            } else {
                binding.countView.isVisible = false
            }
            if (option is Commodity) {
                binding.priceView.text = formatPrice(option.price)
                binding.priceView.isVisible = true
            } else {
                binding.priceView.isVisible = false
            }
            val buffer = OptionBufferBuilder.buildBuffer(option)
            if (buffer.isNotEmpty()) {
                binding.buffView.text = buffer
                binding.buffView.isVisible = true
            } else {
                binding.buffView.isVisible = false
            }
        }

        private fun formatPrice(price: Int): String {
            return "${price}.00"
        }
    }

}