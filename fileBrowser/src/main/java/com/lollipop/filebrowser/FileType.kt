package com.lollipop.filebrowser

import java.io.File

sealed class FileType(
    val icon: Int,
    val suffix: Array<String>
) {

    data object Folder : FileType(
        R.drawable.baseline_folder_24,
        emptyArray()
    )

    data object Unknown : FileType(
        R.drawable.baseline_insert_drive_file_24,
        emptyArray()
    )

    data object Image : FileType(
        R.drawable.baseline_photo_24,
        arrayOf(
            FileMime.Image.BMP.suffix,
            FileMime.Image.GIF.suffix,
            FileMime.Image.JPG.suffix,
            FileMime.Image.JPEG.suffix,
            FileMime.Image.PNG.suffix
        )
    )

    data object Video : FileType(
        R.drawable.baseline_movie_24,
        arrayOf(
            FileMime.Video.GP3.suffix,
            FileMime.Video.ASF.suffix,
            FileMime.Video.AVI.suffix,
            FileMime.Video.M4U.suffix,
            FileMime.Video.M4V.suffix,
            FileMime.Video.MOV.suffix,
            FileMime.Video.MP4.suffix,
            FileMime.Video.MPE.suffix,
            FileMime.Video.MPEG.suffix,
            FileMime.Video.MPG.suffix,
        )
    )

    data object Audio : FileType(
        R.drawable.baseline_music_note_24,
        arrayOf(
            FileMime.Audio.M3U.suffix,
            FileMime.Audio.M4A.suffix,
            FileMime.Audio.M4B.suffix,
            FileMime.Audio.M4P.suffix,
            FileMime.Audio.MP2.suffix,
            FileMime.Audio.MP3.suffix,
            FileMime.Audio.MPGA.suffix,
            FileMime.Audio.OGG.suffix,
            FileMime.Audio.RMVB.suffix,
            FileMime.Audio.WAV.suffix,
            FileMime.Audio.WMA.suffix,
            FileMime.Audio.WMV.suffix,
        )
    )

    data object Text : FileType(
        R.drawable.baseline_book_24,
        arrayOf(
            FileMime.Text.C.suffix,
            FileMime.Text.CONF.suffix,
            FileMime.Text.CPP.suffix,
            FileMime.Text.H.suffix,
            FileMime.Text.HTM.suffix,
            FileMime.Text.HTML.suffix,
            FileMime.Text.JAVA.suffix,
            FileMime.Text.LOG.suffix,
            FileMime.Text.PROP.suffix,
            FileMime.Text.RC.suffix,
            FileMime.Text.SH.suffix,
            FileMime.Text.TXT.suffix,
            FileMime.Text.XML.suffix,
        )
    )

    data object Apk : FileType(
        R.drawable.baseline_android_24,
        arrayOf(
            FileMime.Application.APK.suffix
        )
    )

    data object Application : FileType(
        R.drawable.baseline_insert_drive_file_24,
        arrayOf(
            FileMime.Application.BIN.suffix,
            FileMime.Application.CLASS.suffix,
            FileMime.Application.DOC.suffix,
            FileMime.Application.DOCX.suffix,
            FileMime.Application.XLS.suffix,
            FileMime.Application.XLSX.suffix,
            FileMime.Application.EXE.suffix,
            FileMime.Application.GTAR.suffix,
            FileMime.Application.GZ.suffix,
            FileMime.Application.JAR.suffix,
            FileMime.Application.JS.suffix,
            FileMime.Application.MPC.suffix,
            FileMime.Application.MSG.suffix,
            FileMime.Application.PDF.suffix,
            FileMime.Application.PPS.suffix,
            FileMime.Application.PPT.suffix,
            FileMime.Application.PPTX.suffix,
            FileMime.Application.RTF.suffix,
            FileMime.Application.TAR.suffix,
            FileMime.Application.TGZ.suffix,
            FileMime.Application.WPS.suffix,
            FileMime.Application.Z.suffix,
            FileMime.Application.ZIP.suffix,
        )
    )

    companion object {
        fun find(file: File): FileType {
            if (file.isDirectory) {
                return Folder
            }
            if (!file.exists()) {
                return Unknown
            }
            val fileName = file.name
            val index = fileName.lastIndexOf(".")
            if (index < 0 || index == fileName.length - 1) {
                return Unknown
            }
            val suffix = fileName.substring(index + 1).lowercase()
            if (isTypeByMime(suffix, Apk)) {
                return Apk
            }
            if (isTypeByMime(suffix, Text)) {
                return Text
            }
            if (isTypeByMime(suffix, Image)) {
                return Image
            }
            if (isTypeByMime(suffix, Audio)) {
                return Audio
            }
            if (isTypeByMime(suffix, Video)) {
                return Video
            }
            if (isTypeByMime(suffix, Application)) {
                return Application
            }
            return Unknown
        }

        private fun isTypeByMime(suffix: String, type: FileType): Boolean {
            for (mime in type.suffix) {
                if (mime == suffix) {
                    return true
                }
            }
            return false
        }

    }

}