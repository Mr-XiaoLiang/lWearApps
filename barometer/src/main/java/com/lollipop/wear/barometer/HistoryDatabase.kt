package com.lollipop.wear.barometer

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.database.getFloatOrNull
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import java.util.concurrent.Executors

class HistoryDatabase(context: Context) : SQLiteOpenHelper(context, "history.db", null, 1) {

    companion object {
        private const val TABLE_NAME = "pressure"
    }

    private val asyncThread = Executors.newSingleThreadExecutor()
    private val mainThread = Handler(Looper.getMainLooper())

    override fun onCreate(db: SQLiteDatabase?) {
        val create = "CREATE TABLE $TABLE_NAME ( " +
                " ${Column.TIME.column} INTEGER, " +
                " ${Column.SENSOR_STATE.column} INTEGER, " +
                " ${Column.PRESSURE.column} REAL, " +
                " ${Column.ALTITUDE.column} REAL " +
                ")"
        db?.execSQL(create)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    fun insert(info: PressureInfo) {
        doAsync {
            writableDatabase?.insert(TABLE_NAME, "", info.toContentValues())
        }
    }

    fun select(maxTime: Long, count: Int, callback: (List<PressureInfo>) -> Unit) {
        val selectSql = """
            SELECT 
                ${Column.TIME.column}, 
                ${Column.SENSOR_STATE.column}, 
                ${Column.PRESSURE.column}, 
                ${Column.ALTITUDE.column} 
            FROM $TABLE_NAME 
            WHERE ${Column.TIME.column} < $maxTime 
            ORDER BY ${Column.TIME.column} DESC 
            LIMIT $count
        """.trimIndent()
        doAsync {
            val list = mutableListOf<PressureInfo>()
            try {
                readableDatabase?.rawQuery(selectSql, null)?.use { cursor ->
                    while (cursor.moveToNext()) {
                        list.add(
                            PressureInfo(
                                sensorState = Column.SENSOR_STATE.getValue(cursor),
                                pressure = Column.PRESSURE.getValue(cursor),
                                altitude = Column.ALTITUDE.getValue(cursor),
                                time = Column.TIME.getValue(cursor)
                            )
                        )
                    }
                }
            } catch (e: Throwable) {
                Log.e("HistoryDatabase", e.message, e)
            } finally {
                onUi {
                    callback(list)
                }
            }
        }
    }

    private fun doAsync(block: () -> Unit) {
        asyncThread.execute {
            try {
                block()
            } catch (e: Throwable) {
                Log.e("HistoryDatabase", e.message, e)
            }
        }
    }

    private fun onUi(block: () -> Unit) {
        mainThread.post {
            try {
                block()
            } catch (e: Throwable) {
                Log.e("HistoryDatabase", e.message, e)
            }
        }
    }

    private sealed class Column<T>(val column: String, val type: String) {

        fun getValue(cursor: Cursor): T {
            val columnIndex = cursor.getColumnIndex(column)
            if (columnIndex < 0) {
                return getDefaultValue()
            }
            return mapValue(cursor, columnIndex)
        }

        abstract fun getDefaultValue(): T

        protected abstract fun mapValue(cursor: Cursor, index: Int): T

        data object TIME : Column<Long>("time", "long") {
            override fun getDefaultValue(): Long {
                return 0L
            }

            override fun mapValue(cursor: Cursor, index: Int): Long {
                return cursor.getLongOrNull(index) ?: getDefaultValue()
            }
        }

        data object PRESSURE : Column<Float>("pressure", "float") {
            override fun getDefaultValue(): Float {
                return 0F
            }

            override fun mapValue(cursor: Cursor, index: Int): Float {
                return cursor.getFloatOrNull(index) ?: getDefaultValue()
            }

        }

        data object ALTITUDE : Column<Float>("altitude", "float") {
            override fun getDefaultValue(): Float {
                return 0F
            }

            override fun mapValue(cursor: Cursor, index: Int): Float {
                return cursor.getFloatOrNull(index) ?: getDefaultValue()
            }
        }

        data object SENSOR_STATE : Column<Int>("sensor_state", "int") {
            override fun getDefaultValue(): Int {
                return 0
            }

            override fun mapValue(cursor: Cursor, index: Int): Int {
                return cursor.getIntOrNull(index) ?: getDefaultValue()
            }
        }
    }

    private fun PressureInfo.toContentValues(): ContentValues {
        val contentValues = ContentValues()
        contentValues.put(Column.TIME.column, time)
        contentValues.put(Column.PRESSURE.column, pressure)
        contentValues.put(Column.ALTITUDE.column, altitude)
        contentValues.put(Column.SENSOR_STATE.column, sensorState)
        return contentValues
    }

}