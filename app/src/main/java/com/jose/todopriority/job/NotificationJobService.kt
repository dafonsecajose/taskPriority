package com.jose.todopriority.job

import android.app.job.JobParameters
import android.app.job.JobService
import android.util.Log
import com.jose.todopriority.ui.AddTaskActivity

class NotificationJobService: JobService() {
    override fun onStartJob(jobParameters: JobParameters?): Boolean {
        val taskTitle = jobParameters?.extras?.getString(AddTaskActivity.SCHEDULE_EXTRA_TASK_TITLE)
        Log.i("Notification", taskTitle ?: "Not")

        return false
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        TODO("Not yet implemented")
    }
}