package com.lollipop.file.sender

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.lollipop.file.sender.databinding.ActivityFtpFileManagerBinding
import com.lollipop.file.sender.databinding.ItemFtpCrumbsBinding
import com.lollipop.file.sender.databinding.ItemFtpFileBinding
import com.lollipop.file.sender.databinding.ItemSpaceBinding
import com.lollipop.file.sender.ftp.FileTransferStation
import com.lollipop.file.sender.ftp.FtpManager
import com.lollipop.file.sender.ftp.RequestResult
import com.lollipop.wear.basic.DialogHelper
import com.lollipop.wear.utils.FileChooseHelper
import it.sauronsoftware.ftp4j.FTPFile
import java.util.LinkedList

/**
 * 更新流程
 * onResume -
 */
class FtpFileManagerActivity : AppCompatActivity() {

    companion object {
        private const val KEY_TOKEN = "key_token"

        fun start(context: Context, token: String) {
            context.startActivity(
                Intent(context, FtpFileManagerActivity::class.java).apply {
                    putExtra(KEY_TOKEN, token)
                    if (context !is Activity) {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                }
            )
        }

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

    private val fileList = mutableListOf<Any>()

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

    private val log by lazy {
        FTPLog.with(this)
    }

    private val localFileChooser = FileChooseHelper.fileChoose(
        activity = this, callback = ::onLocalFileChoose
    )

    private val localFolderChooser = FileChooseHelper.folderChoose(
        activity = this, callback = ::onLocalFolderChoose
    )

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
        ViewCompat.setOnApplyWindowInsetsListener(binding.controlBar) { v, insets ->
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
        binding.optionButton.setOnClickListener { showFileOption(OPTION_FOLDER, currentPath, true) }

        binding.pasteControlButton.setOnClickListener {
            paste(currentPath)
        }
        binding.uploadControlButton.setOnClickListener {
            upload()
        }
        binding.holdControlButton.setOnClickListener {
            // TODO 当前暂存的文件清单
        }
        binding.flowControlButton.setOnClickListener {
            authorize()
        }

        onCrumbsChanged()
        crumbsAdapter.notifyDataSetChanged()
    }

    private fun onRefresh() {
        binding.swipeRefreshLayout.post {
            binding.swipeRefreshLayout.isRefreshing = true
        }
        loadFileList()
    }

    @SuppressLint("NotifyDataSetChanged")
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
        crumbsAdapter.notifyDataSetChanged()
        onCrumbsChanged()
    }

    private fun onLocalFileChoose(uri: Uri?) {
        uri ?: return
        // 不管状态怎么样，先暂存文件
        FileTransferStation.holdLocal(uri)
        // 如果是上传模式，那么我们进入上传流程
        if (FileTransferStation.pending == FileTransferStation.Pending.Upload) {
            upload()
        }
    }

    private fun onLocalFolderChoose(uri: Uri?) {
        uri ?: return
        // 选择文件夹一般是下载模式
        if (FileTransferStation.pending == FileTransferStation.Pending.Download) {
            // 将下载的目标路径设置为当前路径，并且检索所有本地文件
            FileTransferStation.download(uri, FileTransferStation.remoteFiles())
            // 触发工作流确认
            authorize()
        } else {
            // 否则，记录一下文件夹
            FileTransferStation.holdLocal(uri)
        }
    }

    private fun getFilePath(fileName: String): String {
        return FileTransferStation.getFilePath(currentPath, fileName)
    }

    private fun onItemClick(file: FTPFile) {
        when (file.type) {
            FTPFile.TYPE_DIRECTORY -> {
                val filePath = getFilePath(file.name)
                addCrumbs(CrumbsInfo(name = file.name, path = filePath))
                onCrumbsChanged()
            }

            FTPFile.TYPE_LINK -> {
                addCrumbs(CrumbsInfo(name = file.name, path = file.link))
                onCrumbsChanged()
            }

            FTPFile.TYPE_FILE -> {
                showFileOption(
                    OPTION_FILE,
                    getFilePath(file.name),
                    false
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
                    filePath,
                    true
                )
            }

            FTPFile.TYPE_LINK -> {
                showFileOption(
                    OPTION_LINK,
                    filePath,
                    false
                )
            }

            FTPFile.TYPE_FILE -> {
                showFileOption(
                    OPTION_FILE,
                    filePath,
                    false
                )
            }
        }
    }

    private fun handlerCrumbsBackPressed() {
        removeCrumbsLast()
        onCrumbsChanged()
    }

    private fun showFileOption(
        optionArray: Array<Int>,
        filePath: String,
        isDir: Boolean
    ) {
        DialogHelper.list(
            context = this,
            titleRes = R.string.title_options,
            itemResList = optionArray
        ) { dialog, option ->
            dialog.dismiss()
            onOptionClick(option, filePath, isDir)
        }
    }

    private fun onOptionClick(option: Int, filePath: String, isDir: Boolean) {
        when (option) {
            R.string.option_ftp_file_hold -> {
                FileTransferStation.holdRemote(filePath, isDir)
            }

            R.string.option_ftp_file_download -> {
                FileTransferStation.holdRemote(filePath, isDir)
                FileTransferStation.pending = FileTransferStation.Pending.Download
                chooseLocalPath()
            }

            R.string.option_ftp_file_copy -> {
                FileTransferStation.holdRemote(filePath, isDir)
                FileTransferStation.pending = FileTransferStation.Pending.Copy
                chooseRemotePath()
            }

            R.string.option_ftp_file_move -> {
                // 移动文件，需要先暂存文件
                FileTransferStation.holdRemote(filePath, isDir)
                // 设置Pending类型，然后发起选择，等待结果
                FileTransferStation.pending = FileTransferStation.Pending.Move
                // 进入远程选择模式
                chooseRemotePath()
            }

            R.string.option_ftp_file_paste -> {
                if (isDir) {
                    paste(filePath)
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
                // 先设置Pending类型，然后发起选择，等待结果
                FileTransferStation.pending = FileTransferStation.Pending.Upload
                localFileChooser.launch(Unit)
            }

            R.string.option_ftp_file_delete -> {
                FileTransferStation.delete(filePath, isDir)
                authorize()
            }
        }
    }

    private fun upload() {
        // 将上传的目标路径设置为当前路径，并且检索所有本地文件
        FileTransferStation.upload(currentPath, FileTransferStation.localFiles())
        // 触发工作流确认
        authorize()
    }

    private fun paste(filePath: String) {
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
                FileTransferStation.copy(
                    dirUri = filePath,
                    files = FileTransferStation.remoteFiles(),
                )
                authorize()
            }
        }
    }

