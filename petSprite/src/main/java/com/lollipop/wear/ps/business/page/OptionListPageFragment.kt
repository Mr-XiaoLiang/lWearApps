package com.lollipop.wear.ps.business.page

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.wear.ps.databinding.ItemOptionBinding
import com.lollipop.wear.ps.engine.state.GameOption

class OptionListPageFragment : ListContentPageFragment() {

    override fun createAdapter(): RecyclerView.Adapter<*> {
        TODO("Not yet implemented")
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
            notifyDataSetChanged()
        }

        private fun getLayoutInflater(parent: ViewGroup): LayoutInflater {
            return layoutInflaterImpl ?: LayoutInflater.from(parent.context).also {
                layoutInflaterImpl = it
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListHolder {
            if (viewType == TYPE_OPTION) {
                return OptionHolder(
                    ItemOptionBinding.inflate(getLayoutInflater(parent)),
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

        fun bind(option: GameOption) {
            binding.nameView.setText(option.name)
        }
    }

    interface Callback {

        fun getOptionList(pageId: String): List<GameOption>

        fun onOptionClick(option: GameOption)

    }


}