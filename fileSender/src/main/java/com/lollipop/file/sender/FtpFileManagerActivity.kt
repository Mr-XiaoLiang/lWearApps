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
import com.lollipop.file.sender.ftp.FtpManager
import com.lollipop.file.sender.ftp.RequestResult
import it.sauronsoftware.ftp4j.FTPFile
import java.util.LinkedList

/**
 * 更新流程
 * onResume -
 */
class FtpFileManagerActivity : AppCompatActivity() {

    companion object {
        private const val KEY_TOKEN = "key_token"
    }

    private val binding by lazy {
        ActivityFtpFileManagerBinding.inflate(layoutInflater)
    }

    private val crumbsList = LinkedList<CrumbsInfo>()

    private val fileList = mutableListOf<FTPFile>()

    private val crumbsAdapter by lazy {
        CrumbsAdapter(layoutInflater, crumbsList, ::onCrumbsClick)
    }

    private val fileAdapter by lazy {
        FileAdapter(layoutInflater, fileList, ::onItemClick, ::onOptionClick)
    }

    private var currentPath = ""
    private val currentToken by lazy {
        intent.getStringExtra(KEY_TOKEN) ?: ""
    }

    private val crumbsOnBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            handlerCrumbsBackPressed()
        }
    }

    private fun findClient(): FtpManager.Client? {
        return FtpManager.findClient(currentToken)
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
        // TODO show loading
        loadFileList()
    }

    private fun onCrumbsClick(info: CrumbsInfo) {
        if (crumbsList.indexOf(info) >= 0) {
            while (crumbsList.isNotEmpty()) {
                if (crumbsList.last().path == info.path) {
                    break
                }
                crumbsList.removeLast()
            }
        } else {
            crumbsList.addLast(info)
        }
        onCrumbsChanged()
    }

    private fun onItemClick(file: FTPFile) {
        when (file.type) {
            FTPFile.TYPE_DIRECTORY -> {
                val filePath = currentPath + "/" + file.name
                crumbsList.addLast(CrumbsInfo(name = file.name, path = filePath))
                onCrumbsChanged()
            }

            FTPFile.TYPE_LINK -> {
                crumbsList.addLast(CrumbsInfo(name = file.name, path = file.link))
                onCrumbsChanged()
            }

            FTPFile.TYPE_FILE -> {
                // TODO 打开文件
            }
        }

    }

    private fun onOptionClick(file: FTPFile) {
        // TODO 选项对话框
    }

    private fun handlerCrumbsBackPressed() {
        if (crumbsList.size > 1) {
            crumbsList.removeLast()
            onCrumbsChanged()
        }
        // TODO
    }

    private fun onCrumbsChanged() {
        crumbsOnBackPressedCallback.isEnabled = crumbsList.size > 1
        updateCurrentPath()
    }

    private fun updateCurrentPath() {
        if (currentPath.isEmpty()) {
            findClient()?.rootPath {
                when (it) {
                    is RequestResult.Success -> {
                        val rootPath = it.data
                        currentPath = rootPath
                        crumbsList.clear()
                        crumbsList.add(CrumbsInfo(getString(R.string.label_ftp_root), rootPath))
                        onRefresh()
                    }

                    else -> {
                        showError()
                    }
                }
            }
        } else {
            currentPath = crumbsList.last().path
            onRefresh()
        }
    }

    private fun loadFileList() {
        // TODO
    }

    private fun showError() {
        // TODO
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
        val path: String,
    ) {
        val childList = ArrayList<FTPFile>()
    }

}