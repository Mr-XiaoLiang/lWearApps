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
        } else if (task.isStart) {
            onTaskRunning()
        } else {
            onTaskPending()
        }
        itemList.clear()
        task.optionArray.forEach { option ->
            val result = task.findResult(option.id)
            val state = when (result) {
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
            itemList.add(ItemInfo(option, state))
        }
        itemAdapter.notifyDataSetChanged()
        task.add(taskListener)
    }

    override fun onStop() {
        super.onStop()
        currentTask?.remove(taskListener)
    }

    private fun onTaskEnd(list: List<ExecuteResult>) {
        // TODO()
    }

    private fun onTaskPending() {
        // TODO()
    }

    private fun onTaskRunning() {
        // TODO()
    }

    private fun onTaskProgressChanged(count: Int, index: Int, option: FTSOption, progress: Float) {
        // TODO()
    }

    private class ItemAdapter(
        private val data: List<ItemInfo>,
    ) : RecyclerView.Adapter<ItemHolder>() {

        private var layoutInflater: LayoutInflater? = null

        private fun getLayoutInflater(parent: ViewGroup): LayoutInflater {
            return layoutInflater ?: LayoutInflater.from(parent.context).also {
                layoutInflater = it
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            return ItemHolder(
                ItemFtpFlowBinding.inflate(getLayoutInflater(parent), parent, false),
                ::onCloseClick
            )
        }

        private fun onCloseClick(position: Int) {
            // 不处理
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            val info = data[position]
            holder.bind(info.option, info.state)
        }

    }

    private class ItemInfo(
        val option: FTSOption, var state: OptionState = OptionState.HIDE
    )

}