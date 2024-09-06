package com.lollipop.wear.basic

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

open class BasicFragment : Fragment() {

    protected inline fun <reified T> findCallback(c: Context?): T? {
        parentFragment?.let {
            if (it is T) {
                return it
            }
        }
        activity?.let {
            if (it is T) {
                return it
            }
        }
        context?.let {
            if (it is T) {
                return it
            }
        }
        if (c is T) {
            return c
        }
        return null
    }

}

inline fun <reified T : Fragment> FragmentManager.findTypedFragment(): T? {
    val fragment = fragments.find { it is T }
    if (fragment != null && fragment is T) {
        return fragment
    }
    return null
}

