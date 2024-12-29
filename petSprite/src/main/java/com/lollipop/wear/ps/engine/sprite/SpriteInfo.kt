package com.lollipop.wear.ps.engine.sprite

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.lollipop.wear.ps.engine.sprite.SpriteFrame.Line
import org.json.JSONObject
import java.io.File

sealed class SpriteInfo(
    private val type: String,
    val left: SpriteFrame,
    val up: SpriteFrame,
    val right: SpriteFrame,
    val down: SpriteFrame
) {

    var name = ""

    abstract fun loadBitmap(context: Context): Bitmap?

    protected abstract fun toContentJson(): String

    protected abstract fun isContentSame(other: SpriteInfo): Boolean

    fun toJson(): JSONObject {
        val json = JSONObject()
        json.put(KEY_LEFT_FRAME, left.toJson())
        json.put(KEY_UP_FRAME, up.toJson())
        json.put(KEY_RIGHT_FRAME, right.toJson())
        json.put(KEY_DOWN_FRAME, down.toJson())
        json.put(KEY_TYPE, type)
        json.put(KEY_CONTENT, toContentJson())
        json.put(KEY_NAME, name)
        return json
    }

    fun getFrame(toward: SpriteToward): SpriteFrame {
        when (toward) {
            SpriteToward.Up -> {
                return up
            }

            SpriteToward.Down -> {
                return down
            }

            SpriteToward.Left -> {
                return left
            }

            SpriteToward.Right -> {
                return right
            }
        }
    }

    fun isSame(other: SpriteInfo): Boolean {
        if (this === other) {
            return true
        }
        if (javaClass != other.javaClass) {
            return false
        }
        if (this.left != other.left
            || this.right != other.right
            || this.up != other.up
            || this.down != other.down
        ) {
            return false
        }
        return isContentSame(other)
    }

    data object None : SpriteInfo(
        "",
        SpriteFrame.None,
        SpriteFrame.None,
        SpriteFrame.None,
        SpriteFrame.None
    ) {
        override fun loadBitmap(context: Context): Bitmap? {
            return null
        }

        override fun toContentJson(): String {
            return ""
        }

        override fun isContentSame(other: SpriteInfo): Boolean {
            return true
        }

    }

    class FromAssets(
        val path: String,
        left: SpriteFrame,
        up: SpriteFrame,
        right: SpriteFrame,
        down: SpriteFrame
    ) : SpriteInfo(TYPE_ASSETS, left, up, right, down) {

        companion object {
            fun parseContent(value: String): String {
                return value
            }
        }

        override fun loadBitmap(context: Context): Bitmap? {
            try {
                return BitmapCache.optAssets(context, path)
            } catch (e: Throwable) {
                Log.e("SpriteInfo", "FromAssets.loadBitmap", e)
            }
            return null
        }

        override fun toContentJson(): String {
            return path
        }

        override fun isContentSame(other: SpriteInfo): Boolean {
            if (other is FromAssets) {
                return other.path == this.path
            }
            return false
        }

    }

    class FromFile(
        private val file: File,
        left: SpriteFrame,
        up: SpriteFrame,
        right: SpriteFrame,
        down: SpriteFrame
    ) : SpriteInfo(TYPE_FILE, left, up, right, down) {

        companion object {
            fun parseContent(value: String): File {
                return File(value)
            }
        }

        override fun loadBitmap(context: Context): Bitmap? {
            try {
                return BitmapCache.optFile(file)
            } catch (e: Throwable) {
                Log.e("SpriteInfo", "FromFile.loadBitmap", e)
            }
            return null
        }

        override fun toContentJson(): String {
            return file.path
        }

        override fun isContentSame(other: SpriteInfo): Boolean {
            if (other is FromFile) {
                return other.file.path == this.file.path
            }
            return false
        }
    }

    class FromByteArray(
        private val bytes: ByteArray,
        left: SpriteFrame,
        up: SpriteFrame,
        right: SpriteFrame,
        down: SpriteFrame
    ) : SpriteInfo(TYPE_BYTE_ARRAY, left, up, right, down) {

        companion object {
            fun parseContent(value: String): ByteArray {
                return value.toByteArray(Charsets.UTF_8)
            }
        }

        override fun loadBitmap(context: Context): Bitmap? {
            try {
                return BitmapCache.optByteArray(bytes)
            } catch (e: Throwable) {
                Log.e("SpriteInfo", "FromByteArray.loadBitmap", e)
            }
            return null
        }

        override fun toContentJson(): String {
            return String(bytes, Charsets.UTF_8)
        }

        override fun isContentSame(other: SpriteInfo): Boolean {
            if (other is FromByteArray) {
                return other.bytes.contentEquals(this.bytes)
            }
            return false
        }
    }

    companion object {

        private const val KEY_LEFT_FRAME = "left_frame"
        private const val KEY_UP_FRAME = "up_frame"
        private const val KEY_RIGHT_FRAME = "right_frame"
        private const val KEY_DOWN_FRAME = "down_frame"
        private const val KEY_TYPE = "type"
        private const val KEY_CONTENT = "content"
        private const val KEY_NAME = "name"


        const val TYPE_ASSETS = "assets"
        const val TYPE_FILE = "file"
        const val TYPE_BYTE_ARRAY = "byteArray"

        fun parse(value: String): SpriteInfo {
            try {
                if (value.isEmpty()) {
                    return None
                }
                return parse(JSONObject(value))
            } catch (e: Throwable) {
                Log.e("SpriteInfo", "parse", e)
            }
            return None
        }

        fun parse(json: JSONObject): SpriteInfo {
            val type = json.optString(KEY_TYPE, "")
            val info = when (type) {
                TYPE_ASSETS -> {
                    FromAssets(
                        path = FromAssets.parseContent(json.optString(KEY_CONTENT, "")),
                        left = parseFrame(json, KEY_LEFT_FRAME),
                        up = parseFrame(json, KEY_UP_FRAME),
                        right = parseFrame(json, KEY_RIGHT_FRAME),
                        down = parseFrame(json, KEY_DOWN_FRAME)
                    )
                }

                TYPE_FILE -> {
                    FromFile(
                        file = FromFile.parseContent(json.optString(KEY_CONTENT, "")),
                        left = parseFrame(json, KEY_LEFT_FRAME),
                        up = parseFrame(json, KEY_UP_FRAME),
                        right = parseFrame(json, KEY_RIGHT_FRAME),
                        down = parseFrame(json, KEY_DOWN_FRAME)
                    )
                }

                TYPE_BYTE_ARRAY -> {
                    FromByteArray(
                        bytes = FromByteArray.parseContent(json.optString(KEY_CONTENT, "")),
                        left = parseFrame(json, KEY_LEFT_FRAME),
                        up = parseFrame(json, KEY_UP_FRAME),
                        right = parseFrame(json, KEY_RIGHT_FRAME),
                        down = parseFrame(json, KEY_DOWN_FRAME)
                    )
                }

                else -> {
                    return None
                }
            }
            info.name = json.optString(KEY_NAME, "")
            return info
        }

        private fun parseFrame(json: JSONObject, key: String): SpriteFrame {
            return SpriteFrame.parse(json.optJSONObject(key) ?: JSONObject())
        }

        inline fun <reified T : SpriteInfo> createBy4x4(
            imageWidth: Int,
            imageHeight: Int = imageWidth,
            paddingLeft: Int = 0,
            paddingTop: Int = 0,
            paddingRight: Int = 0,
            paddingBottom: Int = 0,
            result: (left: Line, up: Line, right: Line, down: Line) -> T
        ): T {
            val lines = SpriteFrame.createBy4x4(
                imageWidth,
                imageHeight,
                paddingLeft,
                paddingTop,
                paddingRight,
                paddingBottom
            )
            return result(lines[0], lines[1], lines[2], lines[3])
        }
    }

}

