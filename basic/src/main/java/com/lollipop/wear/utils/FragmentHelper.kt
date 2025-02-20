package com.lollipop.wear.utils

import android.os.Bundle
import androidx.fragment.app.Fragment

fun Fragment.updateArguments(
    block: Bundle.() -> Unit
) {
    val bundle = arguments
    if (bundle == null) {
        val newBundle = Bundle()
        newBundle.block()
        arguments = newBundle
    } else {
        bundle.block()
    }
}