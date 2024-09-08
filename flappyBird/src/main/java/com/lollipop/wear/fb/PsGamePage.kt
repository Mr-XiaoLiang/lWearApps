package com.lollipop.wear.fb

import android.view.View
import com.lollipop.wear.fb.databinding.SheetMainBinding
import com.lollipop.wear.game.GameActivity
import com.lollipop.wear.game.GamePage

class PsGamePage : GamePage {

    private var binding: SheetMainBinding? = null

    override fun getForegroundView(activity: GameActivity): View? {
//        binding = SheetMainBinding.inflate(activity.layoutInflater)
//        return binding?.root
        return null
    }

}