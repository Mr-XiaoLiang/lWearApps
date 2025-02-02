package com.lollipop.wear.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner


/**
 * 一个文件选择器的辅助工具类
 * 主要用于包装一些通过系统文件管理器来获取和选择文件的方法
 */
object FileChooseHelper {

    /**
     * 选择文件夹路径
     */
    fun folderChoose(
        activity: ComponentActivity,
        callback: ActivityResultCallback<Uri?>
    ): ActivityResultLauncherWrapper<Unit> {
        return autoRegister(activity, FolderChooseContract(), callback)
    }

    /**
     * 选择文件路径
     */
    fun fileChoose(
        activity: ComponentActivity,
        mimeType: Array<String> = emptyArray(),
        callback: ActivityResultCallback<Uri?>
    ): ActivityResultLauncherWrapper<Unit> {
        return autoRegister(activity, FileChooseContract(mimeType), callback)
    }

    private fun <I, O> autoRegister(
        activity: ComponentActivity,
        contract: ActivityResultContract<I, O>,
        callback: ActivityResultCallback<O>
    ): ActivityResultLauncherWrapper<I> {
        return AutoRegisterDelegate.autoRegister(activity, contract, callback)
    }

    private class FolderChooseContract : ActivityResultContract<Unit, Uri?>() {
        override fun createIntent(context: Context, input: Unit): Intent {
            return Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            if (resultCode == Activity.RESULT_OK) {
                return intent?.data
            }
            return null
        }
    }

    private class FileChooseContract(
        private val mimeType: Array<String> = emptyArray()
    ) : ActivityResultContract<Unit, Uri?>() {
        override fun createIntent(context: Context, input: Unit): Intent {
            return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                if (mimeType.size == 1) {
                    type = mimeType[0]
                } else if (mimeType.size > 1) {
                    putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
                } else {
                    type = "*/*"
                }
            }
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            if (resultCode == Activity.RESULT_OK) {
                return intent?.data
            }
            return null
        }
    }

    class AutoRegisterDelegate<I, O> private constructor(
        private val activity: ComponentActivity,
        private val contract: ActivityResultContract<I, O>,
        private val callback: ActivityResultCallback<O>,
        private val launcherWrapper: ActivityResultLauncherWrapper<I>
    ) : LifecycleEventObserver {

        companion object {
            fun <I, O> autoRegister(
                activity: ComponentActivity,
                contract: ActivityResultContract<I, O>,
                callback: ActivityResultCallback<O>
            ): ActivityResultLauncherWrapper<I> {
                val launcherWrapper = ActivityResultLauncherWrapper<I>()
                if (activity.lifecycle.currentState.isAtLeast(Lifecycle.State.CREATED)) {
                    launcherWrapper.register(activity, contract, callback)
                } else {
                    val delegate =
                        AutoRegisterDelegate(activity, contract, callback, launcherWrapper)
                    activity.lifecycle.addObserver(delegate)
                }
                return launcherWrapper
            }

        }

        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            when (event) {
                Lifecycle.Event.ON_CREATE -> {
                    register()
                }

                else -> {
                }
            }
        }

        private fun register() {
            activity.lifecycle.removeObserver(this)
            launcherWrapper.register(activity, contract, callback)
        }

    }

    class ActivityResultLauncherWrapper<I> : ActivityResultLauncher<I>() {

        private var launcherImpl: ActivityResultLauncher<I>? = null

        private fun setImpl(impl: ActivityResultLauncher<I>) {
            launcherImpl = impl
        }

        fun <O> register(
            activity: ComponentActivity,
            contract: ActivityResultContract<I, O>,
            callback: ActivityResultCallback<O>,
        ) {
            val launcher = activity.registerForActivityResult(contract, callback)
            setImpl(launcher)
        }

        override fun launch(input: I, options: ActivityOptionsCompat?) {
            launcherImpl?.launch(input, options)
        }

        override fun unregister() {
            launcherImpl?.unregister()
        }

        override fun getContract(): ActivityResultContract<I, *> {
            return launcherImpl?.contract ?: throw IllegalStateException()
        }
    }

}