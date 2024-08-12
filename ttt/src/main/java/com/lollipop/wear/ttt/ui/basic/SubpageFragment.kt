package com.lollipop.wear.ttt.ui.basic

import android.content.Context
import androidx.fragment.app.Fragment

abstract class SubpageFragment : Fragment() {

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