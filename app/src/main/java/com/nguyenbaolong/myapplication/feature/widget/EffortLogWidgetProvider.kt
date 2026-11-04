package com.nguyenbaolong.myapplication.feature.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.nguyenbaolong.myapplication.MainActivity
import com.nguyenbaolong.myapplication.R
import java.util.Calendar

class EffortLogWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        const val EXTRA_START_VOICE = "extra_start_voice_from_widget"

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_effort_log)

            val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val (title, message) = when (currentHour) {
                in 5..11 -> "🌅 Morning Plan" to "Review your checklist and tackle your morning goals."
                in 12..16 -> "☀️ Afternoon Focus" to "Keep your momentum going! Check off your tasks."
                in 17..21 -> "🌆 Evening Reflection" to "Time to log what you accomplished today."
                else -> "🌙 Night Rest" to "Rest well and recharge for a productive tomorrow."
            }

            views.setTextViewText(R.id.widget_period_title, title)
            views.setTextViewText(R.id.widget_message, message)

            // Open app directly into Quick Log / Voice
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(EXTRA_START_VOICE, true)
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                appWidgetId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_quick_log_btn, pendingIntent)
            views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}

