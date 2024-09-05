
package com.lollipop.wear.barometer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : AppCompatActivity() {

    // sweepAngle = 140
    // startAngle = 185
    // 300hPa = 140 * 0.1 + 185 + 90 = 289
    // 1100hPa = 140 * 0.9 + 185 + 90 = 41


    // startAngle = 35
    // sweepAngle = 140
    // -800m = 140 * 0.9 + 35 + 90 = 251
    // 9000m = 140 * 0.1 + 35 + 90 = 139

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
    }
}