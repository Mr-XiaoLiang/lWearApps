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

    companion object {
        const val ONCE_RECORD_MAX = 10
        const val ONCE_RECORD_MIN = 1
    }

    private val binding by lazy {
        FragmentGameRecordBinding.inflate(layoutInflater)
    }

    private var callback: Callback? = null

    private var onceRecord = ONCE_RECORD_MIN

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
        binding.onceRecordReduceView.setOnClickListener {
            onceRecordUpdate(-1)
        }
        binding.onceRecordIncreaseView.setOnClickListener {
            onceRecordUpdate(1)
        }
    }

    private fun onceRecordUpdate(offset: Int) {
        val old = onceRecord
        onceRecord += offset
        if (onceRecord < ONCE_RECORD_MIN) {
            onceRecord = ONCE_RECORD_MIN
        } else if (onceRecord > ONCE_RECORD_MAX) {
            onceRecord = ONCE_RECORD_MAX
        }
        binding.onceRecordNumberView.text = onceRecord.toString()
        binding.onceRecordReduceView.alpha = if (onceRecord == ONCE_RECORD_MIN) {
            0.5F
        } else {
            1F
        }
        binding.onceRecordIncreaseView.alpha = if (onceRecord == ONCE_RECORD_MAX) {
            0.5F
        } else {
            1F
        }
        if (old != onceRecord) {
            callback?.onOnceRecordChanged(onceRecord)
        }
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

        onceRecord = callback?.getOnceRecord() ?: 0
        onceRecordUpdate(0)
    }

    interface Callback {
        fun getHumanAScore(): Int
        fun getHumanBScore(): Int
        fun getRobotScore(): Int

        fun getOnceRecord(): Int
        fun onOnceRecordChanged(onceRecord: Int)
    }

}