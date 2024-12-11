package com.lollipop.wear.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.reflect.KProperty

abstract class PreferencesDelegate(val preferences: SharedPreferences) {

    companion object {

        fun getPreferences(context: Context, name: String): SharedPreferences {
            return context.getSharedPreferences(name, Context.MODE_PRIVATE)
        }

        fun getPreferences(context: Activity): SharedPreferences {
            return context.getPreferences(Context.MODE_PRIVATE)
        }

    }

    protected fun bool(def: Boolean = false) = BooleanDelegate(def)
    protected fun int(def: Int = 0) = IntDelegate(def)
    protected fun long(def: Long = 0) = LongDelegate(def)
    protected fun string(def: String = "") = StringDelegate(def)
    protected fun float(def: Float = 0F) = FloatDelegate(def)

    protected class BooleanDelegate(private val def: Boolean) {
        operator fun getValue(
            delegate: PreferencesDelegate,
            property: KProperty<*>
        ): Boolean {
            return delegate.preferences.getBoolean(property.name, def)
        }

        operator fun setValue(
            delegate: PreferencesDelegate,
            property: KProperty<*>,
            b: Boolean
        ) {
            delegate.preferences.edit {
                putBoolean(property.name, b)
            }
        }

    }

    protected class IntDelegate(private val def: Int = 0) {
        operator fun getValue(
            delegate: PreferencesDelegate,
            property: KProperty<*>
        ): Int {
            return delegate.preferences.getInt(property.name, def)
        }

        operator fun setValue(
            delegate: PreferencesDelegate,
            property: KProperty<*>,
            b: Int
        ) {
            delegate.preferences.edit {
                putInt(property.name, b)
            }
        }

    }

    protected class LongDelegate(private val def: Long = 0) {
        operator fun getValue(
            delegate: PreferencesDelegate,
            property: KProperty<*>
        ): Long {
            return delegate.preferences.getLong(property.name, def)
        }

        operator fun setValue(
            delegate: PreferencesDelegate,
            property: KProperty<*>,
            b: Long
        ) {
            delegate.preferences.edit {
                putLong(property.name, b)
            }
        }

    }

    protected class StringDelegate(private val def: String = "") {
        operator fun getValue(
            delegate: PreferencesDelegate,
            property: KProperty<*>
        ): String {
            return delegate.preferences.getString(property.name, def) ?: def
        }

        operator fun setValue(
            delegate: PreferencesDelegate,
            property: KProperty<*>,
            b: String
        ) {
            delegate.preferences.edit {
                putString(property.name, b)
            }
        }

    }

    protected class FloatDelegate(private val def: Float = 0F) {
        operator fun getValue(
            delegate: PreferencesDelegate,
            property: KProperty<*>
        ): Float {
            return delegate.preferences.getFloat(property.name, def)
        }

        operator fun setValue(
            delegate: PreferencesDelegate,
            property: KProperty<*>,
            b: Float
        ) {
            delegate.preferences.edit {
                putFloat(property.name, b)
            }
        }

    }

}