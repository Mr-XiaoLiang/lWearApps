package com.lollipop.file.sender.ftp.fts

sealed class FTSOption {

    class Delete(val target: FTSTarget) : FTSOption()

    /**
     * @param from 源文件
     * @param target 目标文件，或文件夹+
     *
     */
    class Upload(val from: FTSTarget.Cache, val target: FTSTarget.Remote) : FTSOption()

    /**
     * @param from 源文件，或文件夹
     * @param target 目标文件
     */
    class Download(val from: FTSTarget.Remote, val target: FTSTarget.Cache) : FTSOption()

    /**
     * @param from 源文件
     * @param target 目标文件
     */
    class Cache(val from: FTSTarget.Local, val target: FTSTarget.Cache) : FTSOption()

    /**
     * @param from 源文件
     * @param target 本地存储的文件夹
     */
    class Save(val from: FTSTarget.Cache, val target: FTSTarget.Local) : FTSOption()

    /**
     * @param from 源文件
     * @param target 目标文件
     */
    class Rename(val from: FTSTarget.Remote, val target: FTSTarget.Remote) : FTSOption()


}