package com.lollipop.file.sender.ftp

class ConnectInfo(
    val host: String,
    val port: Int,
) {

    var username: String = ""
    var password: String = ""

    val token: String by lazy {
        "$host:$port"
    }

}