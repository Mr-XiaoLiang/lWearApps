package com.lollipop.wear.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

abstract class DatabaseHelper(
    context: Context,
    name: String,
    version: Int,
    factory: SQLiteDatabase.CursorFactory? = null
) : SQLiteOpenHelper(context, name, factory, version) {

    companion object {
        val COLUMN_ID = IntColumn(BaseColumns._ID, 0)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    protected fun createTable(
        db: SQLiteDatabase,
        tableName: String,
        primaryKey: Column<Int>? = COLUMN_ID,
        columns: Array<Column<*>>
    ) {
        val createTableSql = StringBuilder("CREATE TABLE IF NOT EXISTS $tableName ( ")
        var isFirst = true
        if (primaryKey != null) {
            isFirst = false
            createTableSql.append(" ${primaryKey.name} ${ColumnsType.INTEGER.key} PRIMARY KEY AUTOINCREMENT ")
        }
        for (column in columns) {
            if (isFirst) {
                isFirst = false
            } else {
                createTableSql.append(" , ")
            }
            createTableSql.append(" ${column.name} ${column.type.key} ")
        }

        createTableSql.append(" )")
        db.execSQL(createTableSql.toString())
    }

    protected fun deleteTable(tableName: String) {
        writableDatabase.execSQL("DROP TABLE IF EXISTS $tableName")
    }

    protected fun <T> select(
        tableName: String,
        columns: Array<Column<*>>,
        suffix: String = "",
        args: Array<String> = emptyArray(),
        mapCallback: (Cursor) -> T
    ): List<T> {
        // 只管检索和表信息，其他的让他们自己拼接吧
        val selectSql = StringBuilder("SELECT ")
        columns.joinTo(buffer = selectSql, separator = ", ") { it.name }
        selectSql.append(" FROM ").append(tableName).append(" ").append(suffix)
        val result = ArrayList<T>()
        readableDatabase.rawQuery(selectSql.toString(), args).use {
            while (it.moveToNext()) {
                result.add(mapCallback(it))
            }
        }
        return result
    }

    protected fun delete(
        tableName: String,
        where: String = "",
        args: Array<String> = emptyArray()
    ): Int {
        return writableDatabase.delete(tableName, where, args)
    }

    protected fun insert(tableName: String, columns: Array<ColumnValue<*>>): Long {
        val contentValues = ContentValues()
        columns.forEach { it.putTo(contentValues) }
        return writableDatabase.insert(tableName, null, contentValues)
    }

    class TextColumn(override val name: String, private val def: String) : Column<String> {
        override val type: ColumnsType = ColumnsType.TEXT
        override fun getValue(cursor: Cursor): String {
            val columnIndex = cursor.getColumnIndex(name)
            if (columnIndex < 0) {
                return def
            }
            return cursor.getString(columnIndex)
        }

        override fun valueOf(value: String): ColumnValue<String> {
            return TextColumnValue(this, value)
        }
    }

    class TextColumnValue(
        column: TextColumn, value: String
    ) : BasicColumnValue<String>(column, value) {
        override fun putTo(contentValues: ContentValues) {
            contentValues.put(name, value)
        }
    }

    class BooleanColumn(override val name: String, private val def: Boolean) : Column<Boolean> {
        override val type: ColumnsType = ColumnsType.INTEGER
        override fun getValue(cursor: Cursor): Boolean {
            val columnIndex = cursor.getColumnIndex(name)
            if (columnIndex < 0) {
                return def
            }
            return cursor.getInt(columnIndex) == 1
        }

        override fun valueOf(value: Boolean): ColumnValue<Boolean> {
            return BooleanColumnValue(this, value)
        }

    }

    class BooleanColumnValue(
        column: BooleanColumn, value: Boolean
    ) : BasicColumnValue<Boolean>(column, value) {
        override fun putTo(contentValues: ContentValues) {
            contentValues.put(
                name,
                if (value) {
                    1
                } else {
                    0
                }
            )
        }
    }

    class IntColumn(override val name: String, private val def: Int) : Column<Int> {
        override val type: ColumnsType = ColumnsType.INTEGER
        override fun getValue(cursor: Cursor): Int {
            val columnIndex = cursor.getColumnIndex(name)
            if (columnIndex < 0) {
                return def
            }
            return cursor.getInt(columnIndex)
        }

        override fun valueOf(value: Int): ColumnValue<Int> {
            return IntColumnValue(this, value)
        }

    }

    class IntColumnValue(
        column: IntColumn, value: Int
    ) : BasicColumnValue<Int>(column, value) {
        override fun putTo(contentValues: ContentValues) {
            contentValues.put(name, value)
        }
    }

    class LongColumn(override val name: String, private val def: Long) : Column<Long> {
        override val type: ColumnsType = ColumnsType.INTEGER
        override fun getValue(cursor: Cursor): Long {
            val columnIndex = cursor.getColumnIndex(name)
            if (columnIndex < 0) {
                return def
            }
            return cursor.getLong(columnIndex)
        }

        override fun valueOf(value: Long): ColumnValue<Long> {
            return LongColumnValue(this, value)
        }
    }

    class LongColumnValue(
        column: LongColumn, value: Long
    ) : BasicColumnValue<Long>(column, value) {
        override fun putTo(contentValues: ContentValues) {
            contentValues.put(name, value)
        }
    }

    class DoubleColumn(override val name: String, private val def: Double) : Column<Double> {
        override val type: ColumnsType = ColumnsType.DOUBLE
        override fun getValue(cursor: Cursor): Double {
            val columnIndex = cursor.getColumnIndex(name)
            if (columnIndex < 0) {
                return def
            }
            return cursor.getDouble(columnIndex)
        }

        override fun valueOf(value: Double): ColumnValue<Double> {
            return DoubleColumnValue(this, value)
        }
    }

    class DoubleColumnValue(
        column: DoubleColumn, value: Double
    ) : BasicColumnValue<Double>(column, value) {
        override fun putTo(contentValues: ContentValues) {
            contentValues.put(name, value)
        }
    }

    interface Column<T> {
        val name: String
        val type: ColumnsType
        fun getValue(cursor: Cursor): T
        fun valueOf(value: T): ColumnValue<T>
    }

    abstract class BasicColumnValue<T>(
        private val column: Column<T>, override val value: T
    ) : ColumnValue<T> {
        override val name: String = column.name
        override val type: ColumnsType = column.type
    }

    interface ColumnValue<T> {

        val name: String
        val type: ColumnsType
        val value: T

        fun putTo(contentValues: ContentValues)

    }

    enum class ColumnsType(val key: String) {
        TEXT("TEXT"), INTEGER("INTEGER"), DOUBLE("REAL")
    }

}