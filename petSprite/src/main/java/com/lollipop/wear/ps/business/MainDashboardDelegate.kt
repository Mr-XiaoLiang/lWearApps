package com.lollipop.wear.ps.business

import androidx.wear.widget.CurvedTextView
import com.lollipop.wear.ps.R
import com.lollipop.wear.ps.databinding.PanelGameBinding
import com.lollipop.wear.ps.engine.state.impl.HealthState
import com.lollipop.wear.ps.engine.state.impl.MoodState
import com.lollipop.wear.ps.engine.state.impl.SatiationState
import com.lollipop.wear.widget.CircularProgressIndicator

class MainDashboardDelegate(
    val binding: PanelGameBinding
) {

    fun updateState() {
        updateProgress(
            binding.healthValueView,
            binding.healthProgressBar,
            R.string.health,
            HealthState.current,
            HealthState.maxValue
        )
        updateProgress(
            binding.moodValueView,
            binding.moodProgressBar,
            R.string.mood,
            MoodState.current,
            MoodState.maxValue
        )
        updateProgress(
            binding.hungerValueView,
            binding.hungerProgressBar,
            R.string.hunger,
            SatiationState.current,
            SatiationState.maxValue
        )
    }

    private fun updateProgress(
        textView: CurvedTextView,
        progressBar: CircularProgressIndicator,
        resId: Int,
        value: Int,
        maxValue: Int
    ) {
        textView.text = textView.context.getString(resId, value)
        progressBar.progress = (value * 1F / maxValue)
    }

}