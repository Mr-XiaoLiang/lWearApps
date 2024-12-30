package com.lollipop.wear.ps.utils

import android.content.Context
import android.util.Log
import com.lollipop.wear.data.FileHelper
import com.lollipop.wear.data.PreferenceHelper
import org.json.JSONArray
import org.json.JSONObject

abstract class BasicDataManager(
    private val fileName: String
) {

    protected val dataObject = JSONObject()
    protected var dataController: PreferenceHelper.Controller? = null

    fun init(context: Context) {
        dataController = PreferenceHelper.from(context, fileName)
        dataController?.readJson { result ->
            when (result) {
                is FileHelper.FileResult.Success -> {
                    parseData(result.data)
                }

                is FileHelper.FileResult.Failure -> {
                    // 读取失败，重新创建一个
                    saveData(JSONObject())
                }
            }
        }
        onInit(context)
    }

    protected open fun onInit(context: Context) {

    }

    fun save() {
        onSave()
        dataController?.saveJson { saveData(it) }?.onResult {
            if (it is FileHelper.FileResult.Failure) {
                Log.e("BasicDataManager", "save", it.error)
            }
        }
    }

    protected open fun onSave() {

    }

    protected fun getString(key: String, default: String): String {
        return dataObject.optString(key, default)
    }

    protected fun getInt(key: String, default: Int): Int {
        return dataObject.optInt(key, default)
    }

    protected fun getLong(key: String, default: Long): Long {
        return dataObject.optLong(key, default)
    }

    protected fun getBoolean(key: String, default: Boolean): Boolean {
        return dataObject.optBoolean(key, default)
    }

    protected fun getJSONObject(key: String, default: JSONObject): JSONObject {
        return dataObject.optJSONObject(key) ?: default
    }

    protected fun getJSONArray(key: String, default: JSONArray): JSONArray {
        return dataObject.optJSONArray(key) ?: default
    }

    protected fun put(key: String, value: Any) {
        dataObject.put(key, value)
    }

    protected open fun parseData(json: JSONObject) {
        json.copyTo(dataObject)
    }

    protected open fun saveData(out: JSONObject) {
        dataObject.copyTo(out)
    }

    protected fun JSONObject.copyTo(target: JSONObject) {
        keys().forEach { key ->
            target.put(key, get(key))
        }
    }

    protected fun getIntFromGroup(groupKey: String, valueKey: String, def: Int): Int {
        return getValueFromGroup(groupKey) { it.optInt(valueKey, def) }
    }

    protected fun getStringFromGroup(groupKey: String, valueKey: String, def: String): String {
        return getValueFromGroup(groupKey) { it.optString(valueKey, def) }
    }

    protected fun getBooleanFromGroup(groupKey: String, valueKey: String, def: Boolean): Boolean {
        return getValueFromGroup(groupKey) { it.optBoolean(valueKey, def) }
    }

    protected fun getLongFromGroup(groupKey: String, valueKey: String, def: Long): Long {
        return getValueFromGroup(groupKey) { it.optLong(valueKey, def) }
    }

    protected fun getDoubleFromGroup(groupKey: String, valueKey: String, def: Double): Double {
        return getValueFromGroup(groupKey) { it.optDouble(valueKey, def) }
    }

    protected inline fun <reified T : Any> getValueFromGroup(
        groupKey: String,
        valueBuilder: (JSONObject) -> T
    ): T {
        val obj = dataObject.optJSONObject(groupKey)
        if (obj != null) {
            return valueBuilder(obj)
        }
        val newObj = JSONObject()
        dataObject.put(groupKey, newObj)
        return valueBuilder(newObj)
    }

    protected fun putValueToGroup(
        groupKey: String,
        fillGroup: (group: JSONObject, newGroup: Boolean) -> Unit
    ) {
        val obj = dataObject.optJSONObject(groupKey)
        if (obj != null) {
            fillGroup(obj, false)
            return
        }
        val newObj = JSONObject()
        dataObject.put(groupKey, newObj)
        fillGroup(newObj, true)
    }

}