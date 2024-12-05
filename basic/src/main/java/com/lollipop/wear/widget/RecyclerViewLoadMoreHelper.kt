package com.lollipop.wear.widget

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class RecyclerViewLoadMoreHelper(
    private val keepCount: Int = 4,
    private val loadMoreListener: OnLoadMoreListener
) {

    var loadMoreEnabled = true
    var canLoadMore = true

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (!loadMoreEnabled) {
                return
            }
            onListScrolled(recyclerView, dx, dy)
        }
    }

    fun attach(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(scrollListener)
    }

    fun detach(recyclerView: RecyclerView) {
        recyclerView.removeOnScrollListener(scrollListener)
    }

    private fun onListScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (!canLoadMore) {
            return
        }
        val layoutManager = recyclerView.layoutManager ?: return
        val itemCount = recyclerView.adapter?.itemCount ?: return
        val lastVisibleItemPosition = when (layoutManager) {
            is GridLayoutManager -> {
                layoutManager.findLastVisibleItemPosition()
            }

            is LinearLayoutManager -> {
                layoutManager.findLastVisibleItemPosition()
            }

            is StaggeredGridLayoutManager -> {
                val lastVisibleItemPositions = layoutManager.findLastVisibleItemPositions(null)
                lastVisibleItemPositions.maxOrNull() ?: RecyclerView.NO_POSITION
            }

            else -> {
                0
            }
        }
        val offset = itemCount - lastVisibleItemPosition
        if (offset <= keepCount) {
            loadMoreListener.onLoadMore()
            canLoadMore = false
        }
    }

    fun interface OnLoadMoreListener {
        fun onLoadMore()
    }

}