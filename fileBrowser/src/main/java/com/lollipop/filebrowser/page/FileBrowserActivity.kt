package com.lollipop.filebrowser.page

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.lollipop.filebrowser.databinding.ActivityFileBrowserBinding
import com.lollipop.filebrowser.databinding.ItemFileBinding
import com.lollipop.filebrowser.databinding.ItemSpaceBinding
import com.lollipop.filebrowser.file.FileOpenHelper
import com.lollipop.filebrowser.file.FileType
import com.lollipop.wear.basic.doAsync
import com.lollipop.wear.basic.onUI
import com.lollipop.wear.widget.CircularOutlineHelper
import java.io.File
import java.io.FileFilter

class FileBrowserActivity : FilePermissionActivity() {

    companion object {
        private const val SDCARD = "SDCard"
        private const val PARAMS_CURRENT_DIRECTORY = "current_directory"

        @SuppressLint("WearRecents")
        fun start(context: Context, dir: File) {
            val intent = Intent(context, FileBrowserActivity::class.java)
            intent.putExtra(PARAMS_CURRENT_DIRECTORY, dir.path)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    private var rootPath = ""
    private var currentPath = ""
    private val fileList = mutableListOf<Item>()
    private val fileAdapter = FileAdapter(fileList, ::onFileClick)

    private val binding by lazy {
        ActivityFileBrowserBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent(binding.root)
        currentPath = intent.getStringExtra(PARAMS_CURRENT_DIRECTORY) ?: ""
        binding.recyclerView.adapter = fileAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
    }

    private fun onFileClick(file: File) {
        if (file.isDirectory) {
            start(this, file)
        } else {
            FileDetailActivity.open(this, file)
        }
    }

    override fun onResume(hasPermission: Boolean) {
        if (hasPermission) {
            updateFileInfo()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateFileInfo() {
        if (rootPath.isEmpty()) {
            rootPath = getRootDirectory().path
        }
        if (currentPath.isEmpty()) {
            currentPath = rootPath
        }
        doAsync {
            Log.d("FileBrowserActivity", "updateFileInfo: $currentPath")
            val currentFile = File(currentPath)
            val outList = mutableListOf<Item>()
            outList.add(Item.SpaceItem)
            currentFile.listFiles(AllFileFilter())?.let { list ->
                val mutableList = list.toMutableList()
                mutableList.sortBy { it.name }
                mutableList.forEach {
                    outList.add(Item.FileItem(it))
                }
            }
            outList.add(Item.SpaceItem)
            onUI {
                fileList.clear()
                fileList.addAll(outList)
                updateFilePath(currentFile)
                fileAdapter.notifyDataSetChanged()
            }
        }
    }

    private class AllFileFilter : FileFilter {
        override fun accept(file: File): Boolean {
            return true
        }
    }

    private fun updateFilePath(file: File) {
        if (file.path == rootPath) {
            binding.pathView.text = SDCARD
            return
        }
        var f = file
        var index = 0
        val path = StringBuilder()
        var isEnd = false
        while (index < 3) {
            if (f.path == rootPath) {
                path.insert(0, SDCARD)
                isEnd = true
                break
            }
            val name = f.name
            path.insert(0, name)
            f = f.parentFile ?: break
            index++
            path.insert(0, "/")
        }
        if (!isEnd) {
            path.insert(0, "...")
        }
        binding.pathView.text = path.toString()
    }

    private class FileAdapter(
        private val list: List<Item>,
        private val onFileClick: (File) -> Unit
    ) : RecyclerView.Adapter<ItemHolder>() {

        companion object {
            private const val TYPE_FILE = 1
            private const val TYPE_SPACE = 0
        }

        private var inflater: LayoutInflater? = null

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            val inf = inflater ?: LayoutInflater.from(parent.context)
            inflater = inf
            when (viewType) {
                TYPE_FILE -> {
                    return ItemHolder.FileItem(
                        ItemFileBinding.inflate(
                            inf,
                            parent,
                            false
                        ),
                        ::onItemClick
                    )
                }

                else -> {
                    return ItemHolder.SpaceItem(
                        ItemSpaceBinding.inflate(
                            inf,
                            parent,
                            false
                        )
                    )
                }
            }
        }

        private fun onItemClick(position: Int) {
            if (position < 0 || position >= list.size) {
                return
            }
            val item = list[position]
            if (item is Item.FileItem) {
                onFileClick(item.file)
            }
        }

        override fun getItemViewType(position: Int): Int {
            when (list[position]) {
                is Item.FileItem -> {
                    return TYPE_FILE
                }

                is Item.SpaceItem -> {
                    return TYPE_SPACE
                }
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            when (holder) {
                is ItemHolder.FileItem -> {
                    val item = list[position]
                    if (item is Item.FileItem) {
                        holder.bind(item)
                    }
                }

                is ItemHolder.SpaceItem -> {}
            }
        }

    }

    private sealed class Item {

        data class FileItem(val file: File) : Item() {
            val type = FileType.find(file)
        }

        data object SpaceItem : Item()
    }

    private sealed class ItemHolder(
        binding: ViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        class FileItem(
            val binding: ItemFileBinding,
            val onClickCallback: (Int) -> Unit
        ) : ItemHolder(binding) {

            init {
                CircularOutlineHelper.bind(binding.fileItemView)
                binding.fileItemView.setOnClickListener {
                    onClick()
                }
            }

            private fun onClick() {
                onClickCallback(adapterPosition)
            }

            fun bind(item: Item.FileItem) {
                binding.fileNameView.text = item.file.name
                binding.fileTypeView.setImageResource(item.type.icon)
            }

        }

        class SpaceItem(binding: ItemSpaceBinding) : ItemHolder(binding)

    }

}