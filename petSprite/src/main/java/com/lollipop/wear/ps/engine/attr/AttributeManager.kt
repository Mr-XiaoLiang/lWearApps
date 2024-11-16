package com.lollipop.wear.ps.engine.attr

import com.lollipop.wear.ps.utils.BasicDataManager

object AttributeManager : BasicDataManager("PS_Attribute.lf") {

    fun put(attr: AttributeBoolean, value: Boolean) {
        put(attr.name, value)
    }

    fun put(attr: AttributeInt, value: Int) {
        put(attr.name, value)
    }

    fun put(attr: AttributeString, value: String) {
        put(attr.name, value)
    }

    fun get(attr: AttributeBoolean, def: Boolean): Boolean {
        return getBoolean(attr.name, def)
    }

    fun get(attr: AttributeInt, def: Int): Int {
        return getInt(attr.name, def)
    }

    fun get(attr: AttributeString, def: String): String {
        return getString(attr.name, def)
    }

}