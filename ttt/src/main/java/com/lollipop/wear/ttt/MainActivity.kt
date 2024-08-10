/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.lollipop.wear.ttt

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.lollipop.wear.ttt.game.GameManager
import com.lollipop.wear.ttt.view.BoardView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun createBoardView(context: Context): BoardView {
        val boardView = BoardView(context)
        boardView.patternColor = 0xAA888888.toInt()
        val padding = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            20F,
            context.resources.displayMetrics
        ).toInt()
        boardView.setPadding(padding, padding, padding, padding)
        boardView.setGameBoardProvider(GameManager)
        boardView.patternStrokeWidth = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            2F,
            context.resources.displayMetrics
        )
        return boardView
    }

}

