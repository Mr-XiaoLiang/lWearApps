package com.lollipop.wear.ps.engine.state

import com.lollipop.wear.ps.R

sealed class GameOptionReason(val display: Int) {

    data object None : GameOptionReason(0)

    data object HealthLow : GameOptionReason(R.string.reason_health_low)

    data object MoodLow : GameOptionReason(R.string.reason_mood_low)

    data object SatiationLow : GameOptionReason(R.string.reason_satiation_low)

}