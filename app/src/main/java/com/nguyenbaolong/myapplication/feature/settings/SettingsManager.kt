package com.nguyenbaolong.myapplication.feature.settings

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

data class AppSettings(
    val themeMode: AppThemeMode = AppThemeMode.LIGHT,
    val startOfDayHour: Int = 5,
    val taskReminderEnabled: Boolean = true,
    val taskReminderHour: Int = 9,
    val habitReminderEnabled: Boolean = true,
    val habitReminderHour: Int = 12,
    val journalReminderEnabled: Boolean = true,
    val journalReminderHour: Int = 20
)

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("effort_log_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private fun loadSettings(): AppSettings {
        val themeStr = prefs.getString("theme_mode", AppThemeMode.SYSTEM.name) ?: AppThemeMode.SYSTEM.name
        val theme = try { AppThemeMode.valueOf(themeStr) } catch (_: Exception) { AppThemeMode.SYSTEM }
        return AppSettings(
            themeMode = theme,
            startOfDayHour = prefs.getInt("start_of_day_hour", 0),
            taskReminderEnabled = prefs.getBoolean("task_reminder_enabled", true),
            taskReminderHour = prefs.getInt("task_reminder_hour", 9),
            habitReminderEnabled = prefs.getBoolean("habit_reminder_enabled", true),
            habitReminderHour = prefs.getInt("habit_reminder_hour", 12),
            journalReminderEnabled = prefs.getBoolean("journal_reminder_enabled", true),
            journalReminderHour = prefs.getInt("journal_reminder_hour", 20)
        )
    }

    fun setThemeMode(mode: AppThemeMode) {
        prefs.edit().putString("theme_mode", mode.name).apply()
        _settings.value = _settings.value.copy(themeMode = mode)
    }

    fun setStartOfDayHour(hour: Int) {
        prefs.edit().putInt("start_of_day_hour", hour).apply()
        _settings.value = _settings.value.copy(startOfDayHour = hour)
    }

    fun setReminderEnabled(type: String, enabled: Boolean) {
        val key = when (type) {
            "task" -> "task_reminder_enabled"
            "habit" -> "habit_reminder_enabled"
            "journal" -> "journal_reminder_enabled"
            else -> return
        }
        prefs.edit().putBoolean(key, enabled).apply()
        _settings.value = loadSettings()
    }

    fun clearAllSettings() {
        prefs.edit().clear().apply()
        _settings.value = loadSettings()
    }
}

