package com.lollipop.file.sender

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.file.sender.components.FTSOptionListDialog
import com.lollipop.file.sender.databinding.ItemFtpFlowBinding
import com.lollipop.file.sender.ftp.FileTransferStation
import com.lollipop.file.sender.ftp.FtpManager
import com.lollipop.file.sender.ftp.fts.FTSContextProviderWrapper
import com.lollipop.file.sender.ftp.fts.FTSOption
import com.lollipop.file.sender.ftp.fts.FTSTaskManager

class FtpFlowAuthorizeDialog : FTSOptionListDialog() {

    private val emptyStatePanel by lazy {
        createHintPanel(R.drawable.baseline_mitre_24px, R.string.hint_empty_flow)
    }

    private val adapter by lazy {
        ItemAdapter(FileTransferStation.allFlows, ::onItemCloseClick)
    }

    override fun initContentView(recyclerView: RecyclerView) {
        setExecuteButton(R.string.execute) { view ->
            val client = FtpManager.currentClient()
            if (client != null) {
                val ftsTask = FTSTaskManager.execute(
                    client,
                    FileTransferStation.allFlows,
                    FTSContextProviderWrapper(view.context)
                )
                // TODO 打开任务列表
                dismiss()
            } else {
                Toast.makeText(
                    view.context,
                    getString(R.string.ftp_client_not_found),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        bindLinearLayoutManager(recyclerView, RecyclerView.VERTICAL)
        recyclerView.adapter = adapter
        if (FileTransferStation.allFlows.isEmpty()) {
            panelManager.changeTo(emptyStatePanel)
        } else {
            panelManager.changeTo(contentPanel)
        }
    }

    private fun onItemCloseClick(option: FTSOption): Boolean {
        FileTransferStation.removeFlowOption(option)
        if (FileTransferStation.allFlows.isEmpty()) {
            panelManager.changeTo(emptyStatePanel)
        }
        return true
    }

    private class ItemAdapter(
        private val data: List<FTSOption>,
        private val onCloseClick: (FTSOption) -> Boolean
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
            if (position < 0 || position >= itemCount) {
                return
            }
            if (onCloseClick(data[position])) {
                notifyItemRemoved(position)
            }
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            holder.bind(data[position])
        }

    }


}