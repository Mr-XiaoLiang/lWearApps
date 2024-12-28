package com.lollipop.wear.ps.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import com.lollipop.wear.ps.databinding.ActivitySpriteSelectBinding
import com.lollipop.wear.ps.engine.sprite.SpriteInfo
import org.json.JSONObject

class SpriteSelectActivity : AppCompatActivity() {

    companion object {

        const val RESULT_SPRITE_INFO = "result_sprite_info"

        private fun setResult(activity: SpriteSelectActivity, info: SpriteInfo) {
            val intent = Intent()
            intent.putExtra(RESULT_SPRITE_INFO, info.toJson().toString())
            activity.setResult(RESULT_OK, intent)
        }

    }

    private val binding by lazy {
        ActivitySpriteSelectBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    private class ItemHolder()

    class ResultContracts : ActivityResultContract<Unit, SpriteInfo>() {

        override fun createIntent(context: Context, input: Unit): Intent {
            return Intent(context, SpriteSelectActivity::class.java)
        }

        override fun parseResult(resultCode: Int, intent: Intent?): SpriteInfo {
            if (resultCode != RESULT_OK) {
                return SpriteInfo.None
            }
            intent ?: return SpriteInfo.None
            val json = intent.getStringExtra(RESULT_SPRITE_INFO)
            if (json == null || TextUtils.isEmpty(json)) {
                return SpriteInfo.None
            }
            try {
                return SpriteInfo.parse(JSONObject(json))
            } catch (_: Throwable) {
            }
            return SpriteInfo.None
        }

    }

}