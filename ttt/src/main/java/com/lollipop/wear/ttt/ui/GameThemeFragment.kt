package com.lollipop.wear.ttt.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.wear.ttt.databinding.FragmentGameThemeBinding
import com.lollipop.wear.ttt.databinding.ItemThemeStyleBinding
import com.lollipop.wear.ttt.theme.PieceTheme
import com.lollipop.wear.ttt.ui.basic.SubpageFragment

/**
 * 游戏主题
 */
class GameThemeFragment : SubpageFragment() {

    private val binding by lazy {
        FragmentGameThemeBinding.inflate(layoutInflater)
    }

    private val dataList = ArrayList<Any>()

    private val adapter = StyleAdapter(dataList, ::isStyleSelected, ::onStyleClick)

    private var selectedPosition = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataList.add("")
        PieceTheme.resourceArray.forEach {
            dataList.add(it)
        }
        dataList.add("")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.recyclerView.layoutManager = WearableLinearLayoutManager(view.context)
        binding.recyclerView.layoutManager = LinearLayoutManager(
            view.context, RecyclerView.VERTICAL, false
        )
        binding.recyclerView.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        fetchSelectedStyle()
        adapter.notifyDataSetChanged()
    }

    private fun fetchSelectedStyle() {
        val pieceX = PieceTheme.pieceX
        val pieceO = PieceTheme.pieceO
        val pieceEmpty = PieceTheme.pieceEmpty
        val position = dataList.indexOfFirst { any ->
            any is PieceTheme.Style && any.x == pieceX && any.o == pieceO && any.empty == pieceEmpty
        }
        if (position >= 0) {
            selectedPosition = position
        }
    }

    private fun onStyleClick(position: Int, style: PieceTheme.Style) {
        if (selectedPosition == position) {
            return
        }
        if (position < 0 || position >= dataList.size) {
            return
        }
        val old = selectedPosition
        selectedPosition = position
        context?.let { c ->
            PieceTheme.change(style.x, style.o, style.empty, c)
        }
        if (old >= 0) {
            adapter.notifyItemChanged(old)
        }
        adapter.notifyItemChanged(position)
    }

    private fun isStyleSelected(position: Int, style: PieceTheme.Style): Boolean {
        return position == selectedPosition
    }

    private class StyleAdapter(
        private val data: List<Any>,
        private val isSelected: (Int, PieceTheme.Style) -> Boolean,
        private val listener: (Int, PieceTheme.Style) -> Unit
    ) : RecyclerView.Adapter<ItemHolder>() {

        companion object {
            private const val TYPE_SPACE = 0
            private const val TYPE_STYLE = 1
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            if (viewType == TYPE_STYLE) {
                return ItemHolder.Style(
                    ItemThemeStyleBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    ),
                    ::onItemClick
                )
            }
            return ItemHolder.Space(Space(parent.context))
        }

        private fun onItemClick(position: Int) {
            if (position < 0 || position >= data.size) {
                return
            }
            val any = data[position]
            if (any is PieceTheme.Style) {
                listener(position, any)
            }
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            when (holder) {
                is ItemHolder.Space -> {}
                is ItemHolder.Style -> {
                    val style = data[position]
                    if (style is PieceTheme.Style) {
                        holder.bind(style, isSelected(position, style))
                    }
                }
            }
        }

        override fun getItemViewType(position: Int): Int {
            val any = data[position]
            return if (any is PieceTheme.Style) {
                TYPE_STYLE
            } else {
                TYPE_SPACE
            }
        }

    }

    private sealed class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {

        class Space(
            val spaceView: android.widget.Space
        ) : ItemHolder(spaceView) {

            init {
                spaceView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        60F,
                        spaceView.resources.displayMetrics
                    ).toInt()
                )
            }

        }

        class Style(
            private val binding: ItemThemeStyleBinding,
            private val onClick: (Int) -> Unit
        ) : ItemHolder(binding.root) {

            init {
                binding.itemContent.setOnClickListener {
                    onItemClick()
                }
            }

            private fun onItemClick() {
                if (adapterPosition == RecyclerView.NO_POSITION) {
                    return
                }
                onClick(adapterPosition)
            }

            fun bind(style: PieceTheme.Style, selected: Boolean) {
                binding.xPieceIcon.setImageResource(style.x)
                binding.oPieceIcon.setImageResource(style.o)
                binding.emptyPieceIcon.setImageResource(style.empty)
                binding.itemContent.isSelected = selected
            }

        }

    }


}