package com.lollipop.ktouch.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.lollipop.ktouch.databinding.ActivityPageManagerBinding

abstract class PagerActivity : AppCompatActivity(), SubPager.Callback {

    private var currentPageMode = SubPageMode.Display

    private val binding by lazy {
        ActivityPageManagerBinding.inflate(layoutInflater)
    }

    protected abstract val pageArray: Array<Class<out SubPager>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.viewPager.adapter = PagerAdapter(this, pageArray)
        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        notifyPagerModeChange(SubPageMode.Display)
    }

    override fun getSubPageMode(): SubPageMode {
        return currentPageMode
    }

    override fun postPageMode(mode: SubPageMode) {
        notifyPagerModeChange(mode)
    }

    private fun notifyPagerModeChange(newMode: SubPageMode) {
        binding.viewPager.isUserInputEnabled = newMode == SubPageMode.Edit
        currentPageMode = newMode
        supportFragmentManager.fragments.forEach {
            if (it is SubPager) {
                if (it.isResumed && it.isVisible) {
                    it.onPageModeChange(newMode)
                }
            }
        }
    }

    class PagerAdapter(
        activity: AppCompatActivity,
        private val pageArray: Array<Class<out SubPager>>
    ) : FragmentStateAdapter(activity) {
        override fun getItemCount(): Int {
            return pageArray.size
        }

        override fun createFragment(position: Int): Fragment {
            return pageArray[position].getDeclaredConstructor().newInstance()
        }

    }

}