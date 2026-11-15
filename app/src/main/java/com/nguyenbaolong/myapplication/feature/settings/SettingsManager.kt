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
    val journalReminderHour: Int = 20,
    val primaryGoal: String = "Master Daily Consistency",
    val companionName: String = "Effort Sprout",
    val companionXp: Int = 0,
    val hasSeenOnboarding: Boolean = false
)

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("effort_log_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private fun loadSettings(): AppSettings {
        val themeStr = prefs.getString("theme_mode", AppThemeMode.SYSTEM.name) ?: AppThemeMode.SYSTEM.name
        val theme = try { AppThemeMode.valueOf(themeStr) } catch (_: Exception) { AppThemeMode.SYSTEM }
        val startHour = prefs.getInt("start_of_day_hour", 5).coerceIn(0, 23)
        return AppSettings(
            themeMode = theme,
            startOfDayHour = startHour,
            taskReminderEnabled = prefs.getBoolean("task_reminder_enabled", true),
            taskReminderHour = prefs.getInt("task_reminder_hour", 9),
            habitReminderEnabled = prefs.getBoolean("habit_reminder_enabled", true),
            habitReminderHour = prefs.getInt("habit_reminder_hour", 12),
            journalReminderEnabled = prefs.getBoolean("journal_reminder_enabled", true),
            journalReminderHour = prefs.getInt("journal_reminder_hour", 20),
            primaryGoal = prefs.getString("primary_goal", "Master Daily Consistency") ?: "Master Daily Consistency",
            companionName = prefs.getString("companion_name", "Effort Sprout") ?: "Effort Sprout",
            companionXp = prefs.getInt("companion_xp", 0).coerceAtLeast(0),
            hasSeenOnboarding = prefs.getBoolean("has_seen_onboarding", false)
        )
    }

    fun setThemeMode(mode: AppThemeMode) {
        prefs.edit().putString("theme_mode", mode.name).apply()
        _settings.value = _settings.value.copy(themeMode = mode)
    }

    fun setStartOfDayHour(hour: Int) {
        val clamped = hour.coerceIn(0, 23)
        prefs.edit().putInt("start_of_day_hour", clamped).apply()
        _settings.value = _settings.value.copy(startOfDayHour = clamped)
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

    fun setPrimaryGoal(goal: String) {
        val trimmed = goal.trim().ifEmpty { "Master Daily Consistency" }
        prefs.edit().putString("primary_goal", trimmed).apply()
        _settings.value = _settings.value.copy(primaryGoal = trimmed)
    }

    fun setCompanionName(name: String) {
        val trimmed = name.trim().ifEmpty { "Effort Sprout" }
        prefs.edit().putString("companion_name", trimmed).apply()
        _settings.value = _settings.value.copy(companionName = trimmed)
    }

    fun addCompanionXp(xp: Int) {
        val newXp = (_settings.value.companionXp + xp).coerceAtLeast(0)
        prefs.edit().putInt("companion_xp", newXp).apply()
        _settings.value = _settings.value.copy(companionXp = newXp)
    }

    fun resetCompanionXp() {
        prefs.edit().putInt("companion_xp", 0).apply()
        _settings.value = _settings.value.copy(companionXp = 0)
    }

    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean("has_seen_onboarding", completed).apply()
        _settings.value = _settings.value.copy(hasSeenOnboarding = completed)
    }

    fun clearAllSettings() {
        prefs.edit().clear().apply()
        _settings.value = loadSettings()
    }
}

