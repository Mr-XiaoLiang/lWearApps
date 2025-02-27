package com.lollipop.file.sender.ftp

class ConnectInfo(
    val security: FTPSecurity,
    val host: String,
    val port: Int,
    var username: String = "",
    var password: String = "",
    var isAnonymous: Boolean = false,
) {

    companion object {

        fun fromUri(uri: FtpUri): ConnectInfo {
            return ConnectInfo(
                security = uri.security,
                host = uri.host,
                port = uri.port,
                username = uri.username,
                password = uri.password,
                isAnonymous = uri.anonymous
            )
        }

    }

    var name: String = ""
        set(value) {
            field = value
            displayNameCache = ""
        }

    val token: String by lazy {
        "${security.prefix}:$host:$port"
    }

    val displayHost: String by lazy {
        "$host:$port"
    }

    private var displayNameCache = ""

    val displayName: String
        get() {
            if (displayNameCache.isNotEmpty()) {
                return displayNameCache
            }
            val n = name
            displayNameCache = if (n.isNotEmpty()) {
                "$n ($displayHost)"
            }else {
                displayHost
            }
            return displayNameCache
        }

    fun toUri(): FtpUri {
        return FtpUri(
            security = security,
            host = host,
            port = port,
            username = username,
            password = password,
            anonymous = isAnonymous
        )
    }

}