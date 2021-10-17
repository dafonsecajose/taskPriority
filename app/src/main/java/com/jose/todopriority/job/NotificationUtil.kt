package com.jose.todopriority.job

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.jose.todopriority.R
import com.jose.todopriority.ui.AddTaskActivity
import com.jose.todopriority.ui.AddTaskActivity.Companion.TASK

object NotificationUtil {

    private const val PRIMARY_CHANNEL_ID = "primary_channel_id"

    fun sendNotification(context: Context, taskTitle: String, taskId: Long) {
        val notificationManager = context
            .getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(context, AddTaskActivity::class.java)
        intent.putExtra(TASK, taskId)
        val pendingIntent = PendingIntent.getActivity( context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        createChannel(notificationManager, context)
        val notificationBuilder: NotificationCompat.Builder =
            getNotificationBuilder(context, taskTitle, pendingIntent)

        notificationManager.notify(taskId.toInt(), notificationBuilder.build())
    }

    private fun getNotificationBuilder(context: Context, taskTitle: String, pendingIntent: PendingIntent):
            NotificationCompat.Builder {

        return NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_schedule)
            .setContentTitle(taskTitle)
            .setAutoCancel(true)
            .setContentText(context.getText(R.string.notification_text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setContentIntent(pendingIntent)
    }

    private fun createChannel(notificationManager: NotificationManager, context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                PRIMARY_CHANNEL_ID,
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.BLUE
            notificationChannel.enableVibration(true)
            notificationChannel.description =
                context.getString(R.string.notification_channel_description)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    fun deleteNotification(context: Context, id: Int) {
        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(id)
    }

}