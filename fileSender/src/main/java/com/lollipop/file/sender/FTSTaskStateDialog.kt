package com.lollipop.file.sender

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.file.sender.components.FTSOptionListDialog
import com.lollipop.file.sender.databinding.ItemFtpFlowBinding
import com.lollipop.file.sender.ftp.fts.ExecuteResult
import com.lollipop.file.sender.ftp.fts.FTSExecuteCallback
import com.lollipop.file.sender.ftp.fts.FTSExecuteCallbackDispatcher
import com.lollipop.file.sender.ftp.fts.FTSExecuteCallbackHandlerWrapper
import com.lollipop.file.sender.ftp.fts.FTSOption
import com.lollipop.file.sender.ftp.fts.FTSTask
import com.lollipop.file.sender.ftp.fts.FTSTaskManager
import com.lollipop.wear.utils.updateArguments

class FTSTaskStateDialog : FTSOptionListDialog() {

    companion object {
        const val TAG = "FTSTaskStateDialog"
        private const val PARAMS_KEY_TASK_ID = "task_id"
        private const val PARAMS_KEY_CLIENT_ID = "client_id"
    }

    private var clientId: String = ""
    private var taskId: String = ""
    private var currentTask: FTSTask? = null
    private val itemList = ArrayList<ItemInfo>()
    private val itemAdapter = ItemAdapter(itemList)
    private var currentOptionIndex = -1

    private val taskListener = FTSExecuteCallbackHandlerWrapper(
        Handler(Looper.getMainLooper()),
        100,
        object : FTSExecuteCallback {
            override fun onStart() {
                onTaskRunning()
            }

            override fun onProgress(count: Int, index: Int, option: FTSOption, progress: Float) {
                onTaskProgressChanged(count, index, option, progress)
            }

            override fun onEnd(list: List<ExecuteResult>) {
                onTaskEnd(list)
            }

        }
    )

    fun setTaskId(clientId: String, taskId: String): FTSTaskStateDialog {
        this.taskId = taskId
        this.clientId = clientId
        updateArguments {
            putString(PARAMS_KEY_CLIENT_ID, clientId)
            putString(PARAMS_KEY_TASK_ID, taskId)

        }
        return this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { args ->
            args.getString(PARAMS_KEY_TASK_ID)?.let {
                taskId = it
            }
            args.getString(PARAMS_KEY_CLIENT_ID)?.let {
                clientId = it
            }
        }
    }

    override fun initContentView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(
            recyclerView.context, RecyclerView.VERTICAL, false
        )
        recyclerView.adapter = itemAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onStart() {
        super.onStart()
        val task = FTSTaskManager.findTask(clientId, taskId)
        currentTask = task
        if (task == null) {
            dismiss()
            return
        }
        if (task.isEnd) {
            onTaskEnd(task.result)
            currentOptionIndex = -1
        } else {
            currentOptionIndex = task.index
            itemList.clear()
            task.optionArray.forEach { option ->
                val result = task.findResult(option.id)
                itemList.add(ItemInfo(option, getStateByResult(result)))
            }
            onTaskRunning()
            task.add(taskListener)
        }
    }

    override fun onStop() {
        super.onStop()
        currentTask?.remove(taskListener)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onTaskEnd(list: List<ExecuteResult>) {
        itemList.clear()
        list.forEach { result ->
            when (result) {
                is ExecuteResult.Success -> {
                    itemList.add(ItemInfo(result.option, OptionState.SUCCESS))
                }

                is ExecuteResult.Failed -> {
                    itemList.add(ItemInfo(result.option, OptionState.ERROR))
                }
            }
        }
        itemAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onTaskRunning() {
        itemAdapter.notifyDataSetChanged()
    }

    private fun onTaskProgressChanged(count: Int, index: Int, option: FTSOption, progress: Float) {
        if (currentOptionIndex != index) {
            itemList.forEachIndexed { i, item ->
                if (item.option.id != option.id) {
                    if (item.state is OptionState.RUNNING) {
                        currentTask?.findResult(item.option.id)?.let { result ->
                            item.state = getStateByResult(result)
                            itemAdapter.notifyItemChanged(i)
                        }
                    }
                }
            }
        }
    }

    private fun getStateByResult(result: ExecuteResult?): OptionState {
        return when (result) {
            null -> {
                OptionState.WAITING
            }

            is ExecuteResult.Success -> {
                OptionState.SUCCESS
            }

            is ExecuteResult.Failed -> {
                OptionState.ERROR
            }
        }
    }

    private class ItemAdapter(
        private val data: List<ItemInfo>,
    ) : RecyclerView.Adapter<StateItemHolder>() {

        private var layoutInflater: LayoutInflater? = null

        private val stateDispatcher = FTSExecuteCallbackDispatcher()

        private fun getLayoutInflater(parent: ViewGroup): LayoutInflater {
            return layoutInflater ?: LayoutInflater.from(parent.context).also {
                layoutInflater = it
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StateItemHolder {
            return StateItemHolder(
                ItemFtpFlowBinding.inflate(getLayoutInflater(parent), parent, false),
            )
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: StateItemHolder, position: Int) {
            val info = data[position]
            holder.bind(info)
            stateDispatcher.add(holder)
        }

        override fun onViewRecycled(holder: StateItemHolder) {
            super.onViewRecycled(holder)
            stateDispatcher.remove(holder)
        }

    }

    private class StateItemHolder(
        binding: ItemFtpFlowBinding
    ) : ItemHolder(binding, {}), FTSExecuteCallback {

        private var itemInfo: ItemInfo? = null

        fun bind(info: ItemInfo) {
            this.itemInfo = info
            bind(info.option, info.state)
        }

        // 不需要管
        override fun onStart() {
        }

        override fun onProgress(count: Int, index: Int, option: FTSOption, progress: Float) {
            if (option.id == currentOptionId) {
                val newState = OptionState.RUNNING(progress)
                itemInfo?.state = newState
                updateState(newState)
            }
        }

        // 不需要管
        override fun onEnd(list: List<ExecuteResult>) {
        }

    }

    private class ItemInfo(
        val option: FTSOption, var state: OptionState = OptionState.HIDE
    )

}