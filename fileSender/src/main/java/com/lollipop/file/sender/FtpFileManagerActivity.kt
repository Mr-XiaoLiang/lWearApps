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
import com.lollipop.file.sender.ftp.FileTransferStation
import com.lollipop.file.sender.ftp.FtpManager
import com.lollipop.file.sender.ftp.RequestResult
import com.lollipop.wear.basic.DialogHelper
import it.sauronsoftware.ftp4j.FTPFile
import java.util.LinkedList

/**
 * 更新流程
 * onResume -
 */
class FtpFileManagerActivity : AppCompatActivity() {

    companion object {
        private const val KEY_TOKEN = "key_token"
        private val OPTION_FILE = arrayOf(
            R.string.option_ftp_file_download,
            R.string.option_ftp_file_copy,
            R.string.option_ftp_file_move,
            R.string.option_ftp_file_delete,
            R.string.option_ftp_file_rename,
        )
        private val OPTION_FOLDER = arrayOf(
            R.string.option_ftp_file_download,
            R.string.option_ftp_file_copy,
            R.string.option_ftp_file_move,
            R.string.option_ftp_file_paste,
            R.string.option_ftp_file_rename,
            R.string.option_ftp_file_create_folder,
            R.string.option_ftp_file_upload,
            R.string.option_ftp_file_delete,
        )
        private val OPTION_LINK = arrayOf(
            R.string.option_ftp_file_download,
            R.string.option_ftp_file_copy,
            R.string.option_ftp_file_move,
            R.string.option_ftp_file_delete,
            R.string.option_ftp_file_rename,
        )
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
        binding.optionButton.setOnClickListener { showFileOption(OPTION_FOLDER, currentPath) }
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

    private fun getFilePath(fileName: String): String {
        return FileTransferStation.getFilePath(currentPath, fileName)
    }

    private fun onItemClick(file: FTPFile) {
        when (file.type) {
            FTPFile.TYPE_DIRECTORY -> {
                val filePath = getFilePath(file.name)
                crumbsList.addLast(CrumbsInfo(name = file.name, path = filePath))
                onCrumbsChanged()
            }

            FTPFile.TYPE_LINK -> {
                crumbsList.addLast(CrumbsInfo(name = file.name, path = file.link))
                onCrumbsChanged()
            }

            FTPFile.TYPE_FILE -> {
                showFileOption(
                    OPTION_FILE,
                    getFilePath(file.name)
                )
            }
        }
    }

    private fun onOptionClick(file: FTPFile) {
        val filePath = getFilePath(file.name)
        when (file.type) {
            FTPFile.TYPE_DIRECTORY -> {
                showFileOption(
                    OPTION_FOLDER,
                    filePath
                )
            }

            FTPFile.TYPE_LINK -> {
                showFileOption(
                    OPTION_LINK,
                    filePath
                )
            }

            FTPFile.TYPE_FILE -> {
                showFileOption(
                    OPTION_FILE,
                    filePath
                )
            }
        }
    }

    private fun handlerCrumbsBackPressed() {
        if (crumbsList.size > 1) {
            crumbsList.removeLast()
            onCrumbsChanged()
        }
        // TODO
    }

    private fun showFileOption(
        optionArray: Array<Int>,
        filePath: String
    ) {
        DialogHelper.list(
            context = this,
            titleRes = R.string.title_options,
            itemResList = optionArray
        ) { dialog, option ->
            dialog.dismiss()
            onOptionClick(option, filePath)
        }
    }

    private fun onOptionClick(option: Int, filePath: String) {
        when (option) {
            R.string.option_ftp_file_stash -> {
                FileTransferStation.stashRemote(filePath)
            }

            R.string.option_ftp_file_download -> {
                FileTransferStation.stashRemote(filePath)
                FileTransferStation.pending = FileTransferStation.Pending.Download
                chooseLocalPath()
            }

            R.string.option_ftp_file_copy -> {
                FileTransferStation.stashRemote(filePath)
                FileTransferStation.pending = FileTransferStation.Pending.Copy
                chooseRemotePath()
            }

            R.string.option_ftp_file_move -> {
                FileTransferStation.stashRemote(filePath)
                FileTransferStation.pending = FileTransferStation.Pending.Move
                chooseRemotePath()
            }

            R.string.option_ftp_file_paste -> {
                when (FileTransferStation.pending) {
                    FileTransferStation.Pending.Copy -> {
                        FileTransferStation.copy(
                            dirUri = filePath,
                            files = FileTransferStation.remoteFiles(),
                        )
                        authorize()
                    }

                    FileTransferStation.Pending.Move -> {
                        FileTransferStation.move(
                            dirUri = filePath,
                            files = FileTransferStation.remoteFiles(),
                        )
                        authorize()
                    }

                    else -> {
                        // 粘贴对于其他的Pending来说无意义
                    }
                }
            }

            R.string.option_ftp_file_rename -> {
                // TODO 显示对话框，修改名称
                // FileTransferStation.rename(filePath, "")
            }

            R.string.option_ftp_file_create_folder -> {
                // TODO 怎么创建文件夹？
            }

            R.string.option_ftp_file_upload -> {
                // TODO 选择本地文件，然后上传流程
            }

            R.string.option_ftp_file_delete -> {
                FileTransferStation.delete(filePath)
                authorize()
            }
        }
    }

    private fun authorize() {
        // TODO 批准执行流程
    }

    private fun chooseRemotePath() {
        // TODO 选择远程路径
    }

    private fun chooseLocalPath() {
        // TODO 选择本地路径
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
        // TODO 加载远程文件列表
    }

    private fun showError() {
        // TODO 显示错误信息
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