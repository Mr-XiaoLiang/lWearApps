package com.lollipop.file.sender

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lollipop.file.sender.databinding.ActivityFtpFileManagerBinding
import com.lollipop.file.sender.databinding.ItemFtpCrumbsBinding
import com.lollipop.file.sender.databinding.ItemFtpFileBinding
import it.sauronsoftware.ftp4j.FTPFile

class FtpFileManagerActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityFtpFileManagerBinding.inflate(layoutInflater)
    }

    private val crumbsList = mutableListOf<CrumbsInfo>()

    private val fileList = mutableListOf<FTPFile>()

    private val crumbsAdapter by lazy {
        CrumbsAdapter(layoutInflater, crumbsList, ::onCrumbsClick)
    }

    private val fileAdapter by lazy {
        FileAdapter(layoutInflater, fileList, ::onItemClick, ::onOptionClick)
    }

    private val crumbsOnBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            handlerCrumbsBackPressed()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        initInsets()
        initView()
    }

    private fun initInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.actionBar) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        ViewCompat.setOnApplyWindowInsetsListener(binding.contentListView) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initView() {
        binding.crumbsView.also { view ->
            view.adapter = crumbsAdapter
            view.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        }
        binding.contentListView.also { view ->
            view.adapter = fileAdapter
            view.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        }
        binding.swipeRefreshLayout.setOnRefreshListener { onRefresh() }
        binding.backButton.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.closeButton.setOnClickListener { finish() }
        onBackPressedDispatcher.addCallback(crumbsOnBackPressedCallback)
        onCrumbsChanged()
    }

    private fun onRefresh() {
        // TODO
    }

    private fun onCrumbsClick(info: CrumbsInfo) {
        // TODO
    }

    private fun onItemClick(file: FTPFile) {
        // TODO
    }

    private fun onOptionClick(file: FTPFile) {
        // TODO
    }

    private fun handlerCrumbsBackPressed() {
        // TODO
    }

    private fun onCrumbsChanged() {
        crumbsOnBackPressedCallback.isEnabled = crumbsList.size > 1
    }

    private class CrumbsAdapter(
        private val layoutInflater: LayoutInflater,
        private val data: List<CrumbsInfo>,
        private val clickCallback: (CrumbsInfo) -> Unit
    ) : RecyclerView.Adapter<CrumbsHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrumbsHolder {
            return CrumbsHolder(
                ItemFtpCrumbsBinding.inflate(layoutInflater, parent, false),
                ::onItemClick
            )
        }

        private fun onItemClick(index: Int) {
            clickCallback(data[index])
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: CrumbsHolder, position: Int) {
            holder.bind(data[position])
        }

    }

    private class FileAdapter(
        private val layoutInflater: LayoutInflater,
        private val data: List<FTPFile>,
        private val clickCallback: (FTPFile) -> Unit,
        private val optionClickCallback: (FTPFile) -> Unit
    ) : RecyclerView.Adapter<FileHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileHolder {
            return FileHolder(
                ItemFtpFileBinding.inflate(layoutInflater, parent, false),
                ::onItemClick,
                ::onOptionClick
            )
        }

        private fun onItemClick(index: Int) {
            clickCallback(data[index])
        }

        private fun onOptionClick(index: Int) {
            optionClickCallback(data[index])
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: FileHolder, position: Int) {
            holder.bind(data[position])
        }

    }

    private class CrumbsHolder(
        val binding: ItemFtpCrumbsBinding,
        private val clickCallback: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClick()
            }
        }

        private fun onItemClick() {
            clickCallback(adapterPosition)
        }

        fun bind(file: CrumbsInfo) {
            binding.nameView.text = file.name
        }

    }

    private class FileHolder(
        val binding: ItemFtpFileBinding,
        private val itemClickCallback: (Int) -> Unit,
        private val optionClickCallback: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onItemClick()
            }
            binding.optionButton.setOnClickListener {
                onOptionButtonClick()
            }
        }

        private fun onItemClick() {
            itemClickCallback(adapterPosition)
        }

        private fun onOptionButtonClick() {
            optionClickCallback(adapterPosition)
        }

        fun bind(file: FTPFile) {
            binding.fileNameView.text = file.name
            binding.fileTypeIconView.setImageResource(getFileTypeIcon(file))
        }

        private fun getFileTypeIcon(file: FTPFile): Int {
            return when (file.type) {
                FTPFile.TYPE_DIRECTORY -> R.drawable.baseline_folder_24
                FTPFile.TYPE_FILE -> R.drawable.baseline_file_24px
                FTPFile.TYPE_LINK -> R.drawable.baseline_file_link_24px
                else -> R.drawable.baseline_file_24px
            }
        }

    }

    private class CrumbsInfo(
        val name: String,
        val childList: List<FTPFile>
    )

}