package com.lollipop.wear.basic

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapter(
    activity: AppCompatActivity,
    private val fragmentList: List<Page>
) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position].clazz.getDeclaredConstructor().newInstance()
    }
}

interface Page {
    val clazz: Class<out Fragment>
}
