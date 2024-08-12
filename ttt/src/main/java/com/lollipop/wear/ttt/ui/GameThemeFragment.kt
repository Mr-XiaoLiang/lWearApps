package com.lollipop.wear.ttt.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lollipop.wear.ttt.databinding.FragmentGameThemeBinding
import com.lollipop.wear.ttt.ui.basic.SubpageFragment

/**
 * 游戏主题
 */
class GameThemeFragment : SubpageFragment() {

    private val binding by lazy {
        FragmentGameThemeBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

}