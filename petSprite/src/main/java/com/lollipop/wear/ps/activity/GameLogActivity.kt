package com.lollipop.wear.ps.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lollipop.wear.basic.doAsync
import com.lollipop.wear.basic.onUI
import com.lollipop.wear.ps.databinding.ActivityGameLogBinding
import com.lollipop.wear.ps.databinding.ItemGameLogBinding
import com.lollipop.wear.ps.engine.log.GameLog
import com.lollipop.wear.ps.engine.log.GameLogStore
import com.lollipop.wear.widget.RecyclerViewLoadMoreHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GameLogActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    private val binding by lazy {
        ActivityGameLogBinding.inflate(layoutInflater)
    }

    private val loadMoreHelper = RecyclerViewLoadMoreHelper {
        loadNextPage()
    }

    private val logStore by lazy {
        GameLogStore(this)
    }

    private val logList = ArrayList<Any>()

    private var pageIndex = 0

    private val adapter = LogAdapter(logList)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        loadMoreHelper.attach(binding.recyclerView)
        loadMoreHelper.loadMoreEnabled = true
        binding.refreshLayout.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN)
        binding.refreshLayout.setProgressBackgroundColorSchemeColor(Color.DKGRAY)
        binding.refreshLayout.setOnRefreshListener(this)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.post {
            adapter.setSpaceHeight(binding.recyclerView.height / 2)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        onRefresh()
    }

    private fun onLoadEnd() {
        loadMoreHelper.canLoadMore = true
    }

    private fun loadNextPage() {
        pageIndex++
        loadItems()
    }

    override fun onRefresh() {
        pageIndex = 0
        loadItems()
    }

    private fun onRefreshEnd() {
        binding.refreshLayout.isRefreshing = false
    }

    private fun loadItems() {
        loadMoreHelper.canLoadMore = false
        val index = pageIndex
        if (index == 0) {
            logList.clear()
            logList.add(SpaceInfo)
        }
        val pageSize = 20
        doAsync {
            val list = logStore.queryLog(index, pageSize = pageSize)
            onUI {
                if (list.isNotEmpty()) {
                    if (logList.size > 1) {
                        val lastIndex = logList.size - 1
                        val last = logList[lastIndex]
                        if (last is SpaceInfo) {
                            logList.removeAt(lastIndex)
                            adapter.notifyItemRemoved(lastIndex)
                        }
                    }
                    val startIndex = logList.size
                    logList.addAll(list)
                    if (list.size < pageSize) {
                        logList.add(SpaceInfo)
                    }
                    adapter.notifyItemRangeInserted(startIndex, list.size)
                }
                onRefreshEnd()
                onLoadEnd()
            }
        }
    }

    private class LogAdapter(private val list: List<Any>) : RecyclerView.Adapter<Holder>() {

        companion object {
            private const val TYPE_SPACE = 0
            private const val TYPE_LOG = 1
        }

        private var spaceHeight = 0

        private var layoutInflaterImpl: LayoutInflater? = null

        fun setSpaceHeight(height: Int) {
            spaceHeight = height
        }

        private fun getLayoutInflater(parent: ViewGroup): LayoutInflater {
            return layoutInflaterImpl ?: LayoutInflater.from(parent.context).also {
                layoutInflaterImpl = it
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            if (viewType == TYPE_LOG) {
                return Holder.LogHolder(
                    ItemGameLogBinding.inflate(
                        getLayoutInflater(parent),
                        parent,
                        false
                    )
                )
            }
            return Holder.SpaceHolder.create(parent, spaceHeight)
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun getItemViewType(position: Int): Int {
            val any = list[position]
            if (any is GameLog) {
                return TYPE_LOG
            }
            return TYPE_SPACE
        }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            if (holder is Holder.LogHolder) {
                val any = list[position]
                if (any is GameLog) {
                    holder.bind(any)
                }
            } else if (holder is Holder.SpaceHolder) {
                holder.setHeight(spaceHeight)
            }
        }

    }

    private object SpaceInfo

    private sealed class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        class SpaceHolder(private val spaceView: Space) : Holder(spaceView) {

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
                spaceView.updateLayoutParams<ViewGroup.LayoutParams> {
                    this.height = height
                }
            }

        }

        class LogHolder(
            private val binding: ItemGameLogBinding
        ) : Holder(binding.root) {

            private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

            @SuppressLint("SetTextI18n")
            fun bind(log: GameLog) {
                binding.messageView.text = "${log.who} ${log.what}"
                binding.timeView.text = dateFormat.format(Date(log.time))
            }

        }

    }

}