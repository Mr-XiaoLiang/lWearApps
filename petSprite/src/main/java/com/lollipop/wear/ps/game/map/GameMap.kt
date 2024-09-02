package com.lollipop.wear.ps.game.map

class GameMap(
    val mapResource: Int,
    private val anchor: MutableMap<MapAnchor, Float> = mutableMapOf()
) {

    fun getAnchor(anchor: MapAnchor): Float? {
        return this.anchor[anchor]
    }

    fun setAnchor(anchor: MapAnchor, value: Float) {
        this.anchor[anchor] = value
    }

}