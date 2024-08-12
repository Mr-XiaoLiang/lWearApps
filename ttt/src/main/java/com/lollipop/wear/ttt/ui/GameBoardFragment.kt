package com.lollipop.wear.ttt.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lollipop.wear.ttt.databinding.FragmentGameBoardBinding
import com.lollipop.wear.ttt.ui.basic.SubpageFragment

/**
 * 游戏棋盘
 */
class GameBoardFragment: SubpageFragment() {

    private val binding by lazy {
        FragmentGameBoardBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

}