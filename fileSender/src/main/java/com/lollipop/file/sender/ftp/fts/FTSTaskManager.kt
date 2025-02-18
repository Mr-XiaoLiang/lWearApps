package com.lollipop.file.sender.ftp.fts

import com.lollipop.file.sender.ftp.FtpManager
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

/**
 * @author lollipop
 * 任务管理器
 * 任务管理器，用于管理任务的执行
 */
object FTSTaskManager {

    private val channelMap = ConcurrentHashMap<String, TaskChannel>()

    private fun getChannel(clientToken: String): TaskChannel {
        return channelMap.getOrPut(clientToken) {
            TaskChannel()
        }
    }

    private fun createTaskId(clientToken: String): String {
        val timeHash = System.currentTimeMillis().toString(16)
        return "${clientToken}:${timeHash}"
    }

    /**
     * 执行任务
     * @param client 客户端
     * @param list 任务列表
     * @param contextProvider 上下文提供者
     * @return 任务对象
     */
    fun execute(
        client: FtpManager.Client,
        list: List<FTSOption>,
        contextProvider: FTSContextProvider,
    ): FTSTask {
        // 获取token
        val clientToken = client.info.token
        // 创建任务ID
        val taskId = createTaskId(clientToken)
        // 创建任务对象
        val task = FTSTask(
            taskId = taskId,
            ftpToken = clientToken,
            optionArray = list.toTypedArray(),
            contextProvider = contextProvider,
        )
        // 获取任务通道，然后添加任务
        getChannel(clientToken).add(task)
        return task
    }

    /**
     * 任务通道
     * 任务通道，用于管理任务的执行
     * 任务通道，通过单线程执行器，来保证顺序执行，同时异步执行
     */
    private class TaskChannel {

        /**
         * 单线程执行器
         * 通过单线程执行器，来保证顺序执行，同时异步执行
         */
        private val executor by lazy {
            Executors.newSingleThreadExecutor()
        }

        private val taskMap = HashMap<String, FTSTask>()

        fun add(task: FTSTask) {
            executor.execute(task)
            taskMap[task.taskId] = task
        }

    }

}