sealed class SpriteFrame {

    abstract fun toJson(): JSONObject

    data object None : SpriteFrame() {
        override fun toJson(): JSONObject {
            return JSONObject()
        }
    }

    class Line(
        val count: Int,
        val top: Int,
        val left: Int,
        val frameWidth: Int,
        val frameHeight: Int
    ) : SpriteFrame() {
        override fun toJson(): JSONObject {
            return JSONObject().apply {
                put(KEY_COUNT, count)
                put(KEY_TOP, top)
                put(KEY_LEFT, left)
                put(KEY_FRAME_WIDTH, frameWidth)
                put(KEY_FRAME_HEIGHT, frameHeight)
            }
        }

    }

    companion object {

        const val KEY_COUNT = "count"
        const val KEY_TOP = "top"
        const val KEY_LEFT = "left"
        const val KEY_FRAME_WIDTH = "frameWidth"
        const val KEY_FRAME_HEIGHT = "frameHeight"

        fun parse(json: JSONObject): SpriteFrame {
            val count = json.optInt(KEY_COUNT, 0)
            if (count > 0) {
                val frameWidth = json.optInt(KEY_FRAME_WIDTH, 0)
                val frameHeight = json.optInt(KEY_FRAME_HEIGHT, 0)
                if (frameWidth > 0 && frameHeight > 0) {
                    return Line(
                        count = count,
                        top = json.optInt(KEY_TOP, 0),
                        left = json.optInt(KEY_LEFT, 0),
                        frameWidth = frameWidth,
                        frameHeight = frameHeight
                    )
                }
            }
            return None
        }

        /**
         * @return [left, up, right, down]
         */
        fun createBy4x4(
            imageWidth: Int,
            imageHeight: Int = imageWidth,
            paddingLeft: Int = 0,
            paddingTop: Int = 0,
            paddingRight: Int = 0,
            paddingBottom: Int = 0,
        ): Array<Line> {
            // 图片一般都是，从上到下依次为：正面、左面、右边、背面
            // 因此顺序为：下，左，右，上
            val frameWidth = (imageWidth - paddingLeft - paddingRight) / 4
            val frameHeight = (imageHeight - paddingTop - paddingBottom) / 4
            var linTop = paddingTop
            val down = Line(
                count = 4,
                top = linTop,
                left = paddingLeft,
                frameWidth = frameWidth,
                frameHeight = frameHeight
            )
            linTop += frameHeight
            val left = Line(
                count = 4,
                top = linTop,
                left = paddingLeft,
                frameWidth = frameWidth,
                frameHeight = frameHeight
            )
            linTop += frameHeight
            val right = Line(
                count = 4,
                top = linTop,
                left = paddingLeft,
                frameWidth = frameWidth,
                frameHeight = frameHeight
            )
            linTop += frameHeight
            val up = Line(
                count = 4,
                top = linTop,
                left = paddingLeft,
                frameWidth = frameWidth,
                frameHeight = frameHeight
            )
            return arrayOf(left, up, right, down)
        }
    }

}
