package com.lollipop.file.sender.ftp

import it.sauronsoftware.ftp4j.FTPClient

enum class FTPSecurity(
    val prefix: String,
    val key: Int,
    val defaultPort: Int,
) {
    FTP("ftp", FTPClient.SECURITY_FTP, 21),
    FTPS("ftps", FTPClient.SECURITY_FTPS, 990),
    FTPES("ftpes", FTPClient.SECURITY_FTPES, 21);

    companion object {
        fun from(key: Int): FTPSecurity {
            return entries.firstOrNull { it.key == key } ?: FTP
        }

        fun find(prefix: String): FTPSecurity {
            if (prefix.isEmpty()) {
                return FTP
            }
            return entries.firstOrNull { it.prefix.equals(prefix, true) } ?: FTP
        }
    }

}