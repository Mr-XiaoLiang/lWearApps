package com.lollipop.wear.ps.business

import android.content.Context
import com.lollipop.wear.ps.engine.sprite.SpriteFrame
import com.lollipop.wear.ps.engine.sprite.SpriteInfo

object SpriteAssets {

    private const val DIRECTION_PATH = "sprite"
    private val spriteSourceList = ArrayList<SpriteInfo>()

    val spriteList: List<SpriteInfo>
        get() = spriteSourceList

    fun init(context: Context) {
        spriteSourceList.clear()
        val lines = SpriteFrame.createBy4x4(256)
        val left = lines[0]
        val up = lines[1]
        val right = lines[2]
        val down = lines[3]
        context.assets.list(DIRECTION_PATH)?.forEach { item ->
            val info = SpriteInfo.FromAssets(path = "$DIRECTION_PATH/$item", left, up, right, down)
            info.name = getAssetsFileName(item)
            spriteSourceList.add(info)
        }
    }

    private fun getAssetsFileName(name: String): String {
        val indexOf = name.lastIndexOf(".")
        return if (indexOf < 0) {
            name
        } else {
            name.substring(0, indexOf)
        }
    }

}