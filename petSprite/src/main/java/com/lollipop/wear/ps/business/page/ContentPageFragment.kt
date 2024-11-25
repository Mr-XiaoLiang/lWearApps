package com.lollipop.wear.ps.business.page

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment

abstract class ContentPageFragment : Fragment() {

    companion object {
        private const val ARG_PAGE_ID = "page_id"

        fun putPageId(fragment: Fragment, pageId: String): Fragment {
            val arguments = fragment.arguments ?: Bundle()
            arguments.putString(ARG_PAGE_ID, pageId)
            fragment.arguments = arguments
            return fragment
        }

        fun getPageId(fragment: Fragment): String {
            return fragment.arguments?.getString(ARG_PAGE_ID) ?: ""
        }

    }

    protected fun getPageId(): String {
        return getPageId(this)
    }

    protected inline fun <reified T> findCallback(c: Context): T? {
        parentFragment?.let { parent ->
            if (parent is T) {
                return parent
            }
        }
        if (c is T) {
            return c
        }
        activity?.let {
            if (it is T) {
                return it
            }
        }
        return null
    }

}