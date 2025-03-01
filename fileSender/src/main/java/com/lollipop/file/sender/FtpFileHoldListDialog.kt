package com.lollipop.file.sender

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.file.sender.components.ListBottomSheetDialog
import com.lollipop.file.sender.databinding.ItemFtpHoldFileBinding
import com.lollipop.file.sender.ftp.FileTransferStation
import com.lollipop.file.sender.ftp.fts.FTSTarget

class FtpFileHoldListDialog : ListBottomSheetDialog() {

    companion object {
        const val TAG = "FtpFileHoldListDialog"
    }

    private val adapter by lazy {
        FileItemAdapter(FileTransferStation.allFiles, ::onDeleteClick)
    }

    override fun initContentView(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter
    }

    private fun onDeleteClick(file: FTSTarget) {
        FileTransferStation.release(file)
    }

    private class FileItemAdapter(
        private val fileList: List<FTSTarget>,
        private val onDeleteClick: (FTSTarget) -> Unit
    ) : RecyclerView.Adapter<FileItemHolder>() {

        private var layoutInflater: LayoutInflater? = null

        private fun getLayoutInflater(parent: ViewGroup): LayoutInflater {
            return layoutInflater ?: LayoutInflater.from(parent.context).also {
                layoutInflater = it
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileItemHolder {
            return FileItemHolder(
                ItemFtpHoldFileBinding.inflate(
                    getLayoutInflater(parent),
                    parent,
                    false
                ),
                ::onDeleteClick
            )
        }

        private fun onDeleteClick(position: Int) {
            if (position < 0 || position >= fileList.size) {
                return
            }
            onDeleteClick(fileList[position])
            notifyItemRemoved(position)
        }

        override fun getItemCount(): Int {
            return fileList.size
        }

        override fun onBindViewHolder(holder: FileItemHolder, position: Int) {
            holder.bind(fileList[position])
        }

    }

    private class FileItemHolder(
        val binding: ItemFtpHoldFileBinding,
        val onDeleteClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.deleteButton.setOnClickListener {
                onDeleteButtonClick()
            }
        }

        private fun onDeleteButtonClick() {
            onDeleteClick(adapterPosition)
        }

        fun bind(file: FTSTarget) {
            when (file) {
                is FTSTarget.Local -> {
                    val path = file.uri.toString()
                    bind(
                        FileTransferStation.findFileName(path),
                        path,
                        R.drawable.baseline_file_24
                    )
                }

                is FTSTarget.Cache -> {
                    val path = file.file.path
                    bind(
                        FileTransferStation.findFileName(path),
                        path,
                        R.drawable.baseline_cached_24
                    )
                }

                is FTSTarget.Remote -> {
                    val path = file.path
                    bind(
                        FileTransferStation.findFileName(path),
                        path,
                        R.drawable.baseline_cloud_24
                    )
                }
            }
        }

        private fun bind(fileName: String, filePath: String, fileType: Int) {
            binding.fileNameView.text = fileName
            binding.filePathView.text = filePath
            binding.fileTypeIconView.setImageResource(fileType)
        }

        private fun getTypeIcon(file: FTSTarget): Int {
            return when (file) {
                is FTSTarget.Local -> R.drawable.baseline_file_24
                is FTSTarget.Remote -> R.drawable.baseline_cloud_24
                is FTSTarget.Cache -> R.drawable.baseline_cached_24
            }
        }

    }

}