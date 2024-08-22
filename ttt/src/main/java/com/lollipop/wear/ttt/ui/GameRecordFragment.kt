package com.lollipop.wear.ttt.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lollipop.wear.ttt.databinding.FragmentGameRecordBinding
import com.lollipop.wear.ttt.game.GamePlayer
import com.lollipop.wear.ttt.ui.basic.SubpageFragment

/**
 * 游戏战绩
 */
class GameRecordFragment : SubpageFragment() {

    private val binding by lazy {
        FragmentGameRecordBinding.inflate(layoutInflater)
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

    override fun onResume() {
        super.onResume()
        val humanAScore = callback?.getHumanAScore() ?: 0
        val humanBScore = callback?.getHumanBScore() ?: 0
        val robotScore = callback?.getRobotScore() ?: 0
        binding.playARecord.text = humanAScore.toString()
        binding.playBRecord.text = humanBScore.toString()
        binding.playRobotRecord.text = robotScore.toString()
        binding.playAIcon.setBackgroundResource(GamePlayer.HumanA.colorRes)
        binding.playBIcon.setImageResource(GamePlayer.HumanA.iconRes)
        binding.playBIcon.setBackgroundResource(GamePlayer.HumanB.colorRes)
        binding.playBIcon.setImageResource(GamePlayer.HumanB.iconRes)
        binding.playRobotIcon.setBackgroundResource(GamePlayer.Robot.colorRes)
        binding.playRobotIcon.setImageResource(GamePlayer.Robot.iconRes)
    }

    interface Callback {
        fun getHumanAScore(): Int
        fun getHumanBScore(): Int
        fun getRobotScore(): Int
    }

}