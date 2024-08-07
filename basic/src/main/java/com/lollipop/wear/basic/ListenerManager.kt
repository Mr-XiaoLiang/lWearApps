package com.lollipop.wear.basic

import java.lang.ref.WeakReference

class ListenerManager<T> {

    private val listeners = mutableListOf<WeakReference<T>>()

    fun add(listener: T) {
        listeners.add(WeakReference(listener))
    }

    fun remove(listener: T) {
        listeners.removeAll { it.get() == listener || it.get() == null }
    }

    fun invoke(block: (T) -> Unit) {
        for (weakReference in listeners) {
            try {
                weakReference.get()?.let { block(it) }
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    fun clear() {
        listeners.clear()
    }

}