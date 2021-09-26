package com.jose.todopriority.datasource

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.jose.todopriority.model.Task

class TaskDB(
    context: Context?
) : SQLiteOpenHelper(context, DB_NAME, null, CURRENT_VERSION) {

    companion object {
        private val DB_NAME = "task.db"
        private val CURRENT_VERSION = 1
    }

    val TABLE_NAME = "tasks"
    val COLUMNS_ID = "id"
    val COLUMNS_TITLE = "title"
    val COLUMNS_DATE = "date"
    val COLUMNS_HOUR = "hour"
    val COLUMNS_DESCRIPTION = "decription"
    val COLUMNS_PRORITY = "priority"
    val CREATE_TABLE = "CREATE TABLE $TABLE_NAME (" +
            "$COLUMNS_ID INTEGER NOT NULL, " +
            "$COLUMNS_TITLE TEXT NOT NULL, " +
            "$COLUMNS_DATE TEXT NOT NULL, " +
            "$COLUMNS_HOUR TEXT NOT NULL, " +
            "$COLUMNS_DESCRIPTION TEXT NOT NULL, " +
            "$COLUMNS_PRORITY TEXT NOT NULL," +
            "PRIMARY KEY ($COLUMNS_ID AUTOINCREMENT))"
    val DROP_TABLE = "DROP TABLE IF EXISTS $TABLE_NAME"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if(oldVersion != newVersion){
            db?.execSQL(DROP_TABLE)
        }
        onCreate(db)
    }

    fun saveTask(task: Task) {
        val db = writableDatabase ?: null

        val content = contentTask(task)
        db?.insert(TABLE_NAME, null, content)
        db?.close()
    }

    fun updateTask(task: Task) {
        val db = writableDatabase ?: null
        val content = contentTask(task)
        val where = "id = ?"
        val args = arrayOf("${task.id}")

        db?.update(TABLE_NAME, content, where, args)
        db?.close()
    }

    fun deleteTask(id: Int) {
        val db = writableDatabase ?: null
        val where = "id = ?"
        val args = arrayOf("$id")

        db?.delete(TABLE_NAME, where, args)
        db?.close()
    }

    @SuppressLint("Range")
    fun searchTasks(search : String, isSearchByID: Boolean = false) : List<Task> {
        val db = readableDatabase ?: return mutableListOf()
        val list = mutableListOf<Task>()
        var where: String? = null
        var args: Array<String> = arrayOf()

        if(isSearchByID){
            where = "$COLUMNS_ID = ?"
            args = arrayOf("$search")
        } else {
            where = "$COLUMNS_DATE = ?"
            args = arrayOf("$search")
        }

        if(search.isEmpty()){
            where = ""
            args = arrayOf()
        }

        var cursor = db.query(TABLE_NAME, null, where, args, null, null, null)
        if (cursor == null){
            db.close()
            return mutableListOf()
        }
        while (cursor.moveToNext()){
            var task = Task(
                cursor.getInt(cursor.getColumnIndex(COLUMNS_ID)),
                cursor.getString(cursor.getColumnIndex(COLUMNS_TITLE)),
                cursor.getString(cursor.getColumnIndex(COLUMNS_HOUR)),
                cursor.getString(cursor.getColumnIndex(COLUMNS_DATE)),
                cursor.getString(cursor.getColumnIndex(COLUMNS_DESCRIPTION)),
                cursor.getInt(cursor.getColumnIndex(COLUMNS_PRORITY)),
            )
            list.add(task)
        }
        db?.close()
        return list
    }

    private fun contentTask(task: Task) : ContentValues {
        val content = ContentValues()
        content.put(COLUMNS_TITLE, task.title)
        content.put(COLUMNS_DATE, task.date)
        content.put(COLUMNS_HOUR, task.hour)
        content.put(COLUMNS_DESCRIPTION, task.description)
        content.put(COLUMNS_PRORITY, task.priority)

        return content
    }
}