package com.lollipop.file.sender

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.file.sender.components.FTSOptionListDialog
import com.lollipop.wear.utils.updateArguments

class FTSTaskStateDialog : FTSOptionListDialog() {

    companion object {
        const val TAG = "FTSTaskStateDialog"
        private const val PARAMS_KEY_TASK_ID = "task_id"
    }

    private var taskId: String = ""

    fun setTaskId(id: String): FTSTaskStateDialog {
        taskId = id
        updateArguments {
            putString(PARAMS_KEY_TASK_ID, id)
        }
        return this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString(PARAMS_KEY_TASK_ID)?.let {
            taskId = it
        }
    }

    override fun initContentView(recyclerView: RecyclerView) {
        TODO("Not yet implemented")
    }
}