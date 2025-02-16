package com.lollipop.file.sender.ftp.fts

import android.net.Uri
import java.io.File

sealed class FTSTarget {

    class Local(val uri: Uri) : FTSTarget()

    class Remote(val path: String, val isDir: Boolean) : FTSTarget()

    class Cache(val file: File) : FTSTarget()

}