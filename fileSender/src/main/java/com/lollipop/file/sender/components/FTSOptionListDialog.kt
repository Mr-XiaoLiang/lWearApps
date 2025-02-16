package com.lollipop.file.sender.components

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.file.sender.R
import com.lollipop.file.sender.databinding.ItemFtpFlowBinding
import com.lollipop.file.sender.ftp.fts.FTSOption
import com.lollipop.file.sender.ftp.fts.FTSTarget

abstract class FTSOptionListDialog : ListBottomSheetDialog() {


    protected class ItemHolder(
        private val binding: ItemFtpFlowBinding,
        private val onCloseClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private var state = OptionState.HIDE

        init {
            binding.stateButton.setOnClickListener {
                onStateButtonClick()
            }
            binding.deleteConfirmationButton.setOnClickListener {
                onDeleteButtonClick()
            }
            binding.cancelConfirmationButton.setOnClickListener {
                closeDeletePanel()
            }
        }

        private fun onDeleteButtonClick() {
            if (state == OptionState.Building) {
                onCloseClick(adapterPosition)
            }
        }

        private fun onStateButtonClick() {
            when (state) {
                OptionState.ERROR -> TODO()
                OptionState.SUCCESS -> TODO()
                OptionState.WAITING -> TODO()
                OptionState.Building -> {
                    expandDeletePanel()
                }

                OptionState.HIDE -> TODO()
            }
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

        fun bind(option: FTSOption, state: OptionState = OptionState.HIDE) {
            resetDeletePanel()
            val hasFrom = hasFrom(option)
            binding.fromView.isVisible = hasFrom
            binding.fromIcon.isVisible = hasFrom
            when (option) {
                is FTSOption.Delete -> {
                    binding.targetView.text = getTargetDisplay(option.target)
                    binding.targetIcon.setImageResource(getTargetIcon(option.target))
                }

                is FTSOption.Rename -> {
                    binding.fromView.text = getTargetDisplay(option.from)
                    binding.fromIcon.setImageResource(getTargetIcon(option.from))
                    binding.targetView.text = getTargetDisplay(option.target)
                    binding.targetIcon.setImageResource(getTargetIcon(option.target))
                }

                is FTSOption.Cache -> {
                    binding.fromView.text = getTargetDisplay(option.from)
                    binding.fromIcon.setImageResource(getTargetIcon(option.from))
                    binding.targetView.text = getTargetDisplay(option.target)
                    binding.targetIcon.setImageResource(getTargetIcon(option.target))
                }

                is FTSOption.Download -> {
                    binding.fromView.text = getTargetDisplay(option.from)
                    binding.fromIcon.setImageResource(getTargetIcon(option.from))
                    binding.targetView.text = getTargetDisplay(option.target)
                    binding.targetIcon.setImageResource(getTargetIcon(option.target))
                }

                is FTSOption.Save -> {
                    binding.fromView.text = getTargetDisplay(option.from)
                    binding.fromIcon.setImageResource(getTargetIcon(option.from))
                    binding.targetView.text = getTargetDisplay(option.target)
                    binding.targetIcon.setImageResource(getTargetIcon(option.target))
                }

                is FTSOption.Upload -> {
                    binding.fromView.text = getTargetDisplay(option.from)
                    binding.fromIcon.setImageResource(getTargetIcon(option.from))
                    binding.targetView.text = getTargetDisplay(option.target)
                    binding.targetIcon.setImageResource(getTargetIcon(option.target))
                }
            }
            binding.optionHintView.setText(getOptionHint(option))
            updateState(state)
        }

        private fun updateState(state: OptionState) {
            this.state = state
            when (state) {
                OptionState.ERROR -> TODO()
                OptionState.SUCCESS -> TODO()
                OptionState.WAITING -> TODO()
                OptionState.Building -> TODO()
                OptionState.HIDE -> TODO()
            }
        }

        private fun hasFrom(option: FTSOption): Boolean {
            return option !is FTSOption.Delete
        }

        private fun getTargetDisplay(target: FTSTarget): String {
            return when (target) {
                is FTSTarget.Cache -> {
                    target.file.path
                }

                is FTSTarget.Local -> {
                    target.uri.path ?: ""
                }

                is FTSTarget.Remote -> {
                    target.path
                }
            }
        }

        private fun getTargetIcon(target: FTSTarget): Int {
            return when (target) {
                is FTSTarget.Cache -> {
                    R.drawable.baseline_cached_24
                }

                is FTSTarget.Local -> {
                    R.drawable.baseline_folder_24
                }

                is FTSTarget.Remote -> {
                    R.drawable.baseline_cloud_24
                }
            }
        }

        private fun getOptionHint(option: FTSOption): Int {
            return when (option) {
                is FTSOption.Delete -> {
                    R.string.flow_item_option_hint_delete
                }

                is FTSOption.Rename -> {
                    R.string.flow_item_option_hint_rename
                }

                is FTSOption.Cache -> {
                    R.string.flow_item_option_hint_cache
                }

                is FTSOption.Download -> {
                    R.string.flow_item_option_hint_download
                }

                is FTSOption.Save -> {
                    R.string.flow_item_option_hint_save
                }

                is FTSOption.Upload -> {
                    R.string.flow_item_option_hint_upload
                }
            }
        }
    }

    protected enum class OptionState {
        ERROR,
        SUCCESS,
        WAITING,
        Building,
        HIDE
    }

}