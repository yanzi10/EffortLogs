package com.nguyenbaolong.myapplication.feature.reminder

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.nguyenbaolong.myapplication.feature.settings.SettingsManager
import java.util.Calendar

object ReminderScheduler {

    const val CHANNEL_ID = "effort_log_reminders"
    const val EXTRA_TITLE = "extra_reminder_title"
    const val EXTRA_MESSAGE = "extra_reminder_message"

    const val REQUEST_CODE_TASK = 1001
    const val REQUEST_CODE_HABIT = 1002
    const val REQUEST_CODE_JOURNAL = 1003

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Effort Log Reminders"
            val description = "Daily notifications for tasks, habits, and journals"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                this.description = description
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun scheduleAll(context: Context, settingsManager: SettingsManager) {
        createNotificationChannel(context)
        val s = settingsManager.settings.value

        if (s.taskReminderEnabled) {
            scheduleDailyAlarm(context, REQUEST_CODE_TASK, s.taskReminderHour, 0, "Daily Tasks Checklist", "Check your pending tasks for today!")
        } else {
            cancelAlarm(context, REQUEST_CODE_TASK)
        }

        if (s.habitReminderEnabled) {
            scheduleDailyAlarm(context, REQUEST_CODE_HABIT, s.habitReminderHour, 0, "Habit Tracking Reminder", "Keep your streaks alive! Have you completed your habits?")
        } else {
            cancelAlarm(context, REQUEST_CODE_HABIT)
        }

        if (s.journalReminderEnabled) {
            scheduleDailyAlarm(context, REQUEST_CODE_JOURNAL, s.journalReminderHour, 0, "Daily Reflection & Journal", "Take a minute to log your accomplishments today.")
        } else {
            cancelAlarm(context, REQUEST_CODE_JOURNAL)
        }
    }

    private fun scheduleDailyAlarm(
        context: Context,
        requestCode: Int,
        hour: Int,
        minute: Int,
        title: String,
        message: String
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_MESSAGE, message)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    private fun cancelAlarm(context: Context, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}

