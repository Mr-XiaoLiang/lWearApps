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

    var gridUnitSize = 10
        private set

    var currentGridUnitSize = 0
        private set

    var gaiaScale = 1F
        private set

    private var maxScrollX = 0
    private var maxScrollY = 0

    private var pendingBuildWorld = true

    fun setGridUnitSize(size: Int) {
        this.gridUnitSize = size
    }

    fun bindWorld(worldInfo: WorldInfo) {
        this.worldInfo = worldInfo
        onWorldChanged()
    }

    fun bindAdapter(adapter: WorldAdapter) {
        worldAdapter = adapter
        onWorldChanged()
    }

    private fun onWorldChanged() {
        pendingBuildWorld = true
        requestLayout()
    }

    private fun buildWorld() {
        if (!pendingBuildWorld) {
            return
        }
        if (isEmpty()) {
            return
        }
        val adapter = worldAdapter ?: return

        removeAllViews()
        edificeViewList.clear()
        gaiaView = null

        val gaia = adapter.createGaia(worldInfo.gaia)
        addView(gaia)
        gaiaView = gaia

        for (edifice in worldInfo.edifice) {
            val view = adapter.createEdifice(edifice)
            addView(view)
            edificeViewList.add(view)
        }
        requestLayout()
        pendingBuildWorld = false
    }

    private fun isEmpty(): Boolean {
        if (worldInfo == WorldInfo.EMPTY) {
            return true
        }
        if (worldInfo.gaia is EmptyGaia) {
            return true
        }
        if (worldInfo.gaia.height < 1 || worldInfo.gaia.width < 1) {
            return true
        }
        return false
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (isEmpty()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        if (pendingBuildWorld) {
            buildWorld()
            return
        }
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(widthSize, heightSize)
        updateGridUnitSize(widthSize, heightSize)
        gaiaView?.let { view ->
            val gaia = worldInfo.gaia
            val gaiaViewHeight = gaia.height * currentGridUnitSize
            val gaiaViewWidth = gaia.width * currentGridUnitSize
            view.measure(
                MeasureSpec.makeMeasureSpec(gaiaViewWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(gaiaViewHeight, MeasureSpec.EXACTLY)
            )
        }
        for (viewIndex in edificeViewList.indices) {
            val view = edificeViewList[viewIndex]
            val edifice = worldInfo.edifice[viewIndex]
            val edificeViewHeight = edifice.height * currentGridUnitSize
            val edificeViewWidth = edifice.width * currentGridUnitSize
            view.measure(
                MeasureSpec.makeMeasureSpec(edificeViewWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(edificeViewHeight, MeasureSpec.EXACTLY)
            )
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (isEmpty()) {
            super.onLayout(changed, left, top, right, bottom)
        }
        if (pendingBuildWorld) {
            buildWorld()
            return
        }

        val viewHeight = height
        val viewWidth = width
        updateGridUnitSize(viewWidth, viewHeight)

        gaiaView?.let { view ->
            val gaia = worldInfo.gaia
            val gaiaViewHeight = gaia.height * currentGridUnitSize
            val gaiaViewWidth = gaia.width * currentGridUnitSize
            view.layout(0, 0, gaiaViewWidth, gaiaViewHeight)
        }
        for (viewIndex in edificeViewList.indices) {
            val view = edificeViewList[viewIndex]
            val edifice = worldInfo.edifice[viewIndex]
            val edificeLeft = edifice.x * currentGridUnitSize
            val edificeTop = edifice.y * currentGridUnitSize
            val edificeHeight = edifice.height * currentGridUnitSize
            val edificeWidth = edifice.width * currentGridUnitSize
            view.layout(
                edificeLeft,
                edificeTop,
                edificeLeft + edificeWidth,
                edificeTop + edificeHeight
            )
        }
    }

    override fun scrollTo(x: Int, y: Int) {
        super.scrollTo(x.limit(0, maxScrollX), y.limit(0, maxScrollY))
    }

    /**
     * 更新单元格的大小，单位为像素
     */
    private fun updateGridUnitSize(w: Int, h: Int) {
        if (isEmpty()) {
            currentGridUnitSize = 0
            return
        }
        val gaia = worldInfo.gaia
        if (gaia.fixMode) {
            val gaiaResSize = gaia.getResourceSize(context)
            val gaiaGridXCount = gaia.width
            val gaiaGridYCount = gaia.height
            val gaiaWidth = gaiaResSize.width.toFloat()
            val gaiaHeight = gaiaResSize.height.toFloat()
            if (gaiaWidth > 0 && gaiaHeight > 0) {
                val gaiaRatio = gaiaWidth / gaiaHeight
                val viewRatio = w.toFloat() / h
                if (gaiaRatio > viewRatio) {
                    // 如果图片的宽高比，大于了屏幕的宽高比，那么说明图片比屏幕更宽，
                    // 那么我们应该按照高度来填充，保证充满，然后滑动内容
                    // 所以格子数量是以高度为基准的
                    currentGridUnitSize = (h.toFloat() / gaiaGridYCount).toInt()
                    gaiaScale = h.toFloat() / gaiaHeight
                } else {
                    currentGridUnitSize = (w.toFloat() / gaiaGridXCount).toInt()
                    gaiaScale = w.toFloat() / gaiaWidth
                }
            } else {
                currentGridUnitSize = 0
                gaiaScale = 1F
            }
        } else {
            val gaiaResSize = gaia.getResourceSize(context)
            currentGridUnitSize = gridUnitSize
            gaiaScale = currentGridUnitSize * gaia.height / gaiaResSize.height.toFloat()
        }
        limitOffset(w, h)
    }

    private fun limitOffset(viewWidth: Int, viewHeight: Int) {
        if (isEmpty()) {
            maxScrollX = 0
            maxScrollY = 0
            return
        }
        val gaiaHeight = worldInfo.gaia.height * currentGridUnitSize
        val gaiaWidth = worldInfo.gaia.width * currentGridUnitSize
        maxScrollX = gaiaWidth - viewWidth
        maxScrollY = gaiaHeight - viewHeight
    }

    private fun Int.limit(min: Int, max: Int): Int {
        if (this < min) {
            return min
        }
        if (this > max) {
            return max
        }
        return this
    }

}