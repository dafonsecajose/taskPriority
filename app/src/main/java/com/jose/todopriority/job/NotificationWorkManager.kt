package com.jose.todopriority.job

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.jose.todopriority.ui.AddTaskActivity

class NotificationWorkManager(context: Context, parameters: WorkerParameters):
    Worker(context, parameters) {

    override fun doWork(): Result {
        val name = inputData.getString(AddTaskActivity.EXTRA_TASK_TITLE)
        val id = inputData.getLong(AddTaskActivity.EXTRA_TASK_ID, 0)
        return name?.let {
            NotificationUtil.sendNotification(applicationContext, it, id)
            Result.success()
        } ?: Result.failure()
    }
}