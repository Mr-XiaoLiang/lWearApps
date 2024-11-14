package com.lollipop.wear.ps.business

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.wear.widget.CurvedTextView
import com.lollipop.wear.ps.R
import com.lollipop.wear.ps.databinding.PanelGameBinding
import com.lollipop.wear.ps.engine.state.GameOption
import com.lollipop.wear.ps.engine.state.StateManager
import com.lollipop.wear.ps.engine.state.impl.HealthState
import com.lollipop.wear.ps.engine.state.impl.MoodState
import com.lollipop.wear.ps.engine.state.impl.SatiationState
import com.lollipop.wear.widget.CircularProgressIndicator
import java.util.concurrent.Executors

class MainDashboardDelegate(
    private val activity: AppCompatActivity,
    private val binding: PanelGameBinding
) : LifecycleEventObserver, StateManager.OnOptionListener {

    companion object {
        private const val TAG = "MainDashboardDelegate"
    }

    private val executor by lazy {
        Executors.newSingleThreadExecutor()
    }

    private val mainThread by lazy {
        Handler(Looper.getMainLooper())
    }

    private var currentLifecycleState = activity.lifecycle.currentState

    init {
        activity.lifecycle.addObserver(this)
        StateManager.addOptionListener(this)
    }

    fun onCreate() {
        updateState()
    }

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

    override fun onOption(option: GameOption) {
        if (currentLifecycleState.isAtLeast(Lifecycle.State.RESUMED)) {
            updateState()
        }
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        currentLifecycleState = event.targetState
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                updateState()
            }

            Lifecycle.Event.ON_DESTROY -> {
                StateManager.removeOptionListener(this)
            }

            else -> {
            }
        }
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

    fun doWork(block: () -> Unit) {
        executor.execute {
            try {
                block()
            } catch (e: Throwable) {
                Log.e(TAG, "onWork.ERROR", e)
            }
        }
    }

    fun onUI(block: () -> Unit) {
        mainThread.post {
            try {
                block()
            } catch (e: Throwable) {
                Log.e(TAG, "onWork.ERROR", e)
            }
        }
    }

}