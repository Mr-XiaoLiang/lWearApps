package com.lollipop.wear.ps

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lollipop.wear.animation.AnimationHelper
import com.lollipop.wear.animation.converter.Alpha
import com.lollipop.wear.animation.converter.TranslationX
import com.lollipop.wear.animation.dsl.withView
import com.lollipop.wear.animation.end.HideOnClose
import com.lollipop.wear.animation.start.ShowOnStart
import com.lollipop.wear.ps.business.MainDashboardDelegate
import com.lollipop.wear.ps.business.options.Foods
import com.lollipop.wear.ps.business.page.ContentPageFragment
import com.lollipop.wear.ps.business.page.ListContentPageFragment
import com.lollipop.wear.ps.business.page.OptionListPageFragment
import com.lollipop.wear.ps.business.page.StatePageFragment
import com.lollipop.wear.ps.databinding.ActivityMainBinding
import com.lollipop.wear.ps.engine.sprite.SpritePlayer
import com.lollipop.wear.ps.engine.sprite.SpriteToward
import com.lollipop.wear.ps.engine.state.GameOption

class MainActivity : AppCompatActivity(), OptionListPageFragment.Callback {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val dashboardDelegate by lazy {
        MainDashboardDelegate(this, binding.dashboardPanel)
    }

    private val contentPanelController by lazy {
        AnimationHelper()
    }

    private val pageList = listOf(
//        ContentPage.State,
        ContentPage.Foods
    )

    private val foodsList = ArrayList<GameOption>()

    private val pageAdapter by lazy {
        PageAdapter(this, pageList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        dashboardDelegate.onCreate()
        initContentPanelController()
        binding.contentViewPager.adapter = pageAdapter
//        binding.spritePlayer.setSpriteInfo(
//            SpriteInfo.createBy4x4(256) { left, up, right, down ->
//                SpriteInfo.FromAssets(
//                    "sprite/PIKACHU.png",
//                    up = up,
//                    down = down,
//                    left = left,
//                    right = right
//                )
//            }
//        )

    }

    private fun initContentPanelController() {
        contentPanelController.build {
            withView(binding.contentPanel) {
                HideOnClose()
                ShowOnStart()
                TranslationX()
            }
            withView(binding.contentPanelBackground) {
                HideOnClose()
                ShowOnStart()
                Alpha()
            }
            withView(binding.contentPanelBackgroundRing) {
                HideOnClose()
                ShowOnStart()
                Alpha()
            }
        }
        binding.contentPanel.isInvisible = true
        binding.contentPanelBackground.isInvisible = true
        binding.contentPanelBackgroundRing.isInvisible = true
        binding.contentPanelBackgroundRing.setOnClickListener {
            contentPanelController.close()
        }
        binding.backpackButton.setOnClickListener {
            contentPanelController.expand()
        }
    }

    private fun updateToward(player: SpritePlayer, toward: SpriteToward) {
        if (player.spriteToward == toward) {
            player.changedToward(toward, !player.isRunning)
        } else {
            player.changedToward(toward, true)
        }
    }

    override fun getOptionList(pageId: String): List<GameOption> {
        when (pageId) {
            ContentPage.State.pageId -> {
                return emptyList()
            }

            ContentPage.Foods.pageId -> {
                if (foodsList.isEmpty()) {
                    foodsList.addAll(Foods.options)
                }
                return foodsList
            }
        }
        return emptyList()
    }

    override fun onOptionClick(option: GameOption) {
        // TODO
    }

    private class PageAdapter(activity: AppCompatActivity, val list: List<ContentPage>) :
        FragmentStateAdapter(activity) {
        override fun getItemCount(): Int {
            return list.size
        }

        override fun createFragment(position: Int): Fragment {
            val page = list[position]
            val fragment = page.fragment.getDeclaredConstructor().newInstance()
            ContentPageFragment.putPageId(fragment, page.pageId)
            return fragment
        }

    }

    private sealed class ContentPage(
        val fragment: Class<out ContentPageFragment>,
        val pageId: String
    ) {

        data object State : ContentPage(StatePageFragment::class.java, "State")
        data object Foods : ContentPage(ListContentPageFragment::class.java, "Foods")

    }


}