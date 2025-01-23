package com.lollipop.file.sender.ftp

sealed class RequestResult<T : Any> {

    class Success<T : Any>(val data: T) : RequestResult<T>()

    class Failure<T : Any>(val error: Throwable) : RequestResult<T>()

    companion object {
        fun <T : Any> success(data: T): RequestResult<T> {
            return Success(data)
        }

        fun <T : Any> failure(error: Throwable): RequestResult<T> {
            return Failure(error)
        }
    }

}