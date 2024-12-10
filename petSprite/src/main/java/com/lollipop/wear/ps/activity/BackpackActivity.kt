package com.lollipop.wear.ps.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.wear.ps.business.options.BackpackMenu
import com.lollipop.wear.ps.databinding.ItemSimpleOptionBinding

class BackpackActivity : DashboardBasicListActivity() {

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, BackpackActivity::class.java))
        }
    }

    private val optionList = ArrayList<Any>()

    private val adapter by lazy {
        BackpackOptionAdapter(optionList, ::onItemClick)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        optionList.add(SpaceInfo)
        optionList.addAll(BackpackMenu.entries)
        optionList.add(SpaceInfo)
    }

    override fun initRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        bindViewSize(recyclerView, adapter)
    }

    private fun onItemClick(item: BackpackMenu) {
        GameOptionListActivity.start(this, item)
    }

    private class BackpackOptionAdapter(
        dataList: List<Any>,
        private val onItemClickCallback: (BackpackMenu) -> Unit
    ) : ListAdapter(dataList) {

        companion object {
            private const val TYPE_OPTION = 2
        }

        override fun onCreateItemHolder(parent: ViewGroup, viewType: Int): ListHolder? {
            if (TYPE_OPTION == viewType) {
                return BackpackOptionHolder(
                    ItemSimpleOptionBinding.inflate(
                        getLayoutInflater(
                            parent
                        ), parent, false
                    ),
                    ::onItemClick
                )
            }
            return null
        }

        private fun onItemClick(position: Int) {
            if (position < 0 || position >= dataList.size) {
                return
            }
            val item = dataList[position]
            if (item is BackpackMenu) {
                onItemClickCallback(item)
            }
        }

        override fun getItemType(data: Any, position: Int): Int? {
            if (data is BackpackMenu) {
                return TYPE_OPTION
            }
            return null
        }

        override fun onBindItemHolder(holder: ListHolder, position: Int) {
            if (holder is BackpackOptionHolder) {
                val any = dataList[position]
                if (any is BackpackMenu) {
                    holder.bind(any)
                }
            }
        }

    }

    private class BackpackOptionHolder(
        binding: ItemSimpleOptionBinding,
        private val onClickCallback: (Int) -> Unit
    ) : SimpleHolder(binding) {

        override fun onItemClick() {
            onClickCallback(adapterPosition)
        }

        fun bind(item: BackpackMenu) {
            binding.nameView.setText(item.label)
        }

    }

}