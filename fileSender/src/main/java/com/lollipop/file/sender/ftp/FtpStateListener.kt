package com.lollipop.file.sender.ftp

interface FtpStateListener {

    fun onConnectResult(result: RequestResult<Array<String>>)

    fun onLoginResult(result: RequestResult<Boolean>)

    fun onOperationFails(e: Throwable)

}