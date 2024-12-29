package com.lollipop.wear.ps.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.wear.ps.databinding.ItemSimpleOptionBinding

abstract class RoundPageListAdapter(
    protected val dataList: List<Any>
) : RecyclerView.Adapter<RoundPageListAdapter.ListHolder>() {

    companion object {
        const val TYPE_SPACE = 999
    }

    private var spaceHeight = 0

    private var layoutInflaterImpl: LayoutInflater? = null

    fun setSpaceHeight(height: Int) {
        spaceHeight = height
    }

    protected fun getLayoutInflater(parent: ViewGroup): LayoutInflater {
        return layoutInflaterImpl ?: LayoutInflater.from(parent.context).also {
            layoutInflaterImpl = it
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListHolder {
        val itemHolder = onCreateItemHolder(parent, viewType)
        if (itemHolder != null) {
            return itemHolder
        }
        return SpaceHolder.create(parent, spaceHeight)
    }

    protected abstract fun onCreateItemHolder(parent: ViewGroup, viewType: Int): ListHolder?

    override fun getItemViewType(position: Int): Int {
        val any = dataList[position]
        if (any is SpaceInfo) {
            return TYPE_SPACE
        }
        return getItemType(any, position) ?: TYPE_SPACE
    }

    protected abstract fun getItemType(data: Any, position: Int): Int?

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: ListHolder, position: Int) {
        if (holder is SpaceHolder) {
            holder.setHeight(spaceHeight)
            return
        }
        onBindItemHolder(holder, position)
    }

    protected abstract fun onBindItemHolder(holder: ListHolder, position: Int)

    object SpaceInfo

    abstract class ListHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class SpaceHolder(
        private val spaceView: Space
    ) : ListHolder(spaceView) {

        companion object {
            fun create(parent: ViewGroup, height: Int): SpaceHolder {
                val spaceView = Space(parent.context)
                spaceView.layoutParams = RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    height
                )
                return SpaceHolder(spaceView)
            }
        }

        fun setHeight(height: Int) {
            spaceView.layoutParams.height = height
        }

    }

    abstract class SimpleHolder(
        val binding: ItemSimpleOptionBinding
    ) : ListHolder(binding.root) {

        init {
            binding.nameView.setOnClickListener { onItemClick() }
        }

        abstract fun onItemClick()

    }

}

