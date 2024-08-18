/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.lollipop.wear.ttt

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.lollipop.wear.ttt.databinding.ActivityMainBinding
import com.lollipop.wear.ttt.game.GameControl
import com.lollipop.wear.ttt.game.GameDelegate
import com.lollipop.wear.ttt.game.GameManager
import com.lollipop.wear.ttt.game.GamePlayer
import com.lollipop.wear.ttt.game.GameState
import com.lollipop.wear.ttt.ui.GameBoardFragment
import com.lollipop.wear.ttt.ui.GameRecordFragment
import com.lollipop.wear.ttt.ui.GameStateFragment
import com.lollipop.wear.ttt.ui.GameThemeFragment
import com.lollipop.wear.ttt.ui.basic.SubpageFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity(),
    GameControl.StateListener,
    GameStateFragment.Callback,
    GameBoardFragment.Callback {

    companion object {
        private val pageList: Array<Class<out SubpageFragment>> = arrayOf(
            GameStateFragment::class.java,
            GameBoardFragment::class.java,
            GameRecordFragment::class.java,
            GameThemeFragment::class.java,
        )
    }

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    private val mainHandler by lazy {
        Handler(mainLooper)
    }

    private val minuteTimer by lazy {
        MinuteTimer(mainHandler) {
            updateTime()
        }
    }

    private val gameDelegate = GameDelegate(::onGameStateChanged)


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
        binding.viewPager.adapter = FragmentAdapter(this)
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

        GameManager.addListener(this)

        selectPage(SubPage.Board, false)

    }

    private fun onGameStateChanged(state: GameState) {
        boardFragment?.updateByState(state)
    }

    private fun updateTime() {
        binding.timeView.text = dateFormat.format(Date(System.currentTimeMillis()))
    }

    override fun onResume() {
        super.onResume()
        updateTime()
        minuteTimer.onResume()
        gameDelegate.onResume()
    }

    override fun onPause() {
        super.onPause()
        minuteTimer.onPause()
        gameDelegate.onPause()
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
    }

    private fun selectPage(page: SubPage, animate: Boolean = true) {
        binding.viewPager.setCurrentItem(page.ordinal, animate)
    }

    private inline fun <reified T : Fragment> findFragment(): T? {
        val fragment = supportFragmentManager.fragments.find { it is T }
        if (fragment != null && fragment is T) {
            return fragment
        }
        return null
    }

    private enum class SubPage(val clazz: Class<out SubpageFragment>) {
        State(GameStateFragment::class.java),
        Board(GameBoardFragment::class.java),
        Record(GameRecordFragment::class.java),
        Theme(GameThemeFragment::class.java),
    }

    private class FragmentAdapter(
        activity: AppCompatActivity
    ) : FragmentStateAdapter(activity) {

        private val fragmentList = SubPage.entries

        override fun getItemCount(): Int {
            return fragmentList.size
        }

        override fun createFragment(position: Int): Fragment {
            return fragmentList[position].clazz.getDeclaredConstructor().newInstance()
        }
    }

    private class MinuteTimer(
        private val handler: Handler,
        private val callback: () -> Unit
    ) {

        companion object {
            private const val MINUTE = 60 * 1000L
        }

        private val updateTask = Runnable {
            next()
            callback()
        }

        fun onResume() {
            next()
        }

        private fun next() {
            val now = now()
            val offset = now % MINUTE
            handler.removeCallbacks(updateTask)
            handler.postDelayed(updateTask, MINUTE - offset)
        }

        fun onPause() {
            handler.removeCallbacks(updateTask)
        }

        private fun now(): Long {
            return System.currentTimeMillis()
        }

    }

}