    private fun authorize() {
        FtpFlowAuthorizeDialog().show(supportFragmentManager, FtpFlowAuthorizeDialog.TAG)
    }

    private fun chooseRemotePath() {
        updateUIState()
    }

    private fun chooseLocalPath() {
        localFolderChooser.launch(Unit)
    }

    private fun onCrumbsChanged() {
        crumbsOnBackPressedCallback.isEnabled = crumbsList.size > 1
        updateCurrentPath()
    }

    private fun updateUIState() {
        val pending = FileTransferStation.pending
        val hasPaste = FileTransferStation.remoteFileCount > 0 && pending.oneOf(
            FileTransferStation.Pending.Copy,
            FileTransferStation.Pending.Move
        )
        val hasUpload = FileTransferStation.localFileCount > 0 && pending.oneOf(
            FileTransferStation.Pending.Upload,
        )
        val hasFile = FileTransferStation.allFiles.isNotEmpty()
        val hasFlow = FileTransferStation.allFlows.isNotEmpty()
        if (hasPaste || hasUpload || hasFile || hasFlow) {
            // 如果暂存文件是空的，并且没有等待的操作，那么就隐藏按钮
            binding.controlBar.isVisible = true
            binding.pasteControlButton.isVisible = hasPaste
            binding.uploadControlButton.isVisible = hasUpload
            binding.holdControlButton.isVisible = hasFile
            binding.flowControlButton.isVisible = hasFlow
        } else {
            binding.controlBar.isVisible = false
        }
    }

