package com.lollipop.file.sender.components

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lollipop.file.sender.R
import com.lollipop.file.sender.databinding.DialogBasicFtpListBinding

abstract class ListBottomSheetDialog : BottomSheetDialogFragment() {

    protected val binding by lazy {
        DialogBasicFtpListBinding.bind(requireView())
    }

    protected val contentPanel by lazy {
        StatePanel.Content(binding.contentContainer)
    }

    protected val panelManager by lazy {
        StatePanelManager()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = layoutInflater.inflate(R.layout.dialog_basic_ftp_list, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initWindow(binding)
        binding.closeButton.setOnClickListener {
            dismiss()
        }
        initView(binding)
        panelManager.changeTo(contentPanel)
    }

    private fun initWindow(binding: DialogBasicFtpListBinding) {
        ViewCompat.setOnApplyWindowInsetsListener(binding.actionBar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.contentContainer) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }
    }

    protected abstract fun initView(binding: DialogBasicFtpListBinding)

    protected fun setExecuteButton(resId: Int, callback: () -> Unit) {
        binding.executeButton.text = getString(resId)
        binding.executeButton.setOnClickListener {
            callback()
        }
    }

    protected fun createHintPanel(
        iconRes: Int,
        infoRes: Int,
    ): StatePanel.Hint {
        return StatePanel.Hint(
            panelRoot = binding.statePanel,
            iconView = binding.stateIconView,
            infoView = binding.stateInfoView,
            iconRes = iconRes,
            infoRes = infoRes
        )
    }

    sealed class StatePanel(
        val panelRoot: View,
    ) {

        fun changeTo(nextState: StatePanel) {
            if (nextState.panelRoot != panelRoot) {
                hide()
            }
            nextState.show()
        }

        fun show() {
            panelRoot.isVisible = true
            onShow()
        }

        fun hide() {
            panelRoot.isVisible = false
            onHide()
        }

        protected open fun onShow() {}

        protected open fun onHide() {}

        class Hint(
            panelRoot: View,
            private val iconView: ImageView,
            private val infoView: TextView,
            private val iconRes: Int,
            private val infoRes: Int
        ) : StatePanel(panelRoot = panelRoot) {
            override fun onShow() {
                iconView.setImageResource(iconRes)
                infoView.setText(infoRes)
            }
        }

        class Content(private val contentView: RecyclerView) : StatePanel(panelRoot = contentView) {
            @SuppressLint("NotifyDataSetChanged")
            override fun onShow() {
                contentView.adapter?.notifyDataSetChanged()
            }
        }

        class Simple(view: View) : StatePanel(panelRoot = view)

    }

    protected class StatePanelManager {

        var currentState: StatePanel? = null
            private set

        fun changeTo(nextState: StatePanel) {
            val current = currentState
            if (current != null) {
                current.changeTo(nextState)
            } else {
                nextState.show()
            }
            currentState = nextState
        }

    }

}