package com.lollipop.wear.ps.activity

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Space
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
        dashboardDelegate.setTextVisible(false)
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

    protected object SpaceInfo

    protected abstract class ListHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    protected class SpaceHolder(
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

}