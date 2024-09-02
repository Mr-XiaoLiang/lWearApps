package com.lollipop.wear.ps.game.painter

import com.lollipop.wear.ps.game.GameCanvas

abstract class BasicGamePainter : GameCanvas.Painter {

    protected val delegate = GamePainterDelegate()

    override val isReady: Boolean
        get() {
            return delegate.isReady
        }

    override fun onSizeChanged(width: Int, height: Int) {
        val oldWidth = delegate.width
        val oldHeight = delegate.height
        delegate.onSizeChanged(width, height)
        onSizeChanged(oldWidth, oldHeight, width, height)
    }

    override fun onGroundEdgeChanged(groundTopEdge: Float, groundBottomEdge: Float) {
        val oldGroundTop = delegate.groundTopEdge
        val oldGroundBottom = delegate.groundBottomEdge
        delegate.onGroundEdgeChanged(groundTopEdge, groundBottomEdge)
        onPainterChanged(oldGroundTop, oldGroundBottom, groundTopEdge, groundBottomEdge)
    }

    protected abstract fun onSizeChanged(
        oldWidth: Int,
        oldHeight: Int,
        newWidth: Int,
        newHeight: Int
    )

    protected abstract fun onPainterChanged(
        oldGroundTop: Float,
        oldGroundBottom: Float,
        newGroundTop: Float,
        newGroundBottom: Float
    )

}