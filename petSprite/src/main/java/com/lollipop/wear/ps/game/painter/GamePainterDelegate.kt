package com.lollipop.wear.ps.game.painter

class GamePainterDelegate {

    var isReady: Boolean = false
        private set
    var groundTopEdge = 0f
        private set
    var groundBottomEdge = 0f
        private set
    var width = 0
        private set
    var height = 0
        private set

    fun onSizeChanged(width: Int, height: Int) {
        isReady = false
        this.width = width
        this.height = height
    }

    fun onGroundEdgeChanged(groundTopEdge: Float, groundBottomEdge: Float) {
        isReady = false
        this.groundTopEdge = groundTopEdge
        this.groundBottomEdge = groundBottomEdge
    }

    fun onReady() {
        isReady = true
    }

}