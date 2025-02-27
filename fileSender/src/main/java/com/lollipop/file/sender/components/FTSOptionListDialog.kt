package com.lollipop.file.sender.components

import android.content.res.ColorStateList
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.file.sender.R
import com.lollipop.file.sender.databinding.ItemFtpFlowBinding
import com.lollipop.file.sender.ftp.fts.FTSOption
import com.lollipop.file.sender.ftp.fts.FTSTarget

abstract class FTSOptionListDialog : ListBottomSheetDialog() {


    protected open class ItemHolder(
        private val binding: ItemFtpFlowBinding,
        private val onCloseClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        protected var state: OptionState = OptionState.HIDE
            private set

        protected var currentOptionId: Int = 0
            private set

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
            binding.stateLoadingIcon.max = 100
        }

        private fun onDeleteButtonClick() {
            if (state == OptionState.Building) {
                onCloseClick(adapterPosition)
            }
        }

        private fun onStateButtonClick() {
            when (state) {
                OptionState.ERROR -> {
                    // 出现异常也暂时没有操作
                }

                OptionState.SUCCESS -> {
                    // 成功了就暂时没有操作
                }

                OptionState.WAITING -> {
                    // 等待中也暂时没有操作
                }

                OptionState.Building -> {
                    expandDeletePanel()
                }

                OptionState.HIDE -> {
                    // 隐藏了就没有操作了
                }

                is OptionState.RUNNING -> {
                    // 运行中也暂时没有操作
                }
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
            currentOptionId = option.id
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

        protected fun updateState(state: OptionState) {
            this.state = state
            when (state) {
                OptionState.ERROR -> {
                    binding.stateButton.isVisible = true
                    binding.stateButton.setImageResource(R.drawable.baseline_warning_24)
                    ImageViewCompat.setImageTintList(
                        binding.stateButton,
                        ColorStateList.valueOf(
                            binding.root.context.getColor(
                                R.color.error_content_background
                            )
                        )
                    )
                    binding.stateLoadingIcon.isVisible = false
                }

                OptionState.SUCCESS -> {
                    binding.stateButton.isVisible = true
                    binding.stateButton.setImageResource(R.drawable.baseline_done_24)
                    ImageViewCompat.setImageTintList(
                        binding.stateButton,
                        ColorStateList.valueOf(
                            binding.root.context.getColor(
                                R.color.success_content_background
                            )
                        )
                    )
                    binding.stateLoadingIcon.isVisible = false
                }

                OptionState.WAITING -> {
                    binding.stateButton.isVisible = false
                    binding.stateLoadingIcon.isVisible = true
                    binding.stateLoadingIcon.show()
                    binding.stateLoadingIcon.isIndeterminate = true
                }

                OptionState.Building -> {
                    binding.stateButton.isVisible = true
                    binding.stateButton.setImageResource(R.drawable.baseline_close_24)
                    ImageViewCompat.setImageTintList(
                        binding.stateButton,
                        ColorStateList.valueOf(
                            binding.root.context.getColor(
                                R.color.error_content_background
                            )
                        )
                    )
                    binding.stateLoadingIcon.isVisible = false
                }

                OptionState.HIDE -> {
                    binding.stateButton.isVisible = false
                    binding.stateLoadingIcon.isVisible = false
                }

                is OptionState.RUNNING -> {
                    binding.stateButton.isVisible = false
                    binding.stateLoadingIcon.isVisible = true
                    binding.stateLoadingIcon.show()
                    binding.stateLoadingIcon.isIndeterminate = false
                    binding.stateLoadingIcon.progress = (state.progress * 100).toInt()
                }
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

    protected sealed class OptionState {
        data object ERROR : OptionState()
        data object SUCCESS : OptionState()
        data object WAITING : OptionState()
        data object Building : OptionState()
        data object HIDE : OptionState()
        class RUNNING(val progress: Float) : OptionState()
    }

}