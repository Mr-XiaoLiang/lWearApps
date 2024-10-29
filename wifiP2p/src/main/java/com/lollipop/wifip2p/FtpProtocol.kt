package com.lollipop.wifip2p

import android.content.Context
import java.io.File

object FtpProtocol {

    const val SERVICE_OWNER_PORT = 1233

    const val BASIC_USER: String = "lollipop"

    const val BASIC_PASSWORD: String = "lollipop"

    const val DIRECTORY_HOME = "FTP"

    fun getServiceHome(context: Context): File {
        return File(context.filesDir, DIRECTORY_HOME)
    }

}