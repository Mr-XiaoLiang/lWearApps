package com.lollipop.wear.ps.activity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
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
    }

    override fun createContent(): View {
        return recyclerView
    }

    protected abstract fun initRecyclerView(recyclerView: RecyclerView)

    protected open fun loadNextPage() {

    }

    protected fun onNextPageLoaded() {
        loadMoreHelper.canLoadMore = true
    }

}