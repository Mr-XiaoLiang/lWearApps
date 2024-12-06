package com.lollipop.wear.ps.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.wear.ps.business.options.OptionBufferBuilder
import com.lollipop.wear.ps.business.options.OptionMenu
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

        fun start(context: Context, option: OptionMenu) {
            context.startActivity(Intent(context, GameOptionListActivity::class.java).apply {
                putExtra(EXTRA_OPTION, option.key)
            })
        }
    }

    private val optionList = ArrayList<GameOption>()

    private val adapter by lazy {
        OptionAdapter(optionList, ::onOptionClick)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val menu = OptionMenu.find(intent.getStringExtra(EXTRA_OPTION) ?: "")
        if (menu == null) {
            finish()
            return
        }
        optionList.addAll(menu.getOptionList())
        adapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun initRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recyclerView.post {
            adapter.setParentHeight(recyclerView.height / 2)
            adapter.notifyDataSetChanged()
        }
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
        private val dataList: List<Any>,
        private val onOptionClick: (GameOption) -> Unit
    ) : RecyclerView.Adapter<ListHolder>() {

        companion object {
            const val TYPE_SPACE = 1
            const val TYPE_OPTION = 2
        }

        private var parentHeight = 0

        private var layoutInflaterImpl: LayoutInflater? = null

        fun setParentHeight(height: Int) {
            parentHeight = height
        }

        private fun getLayoutInflater(parent: ViewGroup): LayoutInflater {
            return layoutInflaterImpl ?: LayoutInflater.from(parent.context).also {
                layoutInflaterImpl = it
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListHolder {
            if (viewType == TYPE_OPTION) {
                return OptionHolder(
                    ItemOptionBinding.inflate(getLayoutInflater(parent), parent, false),
                    this::onItemClick
                )
            }
            return SpaceHolder.create(parent, parentHeight)
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

        override fun getItemViewType(position: Int): Int {
            val d = dataList[position]
            if (d is SpaceInfo) {
                return TYPE_SPACE
            }
            if (d is GameOption) {
                return TYPE_OPTION
            }
            return super.getItemViewType(position)
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        override fun onBindViewHolder(holder: ListHolder, position: Int) {
            if (holder is SpaceHolder) {
                holder.setHeight(parentHeight)
                return
            }
            val d = dataList[position]
            if (d is GameOption) {
                if (holder is OptionHolder) {
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