    private fun updateCurrentPath() {
        if (currentPath.isEmpty() || crumbsList.isEmpty()) {
            findClient()?.rootPath { result ->
                when (result) {
                    is RequestResult.Failure<String> -> {
                        log.e("updateCurrentPath.ERROR", result.error)
                        binding.swipeRefreshLayout.post {
                            binding.swipeRefreshLayout.isRefreshing = false
                        }
                        showError()
                    }

                    is RequestResult.Success<String> -> {
                        val rootPath = result.data
                        log.d("updateCurrentPath.ROOT_PATH = $rootPath")
                        currentPath = rootPath
                        resetCrumbs(CrumbsInfo(getString(R.string.label_ftp_root), rootPath))
                        onRefresh()
                    }
                }
            }
        } else {
            currentPath = lastCrumbs()?.path ?: ""
            onRefresh()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun resetCrumbs(info: CrumbsInfo) {
        crumbsList.clear()
        crumbsList.add(info)
        crumbsAdapter.notifyDataSetChanged()
    }

    private fun addCrumbs(info: CrumbsInfo) {
        val index = crumbsList.size
        crumbsList.addLast(info)
        crumbsAdapter.notifyItemInserted(index)
    }

    private fun removeCrumbsLast() {
        val size = crumbsList.size
        if (size > 1) {
            crumbsList.removeLast()
            crumbsAdapter.notifyItemRemoved(size - 1)
        }
    }

    private fun lastCrumbs(): CrumbsInfo? {
        return crumbsList.lastOrNull()
    }

    private fun loadFileList() {
        if (currentPath.isEmpty()) {
            updateCurrentPath()
            return
        }
        lastCrumbs()?.childList?.also {
            updateFileList(it)
        }
        val client = findClient() ?: return
        // 更新目录，然后才刷新列表
        client.cdAndList(currentPath) { result ->
            when (result) {
                is RequestResult.Success -> {
                    val files = result.data
                    val ftpFileList = files.toList()
                    lastCrumbs()?.childList?.also {
                        // 更新面包屑记录
                        it.clear()
                        it.addAll(ftpFileList)
                    }
                    updateFileList(ftpFileList)
                    // 刷新列表的时候，需要关闭下拉刷新
                    binding.swipeRefreshLayout.post {
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                }

                is RequestResult.Failure -> {
                    log.e("loadFileList.ERROR", result.error)
                    binding.swipeRefreshLayout.post {
                        binding.swipeRefreshLayout.isRefreshing = false
                    }
                    showError()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateFileList(list: List<FTPFile>) {
        fileList.clear()
        fileList.addAll(list)
        fileList.add(SpaceInfo)
        fileAdapter.notifyDataSetChanged()
    }

    private fun showError() {
        // TODO 显示错误信息
        Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show()
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
        private val data: List<Any>,
        private val clickCallback: (FTPFile) -> Unit,
        private val optionClickCallback: (FTPFile) -> Unit
    ) : RecyclerView.Adapter<ItemHolder>() {

        companion object {
            private const val ITEM_TYPE_SPACE = 0
            private const val ITEM_TYPE_FILE = 1
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
            return when (viewType) {
                ITEM_TYPE_FILE -> {
                    ItemHolder.FileHolder(
                        ItemFtpFileBinding.inflate(layoutInflater, parent, false),
                        ::onItemClick,
                        ::onOptionClick
                    )
                }

                else -> {
                    ItemHolder.SpacerHolder(
                        ItemSpaceBinding.inflate(layoutInflater, parent, false)
                    )
                }
            }

        }

        private fun onItemClick(index: Int) {
            val any = data[index]
            if (any is FTPFile) {
                clickCallback(any)
            }
        }

        private fun onOptionClick(index: Int) {
            val any = data[index]
            if (any is FTPFile) {
                optionClickCallback(any)
            }
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun getItemViewType(position: Int): Int {
            return when (data[position]) {
                is FTPFile -> ITEM_TYPE_FILE
                else -> ITEM_TYPE_SPACE
            }
        }

        override fun onBindViewHolder(holder: ItemHolder, position: Int) {
            when (holder) {
                is ItemHolder.FileHolder -> {
                    val any = data[position]
                    if (any is FTPFile) {
                        holder.bind(any)
                    }
                }

                is ItemHolder.SpacerHolder -> {
                }
            }
        }

    }

    private sealed class ItemHolder(binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {

        class SpacerHolder(
            private val binding: ItemSpaceBinding
        ) : ItemHolder(binding)

        class FileHolder(
            val binding: ItemFtpFileBinding,
            private val itemClickCallback: (Int) -> Unit,
            private val optionClickCallback: (Int) -> Unit
        ) : ItemHolder(binding) {

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

    private object SpaceInfo

    private class CrumbsInfo(
        val name: String,
        val path: String,
    ) {
        val childList = ArrayList<FTPFile>()
    }

}