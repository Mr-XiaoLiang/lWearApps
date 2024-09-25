package com.lollipop.wear.ps.engine.world

import android.view.View

abstract class WorldAdapter {

    abstract fun createGaia(info: GaiaInfo): View

    abstract fun createEdifice(info: EdificeInfo): View

}