package com.lollipop.wear.ps.business.page

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class ListContentPageFragment : ContentPageFragment() {

    protected var adapter: RecyclerView.Adapter<*>? = null
        private set

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return RecyclerView(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (view is RecyclerView) {
            adapter = createAdapter()
            view.adapter = adapter
            view.layoutManager = LinearLayoutManager(
                view.context, LinearLayoutManager.VERTICAL, false
            )
        }
    }

    protected abstract fun createAdapter(): RecyclerView.Adapter<*>

    protected object SpaceInfo

    protected abstract class ListHolder(itemView: View): RecyclerView.ViewHolder(itemView)

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