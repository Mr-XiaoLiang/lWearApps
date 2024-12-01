package com.lollipop.wear.ps.business.page

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.wear.ps.engine.state.GameOption

class StatePageFragment : ListContentPageFragment() {
    override fun getListAdapter(): RecyclerView.Adapter<*> {
        TODO("Not yet implemented")
    }

    private class StateAdapter(val list: List<GameOption>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            TODO("Not yet implemented")
        }

        override fun getItemCount(): Int {
            TODO("Not yet implemented")
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            TODO("Not yet implemented")
        }
    }

}