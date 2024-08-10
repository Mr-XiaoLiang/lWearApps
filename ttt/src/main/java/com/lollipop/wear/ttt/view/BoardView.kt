package com.lollipop.wear.ttt.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import com.lollipop.wear.ttt.game.GameBoardProvider
import com.lollipop.wear.ttt.game.GameBoardSnapshot
import kotlin.math.min

class BoardView @JvmOverloads constructor(
    context: Context, attributeSet: AttributeSet? = null
) : ViewGroup(context, attributeSet) {

    private var currentSnapshot: GameBoardSnapshot? = null
    private var pieceProvider: PieceProvider? = null
    private var gameBoardProvider: GameBoardProvider? = null
    private var pieceClickListener: OnPieceClickListener? = null

    private val boardViewMap = BoardViewMap(
        viewProvider = ::createPieceView,
        onViewClick = ::onPieceViewClick
    )

    fun notifyBoardChanged() {
        currentSnapshot = null
        currentSnapshot = gameBoardProvider?.snapshot()
        updatePieces()
    }

    fun notifyPieceChanged() {
        val provider = pieceProvider
        if (provider == null || provider.themeChanged()) {
            removeAllViews()
            boardViewMap.clear()
        }
        if (provider == null) {
            return
        }
        boardViewMap.fill()
        // 更新棋盘内容
        updatePieces()
    }

    private fun createPieceView(): PieceView? {
        val view = pieceProvider?.createPieceView(context)
        // 要添加到屏幕上，才能显示
        addView(view)
        return view
    }

    private fun onPieceViewClick(view: View?) {
        view ?: return
        val point = boardViewMap.getIndex(view)
        if (point.x < 0 || point.y < 0) {
            return
        }
        onPieceClick(point.x, point.y)
    }

    private fun onPieceClick(x: Int, y: Int) {
        pieceClickListener?.onPieceClick(x, y)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val srcWidth = MeasureSpec.getSize(widthMeasureSpec)
        val srcHeight = MeasureSpec.getSize(heightMeasureSpec)
        val width = srcWidth - paddingLeft - paddingRight
        val height = srcHeight - paddingTop - paddingBottom
        val size = min(width, height) / 3
        val childWidthSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY)
        val childHeightSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY)
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.measure(childWidthSpec, childHeightSpec)
        }
        setMeasuredDimension(srcWidth, srcHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val groupWidth = width - paddingLeft - paddingRight
        val groupHeight = height - paddingTop - paddingBottom
        val childSize = min(groupWidth, groupHeight) / 3
        val groupLeft = paddingLeft + ((groupWidth - (childSize * 3)) / 2)
        val groupTop = paddingTop + ((groupHeight - (childSize * 3)) / 2)
        // 只排列棋子，其他的不管
        layoutChild(boardViewMap.leftTop, groupLeft, childSize, childSize, 0, 0)
        layoutChild(boardViewMap.leftCenter, groupLeft, groupTop, childSize, 0, 1)
        layoutChild(boardViewMap.leftBottom, groupLeft, groupTop, childSize, 0, 2)
        layoutChild(boardViewMap.middleTop, groupLeft, groupTop, childSize, 1, 0)
        layoutChild(boardViewMap.middleCenter, groupLeft, groupTop, childSize, 1, 1)
        layoutChild(boardViewMap.middleBottom, groupLeft, groupTop, childSize, 1, 2)
        layoutChild(boardViewMap.rightTop, groupLeft, groupTop, childSize, 2, 0)
        layoutChild(boardViewMap.rightCenter, groupLeft, groupTop, childSize, 2, 1)
        layoutChild(boardViewMap.rightBottom, groupLeft, groupTop, childSize, 2, 2)
    }

    private fun layoutChild(
        view: View?,
        groupLeft: Int,
        groupTop: Int,
        childSize: Int,
        gridX: Int,
        gridY: Int
    ) {
        view ?: return
        val childLeft = getChildOffset(groupLeft, childSize, gridX)
        val childTop = getChildOffset(groupTop, childSize, gridY)
        view.layout(childLeft, childTop, childLeft + childSize, childTop + childSize)
    }

    private fun getChildOffset(edge: Int, size: Int, grid: Int): Int {
        return size * grid + edge
    }

    private fun updatePieces() {
        val snapshot = currentSnapshot ?: GameBoardSnapshot.EMPTY
        boardViewMap.update(snapshot)
    }

    fun setGameBoardProvider(provider: GameBoardProvider) {
        gameBoardProvider = provider
    }

    fun setPieceProvider(provider: PieceProvider) {
        pieceProvider = provider
    }

    private class BoardViewMap(
        private val viewProvider: () -> PieceView?,
        private val onViewClick: (View?) -> Unit
    ) {

        companion object {
            val leftTopPoint = PiecePoint(0, 0)
            val leftCenterPoint = PiecePoint(0, 1)
            val leftBottomPoint = PiecePoint(0, 2)
            val middleTopPoint = PiecePoint(1, 0)
            val middleCenterPoint = PiecePoint(1, 1)
            val middleBottomPoint = PiecePoint(1, 2)
            val rightTopPoint = PiecePoint(2, 0)
            val rightCenterPoint = PiecePoint(2, 1)
            val rightBottomPoint = PiecePoint(2, 2)
            val nonePoint = PiecePoint(-1, -1)
        }

        var leftTop: PieceView? = null
            private set
        var leftCenter: PieceView? = null
            private set
        var leftBottom: PieceView? = null
            private set
        var middleTop: PieceView? = null
            private set
        var middleCenter: PieceView? = null
            private set
        var middleBottom: PieceView? = null
            private set
        var rightTop: PieceView? = null
            private set
        var rightCenter: PieceView? = null
            private set
        var rightBottom: PieceView? = null
            private set

        private val pieceViewClickListener = OnClickListener { v -> onViewClick(v) }

        fun get(x: Int, y: Int): PieceView? {
            return when (x) {
                0 -> when (y) {
                    0 -> leftTop
                    1 -> leftCenter
                    2 -> leftBottom
                    else -> null
                }

                1 -> when (y) {
                    0 -> middleTop
                    1 -> middleCenter
                    2 -> middleBottom
                    else -> null
                }

                2 -> when (y) {
                    0 -> rightTop
                    1 -> rightCenter
                    2 -> rightBottom
                    else -> null
                }

                else -> null
            }
        }

        fun getIndex(view: View): PiecePoint {
            return when (view) {
                leftTop -> leftTopPoint
                leftCenter -> leftCenterPoint
                leftBottom -> leftBottomPoint
                middleTop -> middleTopPoint
                middleCenter -> middleCenterPoint
                middleBottom -> middleBottomPoint
                rightTop -> rightTopPoint
                rightCenter -> rightCenterPoint
                rightBottom -> rightBottomPoint
                else -> nonePoint
            }
        }

        fun clear() {
            leftTop = null
            leftCenter = null
            leftBottom = null
            middleTop = null
            middleCenter = null
            middleBottom = null
            rightTop = null
            rightCenter = null
            rightBottom = null
        }

        fun fill() {
            if (leftTop == null) {
                leftTop = createView()
            }
            if (leftCenter == null) {
                leftCenter = createView()
            }
            if (leftBottom == null) {
                leftBottom = createView()
            }
            if (middleTop == null) {
                middleTop = createView()
            }
            if (middleCenter == null) {
                middleCenter = createView()
            }
            if (middleBottom == null) {
                middleBottom = createView()
            }
            if (rightTop == null) {
                rightTop = createView()
            }
            if (rightCenter == null) {
                rightCenter = createView()
            }
            if (rightBottom == null) {
                rightBottom = createView()
            }
        }

        private fun createView(): PieceView? {
            val pieceView = viewProvider()
            pieceView?.setOnClickListener(pieceViewClickListener)
            return pieceView
        }

        fun update(snapshot: GameBoardSnapshot) {
            leftTop?.update(snapshot.get(0, 0))
            leftCenter?.update(snapshot.get(0, 1))
            leftBottom?.update(snapshot.get(0, 2))
            middleTop?.update(snapshot.get(1, 0))
            middleCenter?.update(snapshot.get(1, 1))
            middleBottom?.update(snapshot.get(1, 2))
            rightTop?.update(snapshot.get(2, 0))
            rightCenter?.update(snapshot.get(2, 1))
            rightBottom?.update(snapshot.get(2, 2))
        }

    }

    class PiecePoint(
        val x: Int,
        val y: Int
    )

    interface PieceProvider {

        fun themeChanged(): Boolean

        fun createPieceView(context: Context): PieceView

    }

    fun interface OnPieceClickListener {
        fun onPieceClick(x: Int, y: Int)
    }

}