package com.lollipop.file.sender.ftp.fts

import android.content.Context
import java.lang.ref.WeakReference

fun interface FTSContextProvider {

    fun getAppContext(): Context?

}

class FTSContextProviderWrapper(
    context: Context
) : FTSContextProvider {
    private val contextRef = WeakReference(context)
    override fun getAppContext(): Context? {
        return contextRef.get()
    }
}


