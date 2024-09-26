package com.lollipop.wear.ps.engine.world

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

/**
 * 世界管理器
 * 它是静态的世界，精灵的活动，需要另外管理器
 */
class WorldGroup @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null
) : FrameLayout(context, attributeSet) {

    private var worldInfo: WorldInfo = WorldInfo.EMPTY

    private var worldAdapter: WorldAdapter? = null

    private var gaiaView: View? = null
    private val edificeViewList = mutableListOf<View>()

    fun bindWorld(worldInfo: WorldInfo) {
        this.worldInfo = worldInfo
        onWorldChanged()
    }

    fun bindAdapter(adapter: WorldAdapter) {
        worldAdapter = adapter
        onWorldChanged()
    }

    private fun onWorldChanged() {
        removeAllViews()
        gaiaView = null
        edificeViewList.clear()
        if (worldInfo == WorldInfo.EMPTY || worldInfo.gaia is EmptyGaia) {
            return
        }
        val adapter = worldAdapter ?: return
        val gaia = adapter.createGaia(worldInfo.gaia)
        addView(gaia)
        gaiaView = gaia

        for (edifice in worldInfo.edifice) {
            val view = adapter.createEdifice(edifice)
            addView(view)
            edificeViewList.add(view)
        }
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

}