package com.lollipop.wear.ps.engine.state

import com.lollipop.wear.basic.doAsync
import com.lollipop.wear.ps.business.SpriteAssets
import com.lollipop.wear.ps.engine.sprite.SpriteInfo
import com.lollipop.wear.ps.utils.BasicDataManager
import org.json.JSONObject

object SpriteDataStore : BasicDataManager("PS_Sprite.lf") {

    private const val KEY_CURRENT_SPRITE = "currentSprite"

    var currentSprite: SpriteInfo = SpriteInfo.None
        private set

    var updateMode = 0
        private set

    override fun parseData(json: JSONObject) {
        super.parseData(json)
        doAsync {
            val value = json.optJSONObject(KEY_CURRENT_SPRITE)
            var info: SpriteInfo? = null
            var isChange = false
            if (value != null) {
                info = SpriteInfo.parse(value)
            }
            if (info == null || info == SpriteInfo.None) {
                isChange = true
                info = getDefaultSprite()
            }
            changeSprite(info)
            if (isChange) {
                save()
            }
        }
    }

    private fun changeSprite(spriteInfo: SpriteInfo) {
        currentSprite = spriteInfo
        updateMode++
    }

    override fun saveData(out: JSONObject) {
        super.saveData(out)
        out.put(KEY_CURRENT_SPRITE, currentSprite.toJson())
    }

    /**
     * 更新当前的精灵
     */
    fun updateSprite(spriteInfo: SpriteInfo) {
        changeSprite(spriteInfo)
        save()
    }

    private fun getDefaultSprite(): SpriteInfo {
        return SpriteAssets.createSprite("PIKACHU.png")
    }

}