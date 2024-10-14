package com.lollipop.filebrowser.file

sealed class FileMime {

    abstract val suffix: String
    abstract val value: String

    class Custom(override val value: String, override val suffix: String = "") : FileMime()

    data object ALL : FileMime() {
        override val suffix: String = ""
        override val value: String = "*/*"
    }

    sealed class Image(override val suffix: String, override val value: String) : FileMime() {
        data object ALL : Image("", "image/*")
        data object PNG : Image("png", "image/png")
        data object BMP : Image("bmp", "image/bmp")
        data object GIF : Image("gif", "image/gif")
        data object JPEG : Image("jpeg", "image/jpeg")
        data object JPG : Image("jpg", "image/jpeg")
    }

    sealed class Text(override val suffix: String, override val value: String) : FileMime() {
        data object ALL : Text("", "text/*")
        data object C   : Text("c", "text/plain")
        data object CONF : Text("conf", "text/plain")
        data object CPP : Text("cpp", "text/plain")
        data object H   : Text("h", "text/plain")
        data object HTM : Text("htm", "text/html")
        data object HTML : Text("html", "text/html")
        data object JAVA : Text("java", "text/plain")
        data object LOG : Text("log", "text/plain")
        data object PROP : Text("prop", "text/plain")
        data object RC  : Text("rc", "text/plain")
        data object SH  : Text("sh", "text/plain")
        data object TXT : Text("txt", "text/plain")
        data object XML : Text("xml", "text/plain")
    }

    sealed class Video(override val suffix: String, override val value: String) : FileMime() {
        data object ALL : Video("", "video/*")
        data object GP3 : Video("3gp", "video/3gpp")
        data object ASF : Video("asf", "video/x-ms-asf")
        data object AVI : Video("avi", "video/x-msvideo")
        data object M4U : Video("m4u", "video/vnd.mpegurl")
        data object M4V : Video("m4v", "video/x-m4v")
        data object MOV : Video("mov", "video/quicktime")
        data object MP4 : Video("mp4", "video/mp4")
        data object MPE : Video("mpe", "video/mpeg")
        data object MPEG : Video("mpeg", "video/mpeg")
        data object MPG : Video("mpg", "video/mpeg")
        data object MPG4 : Video("mpg4", "video/mp4")
    }

    sealed class Audio(override val suffix: String, override val value: String) : FileMime() {
        data object ALL : Audio("", "audio/*")
        data object M3U : Audio("m3u", "audio/x-mpegurl")
        data object M4A : Audio("m4a", "audio/mp4a-latm")
        data object M4B : Audio("m4b", "audio/mp4a-latm")
        data object M4P : Audio("m4p", "audio/mp4a-latm")
        data object MP2 : Audio("mp2", "audio/x-mpeg")
        data object MP3 : Audio("mp3", "audio/x-mpeg")
        data object MPGA : Audio("mpga", "audio/mpeg")
        data object OGG : Audio("ogg", "audio/ogg")
        data object RMVB : Audio("rmvb", "audio/x-pn-realaudio")
        data object WAV : Audio("wav", "audio/x-wav")
        data object WMA : Audio("wma", "audio/x-ms-wma")
        data object WMV : Audio("wmv", "audio/x-ms-wmv")
    }

    sealed class Application(override val suffix: String, override val value: String) : FileMime() {
        data object ALL : Application("", "application/*")
        data object APK : Application("apk", "application/vnd.android.package-archive")
        data object BIN : Application("bin", "application/octet-stream")
        data object CLASS : Application("class", "application/octet-stream")
        data object DOC : Application("doc", "application/msword")
        data object DOCX : Application(
            "docx",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        )

        data object XLS : Application("xls", "application/vnd.ms-excel")
        data object XLSX :
            Application("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")

        data object EXE : Application("exe", "application/octet-stream")
        data object GTAR : Application("gtar", "application/x-gtar")
        data object GZ : Application("gz", "application/x-gzip")
        data object JAR : Application("jar", "application/java-archive")
        data object JS : Application("js", "application/x-javascript")
        data object MPC : Application("mpc", "application/vnd.mpohun.certificate")
        data object MSG : Application("msg", "application/vnd.ms-outlook")
        data object PDF : Application("pdf", "application/pdf")
        data object PPS : Application("pps", "application/vnd.ms-powerpoint")
        data object PPT : Application("ppt", "application/vnd.ms-powerpoint")
        data object PPTX : Application(
            "pptx",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation"
        )

        data object RTF : Application("rtf", "application/rtf")
        data object TAR : Application("tar", "application/x-tar")
        data object TGZ : Application("tgz", "application/x-compressed")
        data object WPS : Application("wps", "application/vnd.ms-works")
        data object Z : Application("z", "application/x-compress")
        data object ZIP : Application("zip", "application/x-zip-compressed")
    }

}