package com.lollipop.wear.ttt.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import com.lollipop.wear.ttt.game.GamePiece
import kotlin.math.min

class PieceView(
    context: Context, attributeSet: AttributeSet? = null
) : ViewGroup(context, attributeSet), BoardView.Piece {

    companion object {
        var iconScale: Float = 0.7F
    }

    private var currentPiece: GamePiece = GamePiece.Empty

    private var pieceX: View? = null
    private var pieceO: View? = null
    private var pieceEmpty: View? = null

    override fun update(piece: GamePiece) {
        currentPiece = piece
        checkState()
    }

    fun setPieceX(view: View) {
        pieceX?.let {
            removeView(view)
        }
        addView(view)
        checkState()
    }

    fun setPieceO(view: View) {
        pieceO?.let {
            removeView(view)
        }
        addView(view)
        checkState()
    }

    fun setPieceEmpty(view: View) {
        pieceEmpty?.let {
            removeView(view)
        }
        addView(view)
        checkState()
    }

    override fun addView(child: View?) {
        child?.let {
            it.scaleX = iconScale
            it.scaleY = iconScale
        }
        super.addView(child)
    }

    private fun checkState() {
        pieceX?.isInvisible = currentPiece != GamePiece.X
        pieceO?.isInvisible = currentPiece != GamePiece.O
        pieceEmpty?.isInvisible = currentPiece != GamePiece.Empty
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val groupWidth = width - paddingLeft - paddingRight
        val groupHeight = height - paddingTop - paddingBottom
        val groupLeft = paddingLeft
        val groupTop = paddingTop
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val childWidth = min(child.measuredWidth, groupWidth)
            val childHeight = min(child.measuredHeight, groupHeight)
            val childLeft = ((groupWidth - childWidth) / 2) + groupLeft
            val childTop = ((groupHeight - childHeight) / 2) + groupTop
            child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight
        val height = MeasureSpec.getSize(heightMeasureSpec) - paddingTop - paddingBottom
        // 选小的那一个，内部限制为正方形
        val size = min(width, height)
        val childWidthSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY)
        val childHeightSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY)
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.measure(childWidthSpec, childHeightSpec)
        }
        setMeasuredDimension(width, height)
    }

}