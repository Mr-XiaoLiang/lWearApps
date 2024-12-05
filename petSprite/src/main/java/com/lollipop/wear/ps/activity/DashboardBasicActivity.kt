package com.lollipop.wear.ps.activity

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.lollipop.wear.ps.business.MainDashboardDelegate
import com.lollipop.wear.ps.databinding.ActivityDashboardBasicBinding

abstract class DashboardBasicActivity : AppCompatActivity() {

    private val basicBinding by lazy {
        ActivityDashboardBasicBinding.inflate(layoutInflater)
    }

    private val dashboardDelegate by lazy {
        MainDashboardDelegate(this, basicBinding.dashboardPanel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(basicBinding.root)
        basicBinding.contentGroup.addView(
            createContent(),
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dashboardDelegate.onCreate()
    }

    protected abstract fun createContent(): View

}