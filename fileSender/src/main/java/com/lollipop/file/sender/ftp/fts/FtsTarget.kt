package com.lollipop.file.sender.ftp.fts

import android.net.Uri
import java.io.File

sealed class FtsTarget {

    class Local(val uri: Uri) : FtsTarget()

    class Remote(val path: String, val isDir: Boolean) : FtsTarget()

    class Cache(val file: File) : FtsTarget()

}