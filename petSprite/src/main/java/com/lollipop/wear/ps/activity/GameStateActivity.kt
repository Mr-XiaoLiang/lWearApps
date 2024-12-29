package com.lollipop.wear.ps.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.wear.ps.databinding.ItemSimpleOptionBinding
import com.lollipop.wear.ps.engine.state.GameState
import com.lollipop.wear.ps.engine.state.GameStateManager
import com.lollipop.wear.ps.utils.RoundPageListAdapter

class GameStateActivity : DashboardBasicListActivity() {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, GameStateActivity::class.java))
        }
    }

    private val stateList = ArrayList<Any>()

    private val adapter = StateAdapter(stateList)

    override fun initRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        bindViewSize(recyclerView, adapter)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stateList.add(RoundPageListAdapter.SpaceInfo)
        stateList.addAll(GameStateManager.stateList)
        stateList.add(RoundPageListAdapter.SpaceInfo)
        adapter.notifyDataSetChanged()
    }

    private class StateAdapter(
        dataList: List<Any>
    ) : ListAdapter(dataList) {

        companion object {
            private const val TYPE_STATE = 1
        }

        override fun onCreateItemHolder(parent: ViewGroup, viewType: Int): ListHolder? {
            if (viewType == TYPE_STATE) {
                return StateItemHolder(
                    ItemSimpleOptionBinding.inflate(
                        getLayoutInflater(parent),
                        parent,
                        false
                    )
                )
            }
            return null
        }

        override fun getItemType(data: Any, position: Int): Int? {
            if (data is GameState) {
                return TYPE_STATE
            }
            return null
        }

        override fun onBindItemHolder(holder: ListHolder, position: Int) {
            if (holder is StateItemHolder) {
                val any = dataList[position]
                if (any is GameState) {
                    holder.bind(any)
                }
            }
        }

    }

    private class StateItemHolder(
        binding: ItemSimpleOptionBinding
    ) : RoundPageListAdapter.SimpleHolder(binding) {
        override fun onItemClick() {
            // 不考虑处理
        }

        fun bind(gameState: GameState) {
            binding.nameView.text = itemView.context.getString(
                gameState.name,
                gameState.displayValue()
            )
        }

    }

}