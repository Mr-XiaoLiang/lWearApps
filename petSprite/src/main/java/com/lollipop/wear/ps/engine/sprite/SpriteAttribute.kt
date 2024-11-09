package com.lollipop.wear.ps.engine.sprite

import android.util.Log
import org.json.JSONObject
import kotlin.reflect.KProperty

object SpriteAttribute {

    private var attribute = JSONObject()

    fun create() {
        // 创建一个新的属性集合
    }

    fun parse(json: String) {
        attribute = try {
            JSONObject(json)
        } catch (e: Throwable) {
            Log.e("SpriteAttribute", "parse error", e)
            JSONObject()
        }
    }

    fun serialize(): String {
        return attribute.toString()
    }

    val name: String by attr("???")



    private fun get(key: String, def: Int): Int {
        return attribute.optInt(key, def)
    }

    private fun get(key: String, def: Boolean): Boolean {
        return attribute.optBoolean(key, def)
    }

    private fun get(key: String, def: Long): Long {
        return attribute.optLong(key, def)
    }

    private fun get(key: String, def: String): String {
        return attribute.optString(key, def)
    }

    private fun attr(def: String): StringDelegate {
        return StringDelegate(def)
    }

    private fun attr(def: Boolean): BooleanDelegate {
        return BooleanDelegate(def)
    }

    private fun attr(def: Long): LongDelegate {
        return LongDelegate(def)
    }

    private fun attr(def: Int): IntDelegate {
        return IntDelegate(def)
    }

    private class StringDelegate(val def: String) {
        operator fun getValue(spriteAttribute: SpriteAttribute, property: KProperty<*>): String {
            return spriteAttribute.get(property.name, def)
        }
    }

    private class LongDelegate(val def: Long) {
        operator fun getValue(spriteAttribute: SpriteAttribute, property: KProperty<*>): Long {
            return spriteAttribute.get(property.name, def)
        }
    }

    private class BooleanDelegate(val def: Boolean) {
        operator fun getValue(spriteAttribute: SpriteAttribute, property: KProperty<*>): Boolean {
            return spriteAttribute.get(property.name, def)
        }
    }

    private class IntDelegate(val def: Int) {
        operator fun getValue(spriteAttribute: SpriteAttribute, property: KProperty<*>): Int {
            return spriteAttribute.get(property.name, def)
        }
    }

}