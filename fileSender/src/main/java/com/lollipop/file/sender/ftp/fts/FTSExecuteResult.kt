package com.lollipop.file.sender.ftp.fts

sealed class ExecuteResult {

    abstract val optionId: Int

    class Success(val option: FTSOption) : ExecuteResult() {
        override val optionId: Int
            get() {
                return option.id
            }
    }

    class Failed(val option: FTSOption, val error: Throwable) : ExecuteResult() {
        override val optionId: Int
            get() {
                return option.id
            }
    }

}