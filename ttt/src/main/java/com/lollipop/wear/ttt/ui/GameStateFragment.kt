package com.lollipop.wear.ttt.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lollipop.wear.ttt.databinding.FragmentGameStateBinding

/**
 * 游戏状态
 */
class GameStateFragment : Fragment() {

    private val binding by lazy {
        FragmentGameStateBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

}