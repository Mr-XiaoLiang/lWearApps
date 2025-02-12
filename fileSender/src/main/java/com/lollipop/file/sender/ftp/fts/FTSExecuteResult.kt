package com.lollipop.file.sender.ftp.fts

sealed class ExecuteResult {

    class Success(val option: FTSOption) : ExecuteResult()
    class Failed(val option: FTSOption, val error: Throwable) : ExecuteResult()

}