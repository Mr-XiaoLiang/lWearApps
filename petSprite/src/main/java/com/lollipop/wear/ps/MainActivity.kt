package com.lollipop.wear.ps

import android.os.Bundle
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
import com.lollipop.wear.devices.TimeViewDelegate
import com.lollipop.wear.ps.business.MainDashboardDelegate
import com.lollipop.wear.ps.business.options.Foods
import com.lollipop.wear.ps.business.page.ContentPageFragment
import com.lollipop.wear.ps.business.page.OptionListPageFragment
import com.lollipop.wear.ps.business.page.StatePageFragment
import com.lollipop.wear.ps.databinding.ActivityMainBinding
import com.lollipop.wear.ps.engine.sprite.SpritePlayer
import com.lollipop.wear.ps.engine.sprite.SpriteToward
import com.lollipop.wear.ps.engine.state.GameOption
import com.lollipop.wear.ps.engine.state.GameOptionAction
import com.lollipop.wear.ps.engine.state.GameStateManager
import com.lollipop.wear.ps.engine.state.type.Food

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
        ContentPage.Asian,
        ContentPage.Fruit,
        ContentPage.Vegetable,
        ContentPage.Cooked,
        ContentPage.Seafood,
        ContentPage.Dessert,
        ContentPage.Drink
    )

    private val itemListMap = HashMap<ContentPage, ArrayList<GameOption>>()

    private val pageAdapter by lazy {
        PageAdapter(this, pageList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        dashboardDelegate.onCreate()
        initContentPanelController()
        binding.contentViewPager.adapter = pageAdapter
        binding.contentPageIndicator.bind(binding.contentViewPager)
//        binding.contentPageIndicator.indicatorCount = pageList.size
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

            ContentPage.Fruit.pageId -> {
                return getOptionList(ContentPage.Fruit) {
                    Foods.Fruit.options
                }
            }

            ContentPage.Vegetable.pageId -> {
                return getOptionList(ContentPage.Vegetable) {
                    Foods.Vegetable.options
                }
            }

            ContentPage.Cooked.pageId -> {
                return getOptionList(ContentPage.Cooked) {
                    Foods.Cooked.options
                }
            }

            ContentPage.Asian.pageId -> {
                return getOptionList(ContentPage.Asian) {
                    Foods.Asian.options
                }
            }

            ContentPage.Seafood.pageId -> {
                return getOptionList(ContentPage.Seafood) {
                    Foods.Seafood.options
                }
            }

            ContentPage.Dessert.pageId -> {
                return getOptionList(ContentPage.Dessert) {
                    Foods.Dessert.options
                }
            }

            ContentPage.Drink.pageId -> {
                return getOptionList(ContentPage.Drink) {
                    Foods.Drink.options
                }
            }
        }
        return emptyList()
    }

    private fun getOptionList(
        page: ContentPage,
        create: () -> Array<GameOption>
    ): List<GameOption> {
        val itemList = itemListMap[page] ?: ArrayList()
        if (itemList.isEmpty()) {
            itemList.addAll(create())
            itemListMap[page] = itemList
        }
        return itemList
    }

    override fun onOptionClick(option: GameOption) {
        if (option is Food) {
            GameStateManager.onOption(GameOptionAction.ATE, option)
        } else {
            GameStateManager.onOption(GameOptionAction.USED, option)
        }
        GameStateManager.save(this)
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
        data object Fruit : ContentPage(OptionListPageFragment::class.java, "Fruit")
        data object Vegetable : ContentPage(OptionListPageFragment::class.java, "Vegetable")
        data object Cooked : ContentPage(OptionListPageFragment::class.java, "Cooked")
        data object Asian : ContentPage(OptionListPageFragment::class.java, "Asian")
        data object Seafood : ContentPage(OptionListPageFragment::class.java, "Seafood")
        data object Dessert : ContentPage(OptionListPageFragment::class.java, "Dessert")
        data object Drink : ContentPage(OptionListPageFragment::class.java, "Drink")

    }


}