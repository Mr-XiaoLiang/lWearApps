package com.lollipop.file.sender.ftp.fts

sealed class FTSOption {

    class Delete(val target: FtsTarget) : FTSOption()

    /**
     * @param from 源文件
     * @param target 目标文件，或文件夹+
     *
     */
    class Upload(val from: FtsTarget.Cache, val target: FtsTarget.Remote) : FTSOption()

    /**
     * @param from 源文件，或文件夹
     * @param target 目标文件
     */
    class Download(val from: FtsTarget.Remote, val target: FtsTarget.Cache) : FTSOption()

    /**
     * @param from 源文件
     * @param target 目标文件
     */
    class Cache(val from: FtsTarget.Local, val target: FtsTarget.Cache) : FTSOption()

    /**
     * @param from 源文件
     * @param target 本地存储的文件夹
     */
    class Save(val from: FtsTarget.Cache, val target: FtsTarget.Local) : FTSOption()

    /**
     * @param from 源文件
     * @param target 目标文件
     */
    class Rename(val from: FtsTarget.Remote, val target: FtsTarget.Remote) : FTSOption()


}