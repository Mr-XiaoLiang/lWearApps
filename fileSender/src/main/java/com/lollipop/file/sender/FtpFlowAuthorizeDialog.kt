package com.lollipop.file.sender

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.lollipop.file.sender.databinding.DialogFtpAuthorizeBinding

class FtpFlowAuthorizeDialog : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "FtpFlowAuthorizeDialog"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = layoutInflater.inflate(R.layout.dialog_ftp_authorize, container, false)
        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        return dialog
    }

    private fun initWindow(binding: DialogFtpAuthorizeBinding) {
//        window?.also { w ->
//            w.setLayout(
//                WindowManager.LayoutParams.MATCH_PARENT,
//                WindowManager.LayoutParams.MATCH_PARENT
//            )
////            val controller = WindowInsetsControllerCompat(w, w.decorView)
//            w.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            w.setType(WindowManager.LayoutParams.TYPE_APPLICATION_PANEL)
//            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//            WindowCompat.setDecorFitsSystemWindows(w, false)
//            w.statusBarColor = Color.TRANSPARENT
//            w.navigationBarColor = Color.TRANSPARENT
//        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

}