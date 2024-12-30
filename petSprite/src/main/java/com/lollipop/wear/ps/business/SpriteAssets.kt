package com.lollipop.wear.ps.business

import android.content.Context
import com.lollipop.wear.ps.engine.sprite.SpriteFrame
import com.lollipop.wear.ps.engine.sprite.SpriteInfo
import kotlin.random.Random
import kotlin.random.nextInt

object SpriteAssets {

    private const val DIRECTION_PATH = "sprite"
    private val spriteSourceList = ArrayList<SpriteInfo>()

    private const val NAME_RANDOM = "000"

    val spriteList: List<SpriteInfo>
        get() = spriteSourceList

    fun getSpritePath(name: String): String {
        return "$DIRECTION_PATH/$name"
    }

    fun filterEmptySprite(info: SpriteInfo): SpriteInfo {
        if (info == SpriteInfo.None || info.name == NAME_RANDOM) {
            return spriteSourceList[Random.nextInt(1 until spriteSourceList.size)]
        }
        return info
    }

    fun createSprite(name: String): SpriteInfo {
        return SpriteInfo.createBy4x4(256) { left, up, right, down ->
            SpriteInfo.FromAssets(
                path = getSpritePath(name),
                left = left,
                up = up,
                right = right,
                down = down
            )
        }
    }

    fun init(context: Context) {
        spriteSourceList.clear()
        val lines = SpriteFrame.createBy4x4(256)
        val left = lines[0]
        val up = lines[1]
        val right = lines[2]
        val down = lines[3]
        context.assets.list(DIRECTION_PATH)?.forEach { item ->
            val info = SpriteInfo.FromAssets(path = getSpritePath(item), left, up, right, down)
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