/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.lollipop.wear.ttt

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.lollipop.wear.basic.Page
import com.lollipop.wear.basic.PagerAdapter
import com.lollipop.wear.basic.findTypedFragment
import com.lollipop.wear.devices.TimeViewDelegate
import com.lollipop.wear.ttt.databinding.ActivityMainBinding
import com.lollipop.wear.ttt.game.GameControl
import com.lollipop.wear.ttt.game.GameDelegate
import com.lollipop.wear.ttt.game.GameManager
import com.lollipop.wear.ttt.game.GamePlayer
import com.lollipop.wear.ttt.game.GameState
import com.lollipop.wear.ttt.game.PlayerScore
import com.lollipop.wear.ttt.theme.PieceTheme
import com.lollipop.wear.ttt.ui.GameBoardFragment
import com.lollipop.wear.ttt.ui.GameRecordFragment
import com.lollipop.wear.ttt.ui.GameStateFragment
import com.lollipop.wear.ttt.ui.GameThemeFragment
import com.lollipop.wear.ttt.ui.basic.SubpageFragment

class MainActivity : AppCompatActivity(),
    GameControl.StateListener,
    GameStateFragment.Callback,
    GameBoardFragment.Callback,
    GameRecordFragment.Callback {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val minuteTimerDelegate by lazy {
        TimeViewDelegate { value ->
            binding.timeView.text = value
        }
    }

    private val gameDelegate by lazy {
        GameDelegate(this, ::onGameStateChanged)
    }


    private val stateFragment: GameStateFragment?
        get() {
            return findFragment()
        }

    private val boardFragment: GameBoardFragment?
        get() {
            return findFragment()
        }

    private val recordFragment: GameRecordFragment?
        get() {
            return findFragment()
        }

    private val themeFragment: GameThemeFragment?
        get() {
            return findFragment()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        init()

        val pageList = getPageList()
        binding.viewPager.adapter = PagerAdapter(this, pageList)
        binding.pageIndicator.indicatorCount = pageList.size
        binding.viewPager.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    binding.pageIndicator.indicatorIndex = position
                    binding.pageIndicator.indicatorOffset = 0F
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    binding.pageIndicator.indicatorIndex = position
                    binding.pageIndicator.indicatorOffset = positionOffset
                }
            }
        )
        selectPage(SubPage.Board, false)
        gameDelegate.init()
    }

    private fun getPageList(): List<SubPage> {
        val subPageList = mutableListOf<SubPage>()
        subPageList.add(SubPage.State)
        subPageList.add(SubPage.Board)
        subPageList.add(SubPage.Record)
        if (PieceTheme.resourceArray.size > 1) {
            subPageList.add(SubPage.Theme)
        }
        return subPageList
    }

    private fun init() {
        GameManager.addListener(this)
        PieceTheme.init(this)
    }

    private fun onGameStateChanged(state: GameState) {
        boardFragment?.updateByState(state)
    }

    override fun onResume() {
        super.onResume()
        minuteTimerDelegate.onResume()
        gameDelegate.onResume()
    }

    override fun onPause() {
        super.onPause()
        minuteTimerDelegate.onPause()
        gameDelegate.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        GameManager.removeListener(this)
    }

    override fun callRestart() {
        gameDelegate.reset()
        selectPage(SubPage.Board)
    }

    override fun startGame() {
        GameManager.newGame()
        boardFragment?.onNewGame()
    }

    override fun getGameState(): GameState {
        return gameDelegate.state
    }

    override fun onPlayerSelect(player: GamePlayer) {
        gameDelegate.addPlayer(player)
    }

    override fun isPlayerSelected(player: GamePlayer): Boolean {
        return gameDelegate.playerA == player || gameDelegate.playerB == player
    }

    override fun onPieceClick(x: Int, y: Int) {
        gameDelegate.onClick(x, y)
    }

    override fun getWinner(): GamePlayer? {
        return gameDelegate.winner
    }

    override fun getPlayerScore(): PlayerScore? {
        return gameDelegate.getScore()
    }


    override fun onGameStart() {
        gameDelegate.start()
    }

    override fun onGameEnd(winner: GamePlayer?) {
        gameDelegate.end(winner)
    }

    override fun onPlayerChanged() {
        boardFragment?.onPlayerChanged()
    }

    override fun onCurrentHandChanged() {
        boardFragment?.onCurrentHandChanged()
        gameDelegate.onCurrentHandChanged()
    }

    override fun getHumanAScore(): Int {
        return gameDelegate.humanAScoreHistory
    }

    override fun getHumanBScore(): Int {
        return gameDelegate.humanBScoreHistory
    }

    override fun getRobotScore(): Int {
        return gameDelegate.robotScoreHistory
    }

    override fun getOnceRecord(): Int {
        return gameDelegate.maxScore
    }

    override fun onOnceRecordChanged(onceRecord: Int) {
        return gameDelegate.changeMaxScore(onceRecord)
    }

    private fun selectPage(page: SubPage, animate: Boolean = true) {
        binding.viewPager.setCurrentItem(page.ordinal, animate)
    }

    private inline fun <reified T : Fragment> findFragment(): T? {
        return supportFragmentManager.findTypedFragment()
    }

    private enum class SubPage(override val clazz: Class<out SubpageFragment>): Page {
        State(GameStateFragment::class.java),
        Board(GameBoardFragment::class.java),
        Record(GameRecordFragment::class.java),
        Theme(GameThemeFragment::class.java),
    }

}

