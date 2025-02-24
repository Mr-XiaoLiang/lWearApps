package com.lollipop.file.sender.ftp

class ConnectInfo(
    val host: String,
    val port: Int,
    var username: String = "",
    var password: String = "",
    var isAnonymous: Boolean = false,
) {

    val token: String by lazy {
        "$host:$port"
    }

    val displayName: String by lazy {
        "$host:$port"
    }

}