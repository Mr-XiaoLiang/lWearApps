package com.lollipop.wifip2p.stp.handler

sealed class StpContent {

    class FromFile(val path: String) : StpContent()

    class FromText(val text: String) : StpContent()

}