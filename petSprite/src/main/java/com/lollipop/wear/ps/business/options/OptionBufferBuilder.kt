package com.lollipop.wear.ps.business.options

import com.lollipop.wear.ps.engine.state.type.Antibiotic
import com.lollipop.wear.ps.engine.state.type.Food
import com.lollipop.wear.ps.engine.state.type.Money
import com.lollipop.wear.ps.engine.state.type.Toy

object OptionBufferBuilder {

    fun buildBuffer(any: Any): String {
        val builder = StringBuilder()
        // 药品，带来健康和抗体
        if (any is Antibiotic) {
            builder.append("💊").append(" ").append(any.antibody).append(" ")
        }
        if (any is Money) {
            builder.append("💰").append(" ").append(any.amount).append(" ")
        }
        if (any is Food) {
            builder.append("🍚").append(" ").append(any.kcal).append(" ")
        }
        if (any is Toy) {
            builder.append("🎮").append(" ").append(any.dopamine).append(" ")
        }
        return builder.toString()
    }

}