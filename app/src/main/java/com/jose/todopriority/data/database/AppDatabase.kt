package com.jose.todopriority.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jose.todopriority.data.database.dao.TaskDao
import com.jose.todopriority.data.model.Task

@Database(entities = [Task::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        fun getInstance(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "db_task_priority_app"
            ).fallbackToDestructiveMigration().build()
        }
    }
}