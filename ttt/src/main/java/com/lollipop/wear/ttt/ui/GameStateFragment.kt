package com.lollipop.wear.ttt.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.wear.widget.CircularProgressLayout
import com.lollipop.wear.ttt.databinding.FragmentGameStateBinding
import com.lollipop.wear.ttt.ui.basic.SubpageFragment

/**
 * 游戏状态
 */
class GameStateFragment : SubpageFragment() {

    private val binding by lazy {
        FragmentGameStateBinding.inflate(layoutInflater)
    }

    private var callback: Callback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = findCallback(context)
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.restartButton.totalTime = 3 * 1000L
        binding.restartButton.setOnTimerFinishedListener {
            callback?.callRestart()
        }
        CircularProgressLayoutHelper.attach(binding.restartButton)
    }

    interface Callback {
        fun callRestart()
    }

    private class CircularProgressLayoutHelper : View.OnTouchListener {

        companion object {
            fun attach(view: CircularProgressLayout) {
                view.setOnTouchListener(CircularProgressLayoutHelper())
            }
        }

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            if (v is CircularProgressLayout) {
                when (event?.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        v.startTimer()
                    }

                    MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_DOWN, MotionEvent.ACTION_CANCEL -> {
                        v.stopTimer()
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val x = event.x
                        val y = event.y
                        if (x < 0 || y < 0 || x > v.width || y > v.height) {
                            v.stopTimer()
                        }
                    }
                }
                return true
            }
            return false
        }

    }

}