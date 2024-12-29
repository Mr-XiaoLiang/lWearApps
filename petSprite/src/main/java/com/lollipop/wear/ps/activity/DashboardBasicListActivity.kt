package com.lollipop.wear.ps.activity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.wear.ps.utils.RoundPageListAdapter
import com.lollipop.wear.widget.RecyclerViewLoadMoreHelper

abstract class DashboardBasicListActivity : DashboardBasicActivity() {

    private val recyclerView by lazy {
        RecyclerView(this)
    }

    protected val loadMoreHelper = RecyclerViewLoadMoreHelper {
        loadNextPage()
    }

    protected open val loadMoreEnable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initRecyclerView(recyclerView)
        loadMoreHelper.loadMoreEnabled = loadMoreEnable
        loadMoreHelper.attach(recyclerView)
        dashboardDelegate.setTextVisible(false)
    }

    override fun createContent(): View {
        return recyclerView
    }

    protected abstract fun initRecyclerView(recyclerView: RecyclerView)

    protected open fun loadNextPage() {

    }

    protected fun bindViewSize(view: View, adapter: ListAdapter) {
        view.post {
            adapter.setSpaceHeight(view.height / 2)
            adapter.notifyDataSetChanged()
        }
    }

    protected fun onNextPageLoaded() {
        loadMoreHelper.canLoadMore = true
    }

    protected abstract class ListAdapter(dataList: List<Any>) : RoundPageListAdapter(dataList)


}