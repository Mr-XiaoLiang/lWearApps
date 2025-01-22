package com.lollipop.file.sender.ftp

interface FtpStateListener {

    fun onConnectResult(result: Result<Array<String>>)

    fun onLoginResult(result: Result<Boolean>)

}