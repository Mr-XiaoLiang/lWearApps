package com.lollipop.file.sender

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.file.sender.components.ListBottomSheetDialog
import com.lollipop.file.sender.databinding.ItemFtpFlowBinding
import com.lollipop.file.sender.ftp.FileTransferStation

class FtpFlowAuthorizeDialog : ListBottomSheetDialog() {

    private val emptyStatePanel by lazy {
        createHintPanel(R.drawable.baseline_mitre_24px, R.string.hint_empty_flow)
    }

    private val adapter by lazy {
        ItemAdapter(FileTransferStation.allFlows, ::onItemCloseClick)
    }

    override fun initContentView(recyclerView: RecyclerView) {
        setExecuteButton(R.string.execute) {
            // TODO("Not yet implemented")
        }
        bindLinearLayoutManager(recyclerView, RecyclerView.VERTICAL)
        recyclerView.adapter = adapter
        if (FileTransferStation.allFlows.isEmpty()) {
            panelManager.changeTo(emptyStatePanel)
        } else {
            panelManager.changeTo(contentPanel)
        }
    }

    private fun onItemCloseClick(option: FileTransferStation.Options): Boolean {
        FileTransferStation.removeFlowOption(option)
        if (FileTransferStation.allFlows.isEmpty()) {
            panelManager.changeTo(emptyStatePanel)
        }
        return true
    }

    private class ItemAdapter(
        private val data: List<FileTransferStation.Options>,
        private val onCloseClick: (FileTransferStation.Options) -> Boolean
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

    private class ItemHolder(
        private val binding: ItemFtpFlowBinding,
        private val onCloseClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.closeButton.setOnClickListener {
                expandDeletePanel()
            }
            binding.deleteConfirmationButton.setOnClickListener {
                onDeleteButtonClick()
            }
            binding.cancelConfirmationButton.setOnClickListener {
                closeDeletePanel()
            }
        }

        private fun onDeleteButtonClick() {
            onCloseClick(adapterPosition)
        }

        private fun expandDeletePanel() {
            binding.deleteConfirmationButton.isVisible = true
            binding.cancelConfirmationButton.isVisible = true
            binding.contentView.animate().apply {
                cancel()
                translationX((binding.root.width - binding.contentView.width).toFloat())
            }
        }

        private fun closeDeletePanel() {
            binding.deleteConfirmationButton.isVisible = false
            binding.cancelConfirmationButton.isVisible = false
            binding.contentView.animate().apply {
                cancel()
                translationX(0f)
            }
        }

        private fun resetDeletePanel() {
            binding.deleteConfirmationButton.isVisible = false
            binding.cancelConfirmationButton.isVisible = false
            binding.contentView.animate().cancel()
            binding.contentView.translationX = 0f
        }

        fun bind(option: FileTransferStation.Options) {
            resetDeletePanel()
            when (option) {
                is FileTransferStation.Options.Delete -> {
                    binding.fromView.isVisible = false
                    binding.fromIcon.isVisible = false
                    binding.targetView.text = getTargetDisplay(option.target)
                    binding.targetIcon.setImageResource(getTargetIcon(option.target))
                }

                is FileTransferStation.Options.Rename -> {
                    binding.fromView.isVisible = true
                    binding.fromIcon.isVisible = true
                    binding.fromView.text = getTargetDisplay(option.from)
                    binding.fromIcon.setImageResource(getTargetIcon(option.from))
                    binding.targetView.text = getTargetDisplay(option.target)
                    binding.targetIcon.setImageResource(getTargetIcon(option.target))
                }

                is FileTransferStation.Options.Transmission -> {
                    binding.fromView.isVisible = true
                    binding.fromIcon.isVisible = true
                    binding.fromView.text = getTargetDisplay(option.from)
                    binding.fromIcon.setImageResource(getTargetIcon(option.from))
                    binding.targetView.text = getTargetDisplay(option.target)
                    binding.targetIcon.setImageResource(getTargetIcon(option.target))
                }
            }
            binding.optionHintView.setText(getOptionHint(option))
        }

        private fun getTargetDisplay(target: FileTransferStation.FtsTarget): String {
            return when (target) {
                is FileTransferStation.FtsTarget.Cache -> {
                    target.name
                }

                is FileTransferStation.FtsTarget.Local -> {
                    target.uri.path ?: ""
                }

                is FileTransferStation.FtsTarget.Remote -> {
                    target.path
                }
            }
        }

        private fun getTargetIcon(target: FileTransferStation.FtsTarget): Int {
            return when (target) {
                is FileTransferStation.FtsTarget.Cache -> {
                    R.drawable.baseline_cached_24
                }

                is FileTransferStation.FtsTarget.Local -> {
                    R.drawable.baseline_folder_24
                }

                is FileTransferStation.FtsTarget.Remote -> {
                    R.drawable.baseline_cloud_24
                }
            }
        }

        private fun getOptionHint(option: FileTransferStation.Options): Int {
            return when (option) {
                is FileTransferStation.Options.Delete -> {
                    R.string.flow_item_option_hint_delete
                }

                is FileTransferStation.Options.Rename -> {
                    R.string.flow_item_option_hint_rename
                }

                is FileTransferStation.Options.Transmission -> {
                    R.string.flow_item_option_hint_transmission
                }
            }
        }
    }

}