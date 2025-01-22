package com.lollipop.file.sender.ftp

import com.lollipop.wear.basic.ListenerManager
import it.sauronsoftware.ftp4j.FTPClient

object FtpManager {

    private val clientMap = HashMap<String, Client>()

    fun getOrCreate(info: ConnectInfo): Client {
        return clientMap[info.token] ?: Client(info).also {
            clientMap[info.token] = it
        }
    }

    fun findClient(token: String): Client? {
        return clientMap[token]
    }

    fun removeClient(token: String) {
        clientMap.remove(token)
    }

    class Client(
        val info: ConnectInfo
    ) {

        val impl = FTPClient()
        private val listenerManager = ListenerManager<FtpStateListener>()

        fun addListener(listener: FtpStateListener) {
            listenerManager.add(listener)
        }

        fun removeListener(listener: FtpStateListener) {
            listenerManager.remove(listener)
        }

        private fun notifyConnectResult(result: Result<Array<String>>) {
            listenerManager.invoke {
                it.onConnectResult(result)
            }
        }

        private fun notifyLoginResult(result: Result<Boolean>) {
            listenerManager.invoke {
                it.onLoginResult(result)
            }
        }

        fun connect() {
            try {
                val result = impl.connect(info.host, info.port)
                notifyConnectResult(Result.success(result))
            } catch (e: Throwable) {
                notifyConnectResult(Result.failure(e))
            }
            try {
                impl.login(info.username, info.password)
                notifyLoginResult(Result.success(true))
            } catch (e: Throwable) {
                notifyLoginResult(Result.failure(e))
            }
        }

    }

}