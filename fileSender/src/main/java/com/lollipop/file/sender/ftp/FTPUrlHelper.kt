package com.lollipop.file.sender.ftp

import android.net.Uri
import androidx.core.net.toUri

/**
 * @param useSSL 如果为 true，则使用 FTPS 协议；否则使用 FTP 协议。
 * @param host 服务器的主机名。
 * @param port 服务器的端口号。
 * @param anonymous 如果为 true，则使用匿名登录；否则使用提供的用户名和密码。
 * @param username 登录的用户名，如果匿名登录则为 null。
 * @param password 登录的密码，如果匿名登录则为 null。
 */
class FtpUri(
    val useSSL: Boolean,
    val host: String,
    val port: Int,
    val anonymous: Boolean,
    val username: String,
    val password: String,
) {

    companion object {
        const val FTP_PREFIX = "ftp"
        const val FTPS_PREFIX = "ftps"

        val EMPTY = FtpUri(
            useSSL = false,
            host = "",
            port = 21,
            anonymous = true,
            username = "",
            password = "",
        )

        fun parse(uri: String): FtpUri {
            return try {
                parse(uri.toUri())
            } catch (e: Exception) {
                EMPTY
            }
        }

        fun parse(uri: Uri): FtpUri {
            val useSSL = uri.scheme == FTPS_PREFIX
            val host = uri.host ?: ""
            var port = uri.port
            if (port <= 0) {
                port = if (useSSL) {
                    990
                } else {
                    21
                }
            }
            val userInfo = uri.userInfo ?: ""
            val anonymous = userInfo.isEmpty()
            val userInfoArray = userInfo.split(":")
            val username = if (anonymous) {
                ""
            } else {
                userInfoArray.getOrNull(0) ?: ""
            }
            val password = if (anonymous) {
                ""
            } else {
                userInfoArray.getOrNull(1) ?: ""
            }
            return FtpUri(
                useSSL = useSSL,
                host = host,
                port = port,
                anonymous = anonymous,
                username = username,
                password = password,
            )
        }

    }

    /**
     * 根据给定的参数构建 FTP 或 FTPS URL。
     * @return 构建好的 FTP 或 FTPS URL 字符串。
     */
    fun getUrl(): String {
        val scheme = if (useSSL) {
            FTPS_PREFIX
        } else {
            FTP_PREFIX
        }
        val authority = if (anonymous) {
            "$host:$port"
        } else {
            "$username:$password@$host:$port"
        }
        return Uri.Builder()
            .scheme(scheme)
            .encodedAuthority(authority)
            .build()
            .toString()
    }

}