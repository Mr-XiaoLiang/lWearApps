package com.lollipop.wear.ttt.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.wear.ttt.R
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
        dataList.add(SpaceInfo)
        dataList.add(TitleInfo)
        PieceTheme.resourceArray.forEach {
            dataList.add(it)
        }
        dataList.add(SpaceInfo)
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

    private object TitleInfo

    private object SpaceInfo

    private class StyleAdapter(
        private val data: List<Any>,
        private val isSelected: (Int, PieceTheme.Style) -> Boolean,
        private val listener: (Int, PieceTheme.Style) -> Unit
    ) : RecyclerView.Adapter<ItemHolder>() {

        companion object {
            private const val TYPE_SPACE = 0
            private const val TYPE_TITLE = 1
            private const val TYPE_STYLE = 2
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
            if (viewType == TYPE_TITLE) {
                return ItemHolder.Title.create(parent.context)
            }
            return ItemHolder.Space.create(parent.context)
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

                is ItemHolder.Title -> {}
            }
        }

        override fun getItemViewType(position: Int): Int {
            val any = data[position]
            return when (any) {
                is PieceTheme.Style -> {
                    TYPE_STYLE
                }

                is TitleInfo -> {
                    TYPE_TITLE
                }

                else -> {
                    TYPE_SPACE
                }
            }
        }

    }

    private sealed class ItemHolder(view: View) : RecyclerView.ViewHolder(view) {

        class Space private constructor(
            val spaceView: android.widget.Space
        ) : ItemHolder(spaceView) {

            companion object {
                fun create(context: Context): Space {
                    return Space(Space(context))
                }
            }

            init {
                spaceView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        40F,
                        spaceView.resources.displayMetrics
                    ).toInt()
                )
            }

        }

        class Title private constructor(
            val titleView: TextView
        ) : ItemHolder(titleView) {

            companion object {
                fun create(context: Context): Title {
                    return Title(TextView(context))
                }
            }

            init {
                titleView.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                titleView.textSize = 16F
                titleView.gravity = Gravity.CENTER
                titleView.setTextColor(Color.WHITE)
                titleView.setText(R.string.title_theme)
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
                binding.itemContent.isSelected = selected
            }

        }

    }


}