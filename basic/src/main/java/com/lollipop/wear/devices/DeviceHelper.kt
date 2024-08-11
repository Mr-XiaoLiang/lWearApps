package com.lollipop.wear.devices

import android.content.Context

val Context.isScreenRound: Boolean
    get() {
        return resources.configuration.isScreenRound
    }

object DeviceHelper {
    fun arcDimenToAngle(radius: Float, arcDimen: Float): Float {
        // 角度计算长度：2 * PI * radius * (angle / 360)
        // 反过来，通过长度尺寸换算角度
        return (arcDimen / (2 * Math.PI * radius) * 360).toFloat()
    }
}
