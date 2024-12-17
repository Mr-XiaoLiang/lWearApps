package com.lollipop.wear.ps.engine.log

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.lollipop.wear.data.DatabaseHelper

class GameLogStore(context: Context) : DatabaseHelper(context, "game_log", 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db ?: return
        createTable(
            db,
            LogTable.TABLE_NAME,
            LogTable.id,
            arrayOf(LogTable.time, LogTable.who, LogTable.what, LogTable.option, LogTable.reason)
        )
    }

    fun addLog(who: String, what: String, option: String, reason: String) {
        val time = System.currentTimeMillis()
        insert(
            LogTable.TABLE_NAME,
            arrayOf(
                LogTable.who.valueOf(who),
                LogTable.what.valueOf(what),
                LogTable.option.valueOf(option),
                LogTable.time.valueOf(time),
                LogTable.reason.valueOf(reason)
            )
        )
    }

    fun queryLog(pageIndex: Int, pageSize: Int = 20): List<GameLog> {
        return select(
            tableName = LogTable.TABLE_NAME,
            columns = arrayOf(
                LogTable.id,
                LogTable.who,
                LogTable.what,
                LogTable.option,
                LogTable.time,
                LogTable.reason
            ),
            suffix = " ORDER BY ${LogTable.time.name} DESC " +
                    " LIMIT ? OFFSET ? ",
            args = arrayOf(pageSize.toString(), pageIndex.toString())
        ) {
            GameLog(
                id = LogTable.id.getValue(it),
                who = LogTable.who.getValue(it),
                what = LogTable.what.getValue(it),
                option = LogTable.option.getValue(it),
                time = LogTable.time.getValue(it),
                reason = LogTable.reason.getValue(it)
            )
        }
    }

    object LogTable {

        const val TABLE_NAME = "gameLog"

        val id = COLUMN_ID
        val time = LongColumn("log_time", 0)
        val who = TextColumn("log_who", "")
        val what = TextColumn("log_what", "")
        val option = TextColumn("log_option", "")
        val reason = TextColumn("log_reason", "")

    }

}