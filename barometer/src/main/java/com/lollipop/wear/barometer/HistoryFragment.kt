package com.lollipop.wear.barometer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lollipop.wear.barometer.databinding.FragmentHistoryBinding
import com.lollipop.wear.basic.BasicFragment

class HistoryFragment : BasicFragment() {

    private val binding by lazy {
        FragmentHistoryBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

}