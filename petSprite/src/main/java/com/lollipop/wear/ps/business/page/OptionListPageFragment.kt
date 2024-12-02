package com.lollipop.wear.ps.business.page

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.wear.ps.business.options.OptionBufferBuilder
import com.lollipop.wear.ps.databinding.ItemOptionBinding
import com.lollipop.wear.ps.engine.state.BackpackItem
import com.lollipop.wear.ps.engine.state.BackpackManager
import com.lollipop.wear.ps.engine.state.GameOption
import com.lollipop.wear.ps.engine.state.type.Commodity

open class OptionListPageFragment : ListContentPageFragment() {

    private val dataList = ArrayList<Any>()

    private val adapter = OptionAdapter(dataList, ::onOptionClick)

    private var callback: Callback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = findCallback(context)
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    override fun getListAdapter(): RecyclerView.Adapter<*> {
        return adapter
    }

    private fun onOptionClick(option: GameOption) {
        callback?.onOptionClick(option)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.post {
            adapter.setParentHeight(view.height / 2)
            adapter.notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        view?.let {
            adapter.setParentHeight(it.height / 2)
        }
        dataList.clear()
        dataList.add(SpaceInfo)
        callback?.let {
            dataList.addAll(it.getOptionList(getPageId(this)))
        }
        dataList.add(SpaceInfo)
        adapter.notifyDataSetChanged()
    }

    protected class OptionAdapter(
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

    interface Callback {

        fun getOptionList(pageId: String): List<GameOption>

        fun onOptionClick(option: GameOption)

    }


}