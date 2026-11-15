package com.nguyenbaolong.myapplication.feature.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nguyenbaolong.myapplication.feature.settings.SettingsManager

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val settingsManager = SettingsManager(context)
            ReminderScheduler.scheduleAll(context, settingsManager)
        }
    }
}

