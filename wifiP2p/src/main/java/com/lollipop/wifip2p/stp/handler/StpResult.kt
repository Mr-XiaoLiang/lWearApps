package com.lollipop.wifip2p.stp.handler

sealed class StpResult<T> {

    class Success<T>(val data: T) : StpResult<T>()

    class Error<T>(val error: Throwable) : StpResult<T>()

    class Empty<T> : StpResult<T>()

}