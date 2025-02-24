package com.lollipop.wear.basic

import android.content.Context
import android.content.DialogInterface
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder


object DialogHelper {

    fun list(
        context: Context,
        titleRes: Int,
        itemResList: Array<Int>,
        onSelect: (DialogInterface, Int) -> Unit
    ): AlertDialog {
        val itemValueList = itemResList.map { context.getString(it) }.toTypedArray()
        return MaterialAlertDialogBuilder(context)
            .setTitle(titleRes)
            .setItems(itemValueList) { dialog, which ->
                onSelect(dialog, itemResList[which])
            }
            .show()
    }

    fun alert(
        context: Context,
        messageRes: Int,
        positiveRes: Int = 0,
        titleRes: Int = 0,
        negativeRes: Int = 0,
        onPositive: ((DialogInterface) -> Unit) = { it.dismiss() },
        onNegative: ((DialogInterface) -> Unit) = { it.dismiss() }
    ): AlertDialog {
        return MaterialAlertDialogBuilder(context)
            .setMessage(messageRes)
            .also {
                if (titleRes != 0) {
                    it.setTitle(titleRes)
                }
                if (positiveRes != 0) {
                    it.setPositiveButton(positiveRes) { dialog, _ ->
                        onPositive(dialog)
                    }
                }
                if (negativeRes != 0) {
                    it.setNegativeButton(negativeRes) { dialog, _ ->
                        onNegative(dialog)
                    }
                }
            }
            .show()
    }

    fun loading(
        context: Context,
        titleRes: Int,
        cancelable: Int = 0,
        onCancel: (() -> Unit)? = null
    ): AlertDialog {
        return MaterialAlertDialogBuilder(context)
            .setTitle(titleRes)
            .setView(LinearLayout(context).also { linearLayout ->
                linearLayout.orientation = LinearLayout.VERTICAL
                val progressBar = ProgressBar(
                    context,
                    null,
                    android.R.attr.progressBarStyleHorizontal
                )
                linearLayout.addView(
                    progressBar,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                linearLayout.layoutParams = FrameLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                linearLayout.gravity = Gravity.CENTER
                progressBar.isIndeterminate = true
            })
            .setCancelable(false)
            .also { builder ->
                if (cancelable != 0) {
                    builder.setNegativeButton(cancelable) { dialog, _ ->
                        onCancel?.invoke()
                        dialog.dismiss()
                    }
                }
            }
            .show()
    }

}