package com.lollipop.wear.data

class DataStore<K, V> {

    private val dataMap = HashMap<K, V>()
    private val dataList = ArrayList<V>()

    val size: Int
        get() {
            return dataList.size
        }

    fun put(key: K, value: V) {
        dataMap[key]?.let { old ->
            dataList.remove(old)
        }
        dataMap[key] = value
        dataList.add(value)
    }

    fun get(key: K): V? {
        return dataMap[key]
    }

    fun forEach(action: (V) -> Unit) {
        dataList.forEach(action)
    }

    fun clear() {
        dataMap.clear()
        dataList.clear()
    }

}