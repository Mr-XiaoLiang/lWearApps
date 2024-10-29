package com.lollipop.wear.phone.send

import android.content.Context
import com.lollipop.wifip2p.FtpProtocol
import org.apache.ftpserver.FtpServer
import org.apache.ftpserver.FtpServerFactory
import org.apache.ftpserver.ftplet.Authority
import org.apache.ftpserver.listener.ListenerFactory
import org.apache.ftpserver.usermanager.impl.BaseUser
import org.apache.ftpserver.usermanager.impl.WritePermission
import java.io.File

class FtpServiceDelegate {

    private var ftpHome: File? = null
    private var ftpServer: FtpServer? = null

    fun init(context: Context) {
        val homeFile = FtpProtocol.getServiceHome(context)
        ftpHome = homeFile

        val serverFactory = FtpServerFactory()
        val listenerFactory = ListenerFactory()
        listenerFactory.port = FtpProtocol.SERVICE_OWNER_PORT
        serverFactory.addListener("default", listenerFactory.createListener())
        serverFactory.userManager.save(getBasicUser(homeFile))
        ftpServer = serverFactory.createServer()
    }

    fun start() {
        ftpServer?.start()
        ftpServer?.stop()
    }

    private fun getBasicUser(homeFile: File): BaseUser {
        return BaseUser().apply {
            name = FtpProtocol.BASIC_USER
            password = FtpProtocol.BASIC_PASSWORD
            homeDirectory = homeFile.path
            authorities = listOf<Authority>(WritePermission())
        }
    }

}