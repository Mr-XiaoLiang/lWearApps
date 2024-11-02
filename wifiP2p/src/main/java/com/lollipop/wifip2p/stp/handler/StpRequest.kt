package com.lollipop.wifip2p.stp.handler

import com.lollipop.wifip2p.stp.data.DataBody

class StpRequest(
    val content: StpContent
) {

    val requestBody: DataBody by lazy {
        when (content) {
            is StpContent.FromFile -> {
                DataBody.File.create(content.path)
            }

            is StpContent.FromText -> {
                DataBody.Text.create(content.text)
            }
        }
    }

    var response: DataBody? = null

    var fileWriteListener: ProgressListener? = null

}