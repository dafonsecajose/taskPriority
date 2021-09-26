package com.jose.todopriority.application

import android.app.Application
import com.jose.todopriority.datasource.TaskDB

class TaskApplication: Application() {
    var taskDB: TaskDB? = null
        private set

    companion object {
        lateinit var instance: TaskApplication
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        taskDB = TaskDB(this)
    }
}